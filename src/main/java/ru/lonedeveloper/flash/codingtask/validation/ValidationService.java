package ru.lonedeveloper.flash.codingtask.validation;

import ru.lonedeveloper.flash.codingtask.domain.Solution;

public interface ValidationService {

    void validate(final Solution solution) throws SolutionValidationException;
}
