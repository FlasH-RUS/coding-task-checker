package ru.lonedeveloper.flash.codingtask.validation.lang;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import ru.lonedeveloper.flash.codingtask.validation.SolutionValidationException;

@Service
public class JavaLanguageService implements LanguageService {

    private static final Logger log = LoggerFactory.getLogger(JavaLanguageService.class);

    private static final long COMPILATION_TIMEOUT_SEC = 5;
    private static final String TEMP_FOLDER_PREFIX = "ctc_jls_";
    private static final Path JAVA_BIN_PATH = Paths.get(System.getenv("JAVA_HOME"), "bin");

    private static final Map<String, Path> compiled = new ConcurrentHashMap<>();

    @Override
    public void prepare(final String sourceCode) throws SolutionValidationException {
        compile(sourceCode);
    }

    @Override
    public String run(final String sourceCode, final String input, final long timeoutSeconds)
            throws SolutionValidationException {
        final Path compilePath = compiled.get(sourceCode);
        if (compilePath == null) {
            throw new IllegalStateException("An attempt to run uncompiled code was made!");
        }

        synchronized (compilePath) {
            final String fullClassName = getFullClassName(sourceCode);

            final String command = String.format(
                    "\"%s\" -cp \"%s\" %s",
                    JAVA_BIN_PATH.resolve("java").toAbsolutePath().toString(),
                    compilePath.toAbsolutePath().toString(),
                    fullClassName);

            try {
                final Process javaProcess = Runtime.getRuntime().exec(command);
                final StringWriter processOutput = new StringWriter();
                startOutputCollectorThread(javaProcess, processOutput);
                startInputProviderThread(javaProcess, input);

                waitFor(javaProcess, timeoutSeconds);
                if (javaProcess.exitValue() != 0) {
                    throw new SolutionValidationException("Abnormal process termination");
                }

                return processOutput.toString();
            } catch (IOException ex) {
                throw new RuntimeException("Unexpected error while running code", ex);
            }
        }
    }

    private void startOutputCollectorThread(final Process process, final StringWriter processOutput) {
        new Thread() {

            @Override
            public void run() {
                try {
                    IOUtils.copy(process.getInputStream(), processOutput);
                } catch (IOException ex) {
                    log.warn("Error while collecting process output", ex);
                }
            }
        }.start();
    }

    private void startInputProviderThread(final Process process, final String input) {
        new Thread() {

            @Override
            public void run() {
                try {
                    final ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes("UTF-8"));
                    IOUtils.copy(inputStream, process.getOutputStream());
                    process.getOutputStream().flush();
                } catch (IOException ex) {
                    log.warn("Error while writing process input", ex);
                }
            }
        }.start();
    }

    protected Path compile(final String sourceCode) throws SolutionValidationException {
        try {
            final Path compilePath = Files.createTempDirectory(TEMP_FOLDER_PREFIX);
            compilePath.toFile().deleteOnExit();

            final File sourceFile = writeSourceFile(compilePath, sourceCode);

            executeCompileCommand(compilePath, sourceFile);

            compiled.put(sourceCode, compilePath);

            return compilePath;
        } catch (IOException ex) {
            throw new RuntimeException("Unexpected error while compiling source!", ex);
        }
    }

    private File writeSourceFile(final Path compilePath, final String sourceCode)
            throws IOException, SolutionValidationException {
        final String className = getClassName(sourceCode);

        final File sourceFile = compilePath.resolve(className + ".java").toFile();
        sourceFile.createNewFile();
        try (FileWriter fw = new FileWriter(sourceFile)) {
            fw.write(sourceCode);
        }

        return sourceFile;
    }

    private void executeCompileCommand(final Path compilePath, final File sourceFile)
            throws SolutionValidationException, IOException {
        final String command = String.format(
                "\"%s\" -d \"%s\" \"%s\"",
                JAVA_BIN_PATH.resolve("javac").toAbsolutePath().toString(),
                compilePath.toAbsolutePath().toString(),
                sourceFile.getAbsolutePath());
        final Process compileProcess = Runtime.getRuntime().exec(command);
        waitFor(compileProcess, COMPILATION_TIMEOUT_SEC);
        if (compileProcess.exitValue() != 0) {
            logProcessOutput(compileProcess);
            throw new SolutionValidationException("Compilation error");
        }
    }

    private void waitFor(final Process process, final long seconds) throws SolutionValidationException {
        synchronized (process) {
            new Thread() {

                @Override
                public void run() {
                    try {
                        process.waitFor();
                    } catch (InterruptedException e) {
                        log.warn("Process interrupted!");
                    }

                    synchronized (process) {
                        process.notify();
                    }
                }
            }.start();

            try {
                process.wait(seconds * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException("Process interrupted!");
            }
        }

        try {
            process.exitValue();
        } catch (IllegalThreadStateException ex) {
            process.destroy();
            throw new SolutionValidationException("Execution time exceeded");
        }
    }

    private void logProcessOutput(final Process process) throws IOException {
        log.info("Compilations failed: {}", IOUtils.toString(process.getErrorStream()));
    }

    @Override
    public void cleanup(final String sourceCode) {
        compiled.remove(sourceCode);
    }

    /**
     * Method for unit tests only.
     */
    protected void cleanup() {
        compiled.clear();
    }

    protected String getPackage(final String sourceCode) throws SolutionValidationException {
        final Pattern packagePattern = Pattern.compile("package\\s+([\\w.]+)");
        final Matcher packageMatcher = packagePattern.matcher(sourceCode);

        return packageMatcher.find() ? packageMatcher.group(1) : null;
    }

    protected String getClassName(final String sourceCode) throws SolutionValidationException {
        final Pattern classNamePattern = Pattern.compile("public\\s+class\\s+([\\w]+)");
        final Matcher classNameMatcher = classNamePattern.matcher(sourceCode);
        if (classNameMatcher.find()) {
            return classNameMatcher.group(1);

        } else {
            log.warn("Unable to find class name for \"\"" + sourceCode);
            throw new SolutionValidationException("Class name not found");
        }
    }

    private String getFullClassName(final String sourceCode) throws SolutionValidationException {
        final String pkg = getPackage(sourceCode);
        final String className = getClassName(sourceCode);

        return pkg == null ? className : pkg + '.' + className;
    }

}
