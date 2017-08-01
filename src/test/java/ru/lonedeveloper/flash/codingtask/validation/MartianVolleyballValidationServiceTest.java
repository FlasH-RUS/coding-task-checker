package ru.lonedeveloper.flash.codingtask.validation;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import ru.lonedeveloper.flash.codingtask.domain.Solution;
import ru.lonedeveloper.flash.codingtask.domain.Task;
import ru.lonedeveloper.flash.codingtask.validation.lang.LanguageService;

@RunWith(MockitoJUnitRunner.class)
public class MartianVolleyballValidationServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private LanguageService languageService;

    @InjectMocks
    private MartianVolleyballValidationService validationService;

    @Test
    public void shouldFailFirstTest() throws Exception {
        // given
        final Task task = new Task();
        task.setTimeLimit(2);
        final Solution solution = new Solution();
        solution.setTask(task);
        when(languageService.run(anyString(), anyString(), anyLong())).thenReturn("1\r\n");

        // expect
        thrown.expect(SolutionValidationException.class);
        thrown.expectMessage("Test 01 failed");

        // when
        validationService.validate(solution);
    }

    @Test
    public void shouldFailSecondTest() throws Exception {
        // given
        final Task task = new Task();
        task.setTimeLimit(2);
        final Solution solution = new Solution();
        solution.setTask(task);
        when(languageService.run(anyString(), anyString(), anyLong())).thenReturn("1\r\n1\r\n5\r\n");

        // expect
        thrown.expect(SolutionValidationException.class);
        thrown.expectMessage("Test 02 failed");

        // when
        validationService.validate(solution);
    }
}
