package ru.lonedeveloper.flash.codingtask.validation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.lonedeveloper.flash.codingtask.domain.Solution;
import ru.lonedeveloper.flash.codingtask.validation.lang.LanguageService;

/**
 * Validation service for a "Martian Volleyball" task (http://www.russiancodecup.ru/en/tasks/round/61/).<br>
 * Technically the validation should be the same for all tasks that have one and only correct answer for every input, so should
 * be later extended this way.
 */
@Service
public class MartianVolleyballValidationService implements ValidationService {

    private static final String TASK_FOLDER = "tasks/martian_volleyball";

    private final LanguageService languageService;

    @Autowired
    public MartianVolleyballValidationService(final LanguageService languageService) {
        this.languageService = languageService;
    }

    @Override
    public void validate(final Solution solution) throws SolutionValidationException {
        try {
            languageService.prepare(solution.getCode());

            for (final String testName : listTests()) {
                final File inputFile = new File(
                        Thread.currentThread().getContextClassLoader().getResource(TASK_FOLDER + "/" + testName).getFile());
                final File outputFile = new File(
                        Thread.currentThread()
                                .getContextClassLoader()
                                .getResource(TASK_FOLDER + "/" + testName + ".a")
                                .getFile());

                final String inputStr = FileUtils.readFileToString(inputFile, "UTF-8");
                final String outputStr = FileUtils.readFileToString(outputFile, "UTF-8");

                final String realOutput = languageService.run(solution.getCode(), inputStr, solution.getTask().getTimeLimit());

                if (!realOutput.trim().equals(outputStr.trim())) {
                    throw new SolutionValidationException("Test " + testName + " failed");
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Unexpected error while validating solution!", ex);
        } finally {
            languageService.cleanup(solution.getCode());
        }
    }

    private List<String> listTests() {
        final List<String> testNames = new ArrayList<>();

        final File taskFolder = new File(Thread.currentThread().getContextClassLoader().getResource(TASK_FOLDER).getFile());
        for (final String fileName : taskFolder.list(new RegexFileFilter("\\d+"))) {
            testNames.add(fileName);
        }

        Collections.sort(testNames);

        return testNames;
    }

}
