// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package ru.lonedeveloper.flash.codingtask.web;

import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import org.joda.time.format.DateTimeFormat;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;
import ru.lonedeveloper.flash.codingtask.domain.Solution;
import ru.lonedeveloper.flash.codingtask.domain.Task;
import ru.lonedeveloper.flash.codingtask.web.SolutionController;

privileged aspect SolutionController_Roo_Controller {
    
    @RequestMapping(value = "/{id}", produces = "text/html")
    public String SolutionController.show(@PathVariable("id") Long id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("solution", solutionRepository.findOne(id));
        uiModel.addAttribute("itemId", id);
        return "solutions/show";
    }
    
    void SolutionController.addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("solution_createdat_date_format", DateTimeFormat.patternForStyle("-M", LocaleContextHolder.getLocale()));
    }
    
    void SolutionController.populateEditForm(Model uiModel, Solution solution) {
        uiModel.addAttribute("solution", solution);
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("tasks", Task.findAllTasks());
    }
    
    String SolutionController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
    
}