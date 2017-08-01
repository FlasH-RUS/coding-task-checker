package ru.lonedeveloper.flash.codingtask.validation.lang;

import ru.lonedeveloper.flash.codingtask.validation.SolutionValidationException;

public interface LanguageService {

    void prepare(final String sourceCode) throws SolutionValidationException;

    String run(final String sourceCode, final String input, final long timeoutSeconds) throws SolutionValidationException;

    void cleanup(final String sourceCode);
}
