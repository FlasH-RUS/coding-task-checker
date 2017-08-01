package ru.lonedeveloper.flash.codingtask.validation;

import org.springframework.stereotype.Service;

import ru.lonedeveloper.flash.codingtask.domain.Solution;

@Service
public class TestValidationService implements ValidationService {

    @Override
    public void validate(final Solution solution) throws SolutionValidationException {
        if (!solution.getCode().startsWith("OK")) {
            throw new SolutionValidationException("Not OK!");
        }
    }

}
