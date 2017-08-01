package ru.lonedeveloper.flash.codingtask.validation.lang;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import ru.lonedeveloper.flash.codingtask.validation.SolutionValidationException;

public class JavaLanguageServiceTest {

    private static final String CODE_FOLDER = "code";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private JavaLanguageService javaLanguageService = new JavaLanguageService();

    @After
    public void tearDown() {
        javaLanguageService.cleanup();
    }

    @Test
    public void shouldGetPackage() throws Exception {
        // given
        final String sourceCode = readSource("empty_main_with_pkg.java");

        // when
        final String pkg = javaLanguageService.getPackage(sourceCode);

        // then
        assertThat(pkg, is("com.test"));
    }

    @Test
    public void shouldGetEmptyPackage() throws Exception {
        // given
        final String sourceCode = readSource("empty_main_without_pkg.java");

        // when
        final String pkg = javaLanguageService.getPackage(sourceCode);

        // then
        assertThat(pkg, is(nullValue()));
    }

    @Test
    public void shouldGetClassName() throws Exception {
        // given
        final String sourceCode = readSource("empty_main_with_pkg.java");

        // when
        final String className = javaLanguageService.getClassName(sourceCode);

        // then
        assertThat(className, is("Test"));
    }

    @Test
    public void shouldGetClassNameWithNoSpaceBeforeBrace() throws Exception {
        // given
        final String sourceCode = readSource("empty_main_with_pkg_ugly_format.java");

        // when
        final String className = javaLanguageService.getClassName(sourceCode);

        // then
        assertThat(className, is("Test"));
    }

    @Test
    public void shoudlThrowExceptionWhenNoClassName() throws Exception {
        // given
        final String sourceCode = "This is not a Java source";

        // expect
        thrown.expect(SolutionValidationException.class);
        thrown.expectMessage("Class name not found");

        // when
        javaLanguageService.getClassName(sourceCode);
    }

    @Test
    public void shouldCompile() throws Exception {
        // given
        final String sourceCode = readSource("empty_main_with_pkg.java");

        // when
        final Path compilePath = javaLanguageService.compile(sourceCode);

        // then
        assertThat(compilePath.resolve("com").resolve("test").resolve("Test.class").toFile().exists(), is(true));
    }

    @Test
    public void shouldFailCompilationOnMissinClassName() throws Exception {
        // given
        final String sourceCode = readSource("interface.java");

        // expect
        thrown.expect(SolutionValidationException.class);
        thrown.expectMessage("Class name not found");

        // when
        javaLanguageService.compile(sourceCode);
    }

    @Test
    public void shouldFailCompilationOnCompilationFailures() throws Exception {
        // given
        final String sourceCode = readSource("syntax_error.java");

        // expect
        thrown.expect(SolutionValidationException.class);
        thrown.expectMessage("Compilation error");

        // when
        javaLanguageService.compile(sourceCode);
    }

    @Test
    public void shouldNotRunIfNotCompiled() throws Exception {
        // given
        final String sourceCode = readSource("empty_main_with_pkg.java");

        // expect
        thrown.expect(IllegalStateException.class);

        // when
        javaLanguageService.run(sourceCode, "", 5);
    }

    @Test
    public void shouldReturnExecutedCodeOutput() throws Exception {
        // given
        final String sourceCode = readSource("output_test.java");
        javaLanguageService.compile(sourceCode);

        // when
        final String out = javaLanguageService.run(sourceCode, "", 1);

        // then
        assertThat(out.trim(), is("Test!"));
    }

    @Test
    public void shouldReadProvidedInputWhenExecuted() throws Exception {
        // given
        final String sourceCode = readSource("pipe_one_line.java");
        javaLanguageService.compile(sourceCode);

        // when
        final String out = javaLanguageService.run(sourceCode, String.format("One input line%n"), 1);

        // then
        assertThat(out.trim(), is("One input line"));
    }

    @Test
    public void shouldThrowExceptionWhenStuck() throws Exception {
        // given
        final String sourceCode = readSource("sleep.java");
        javaLanguageService.compile(sourceCode);

        // expect
        thrown.expect(SolutionValidationException.class);
        thrown.expectMessage("Execution time exceeded");

        // when
        javaLanguageService.run(sourceCode, "", 3);
    }

    @Test
    public void shouldThrowExceptionWhenProgramCrashes() throws Exception {
        // given
        final String sourceCode = readSource("exception.java");
        javaLanguageService.compile(sourceCode);

        // expect
        thrown.expect(SolutionValidationException.class);
        thrown.expectMessage("Abnormal process termination");

        // when
        javaLanguageService.run(sourceCode, "", 1);
    }

    @Test
    public void shouldSendMoreDataThanCodeReads() throws Exception {
        // given
        final String sourceCode = readSource("pipe_one_line.java");
        javaLanguageService.compile(sourceCode);
        final StringBuilder hugeInput = new StringBuilder();
        for (int i = 0; i < 10000; ++i) {
            hugeInput.append(String.format("Line %d%n", i));
        }

        // when
        final String result = javaLanguageService.run(sourceCode, hugeInput.toString(), 1);

        // then
        assertThat(result.trim(), is("Line 0"));
    }

    @Test
    public void shouldSendAndReadMoreThanPipeBuffer() throws Exception {
        // given
        final String sourceCode = readSource("pipe_10000_lines.java");
        javaLanguageService.compile(sourceCode);
        final StringBuilder hugeInput = new StringBuilder();
        for (int i = 0; i < 10000; ++i) {
            hugeInput.append(String.format("Line %d%n", i));
        }

        // when
        final String result = javaLanguageService.run(sourceCode, hugeInput.toString(), 1);

        // then
        assertThat(result, is(hugeInput.toString()));
    }

    private String readSource(final String fileName) throws IOException {
        final File inputFile = new File(
                Thread.currentThread().getContextClassLoader().getResource(CODE_FOLDER + "/" + fileName).getFile());
        return FileUtils.readFileToString(inputFile, "UTF-8");
    }
}
