package ru.lonedeveloper.flash.codingtask.web;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.roo.addon.web.mvc.controller.converter.RooConversionService;

import ru.lonedeveloper.flash.codingtask.domain.Task;

/**
 * A central place to register application converters and formatters.
 */
@RooConversionService
public class ApplicationConversionServiceFactoryBean extends FormattingConversionServiceFactoryBean {

    @Override
    protected void installFormatters(final FormatterRegistry registry) {
        super.installFormatters(registry);
        // Register application converters and formatters
    }

    public Converter<Task, String> getTaskToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<ru.lonedeveloper.flash.codingtask.domain.Task, java.lang.String>() {

            @Override
            public String convert(final Task task) {
                return task.getName();
            }
        };
    }
}
