package ru.lonedeveloper.flash.codingtask.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.lonedeveloper.flash.codingtask.domain.Solution;
import ru.lonedeveloper.flash.codingtask.domain.SolutionRepository;
import ru.lonedeveloper.flash.codingtask.domain.Task;
import ru.lonedeveloper.flash.codingtask.validation.SolutionValidationException;
import ru.lonedeveloper.flash.codingtask.validation.ValidationService;

@RequestMapping("/solutions")
@Controller
@RooWebScaffold(path = "solutions", formBackingObject = Solution.class, delete = false, update = false)
public class SolutionController {

    private static final Logger log = LoggerFactory.getLogger(SolutionController.class);

    private static final String AUTHOR_COOKIE_NAME = "ctc_author";

    private final SolutionRepository solutionRepository;
    private final ApplicationContext context;

    @Autowired
    public SolutionController(final SolutionRepository solutionRepository, final ApplicationContext context) {
        this.solutionRepository = solutionRepository;
        this.context = context;
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(
            @Valid final Solution solution,
            final BindingResult bindingResult,
            final Model uiModel,
            final HttpServletRequest httpServletRequest,
            final HttpServletResponse response) {
        if (isTaskAlreadySolved(httpServletRequest, solution)) {
            bindingResult.addError(new ObjectError("code", "You have already solved this task!"));
        }

        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, solution);
            return "solutions/create";
        }
        uiModel.asMap().clear();
        solution.setIp(httpServletRequest.getRemoteAddr());
        solutionRepository.save(solution);

        validateSolution(solution);

        setAuthorCookie(response, solution.getAuthor());

        return "redirect:/solutions/" + encodeUrlPathSegment(solution.getId().toString(), httpServletRequest);
    }

    private boolean isTaskAlreadySolved(final HttpServletRequest request, final Solution newSolution) {
        return solutionRepository.countByIpAndTaskAndSuccessful(request.getRemoteAddr(), newSolution.getTask(), true) > 0;
    }

    private void validateSolution(final Solution solution) {
        final ValidationService validationService = resolveValidationService(solution);
        try {
            validationService.validate(solution);
            solution.setResults("PASSED");
            solution.setSuccessful(true);
        } catch (SolutionValidationException ex) {
            solution.setSuccessful(false);
            solution.setResults("FAILED: " + ex.getMessage());
        } catch (Exception ex) {
            solution.setSuccessful(false);
            solution.setResults("Unexpected error: " + ex.getMessage());
            log.error("Unexpected error while validating solution {}", solution.getId(), ex);
        } finally {
            solutionRepository.save(solution);
        }
    }

    @SuppressWarnings("unchecked")
    private ValidationService resolveValidationService(final Solution solution) {
        try {
            @SuppressWarnings("rawtypes")
            final Class validationServiceClass = Class.forName(solution.getTask().getValidationServiceClassName());
            return context.getBean(validationServiceClass);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Class " + solution.getTask().getValidationServiceClassName() + " not found!");
        }
    }

    private void setAuthorCookie(final HttpServletResponse response, final String authorName) {
        final Cookie cookie = new Cookie(AUTHOR_COOKIE_NAME, authorName);
        cookie.setMaxAge(3600);

        response.addCookie(cookie);
    }

    /**
     * @return {@code null} in case cookie doesn't exist; its value otherwise
     */
    private String getAuthorCookie(final HttpServletRequest request) {
        for (final Cookie cookie : request.getCookies()) {
            if (AUTHOR_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(final Model uiModel, final HttpServletRequest request) {
        populateEditForm(uiModel, new Solution(), getAuthorCookie(request));
        final List<String[]> dependencies = new ArrayList<>();
        if (Task.countTasks() == 0) {
            dependencies.add(new String[] { "task", "tasks" });
        }
        uiModel.addAttribute("dependencies", dependencies);
        return "solutions/create";
    }

    void populateEditForm(final Model uiModel, final Solution solution, final String author) {
        if (author != null) {
            solution.setAuthor(author);
        }
        uiModel.addAttribute("solution", solution);
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("tasks", Task.findAllTasks());
    }

    @RequestMapping(produces = "text/html")
    public String list(
            @RequestParam(value = "sortFieldName", required = false) final String sortFieldName,
            @RequestParam(value = "sortOrder", required = false) final String sortOrder,
            final Model uiModel,
            final HttpServletRequest request) {
        final Sort sort = createSort(sortFieldName, sortOrder);
        final List<Solution> solutions = findPermittedSolutions(request, sort);
        uiModel.addAttribute("solutions", solutions);
        addDateTimeFormatPatterns(uiModel);
        return "solutions/list";
    }

    private Sort createSort(final String sortFieldName, final String sortOrder) {
        final Direction sortDirection = Direction.fromStringOrNull(sortOrder);
        if (sortFieldName == null || sortFieldName.length() == 0 || sortDirection == null) {
            return null;
        }

        return new Sort(sortDirection, sortFieldName);
    }

    private List<Solution> findPermittedSolutions(final HttpServletRequest request, final Sort sort) {
        if (request.isUserInRole("ROLE_ADMIN")) {
            return solutionRepository.findAll(sort);
        } else {
            return solutionRepository.findByIp(request.getRemoteAddr(), sort);
        }
    }
}
