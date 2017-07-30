package ru.lonedeveloper.flash.codingtask.web;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.lonedeveloper.flash.codingtask.domain.Task;

@RequestMapping("/tasks")
@Controller
@RooWebScaffold(path = "tasks", formBackingObject = Task.class)
public class TaskController {
}
