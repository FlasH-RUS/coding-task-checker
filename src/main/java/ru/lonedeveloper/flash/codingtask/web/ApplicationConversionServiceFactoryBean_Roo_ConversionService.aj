// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package ru.lonedeveloper.flash.codingtask.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import ru.lonedeveloper.flash.codingtask.domain.Solution;
import ru.lonedeveloper.flash.codingtask.domain.SolutionRepository;
import ru.lonedeveloper.flash.codingtask.domain.Task;
import ru.lonedeveloper.flash.codingtask.web.ApplicationConversionServiceFactoryBean;

privileged aspect ApplicationConversionServiceFactoryBean_Roo_ConversionService {
    
    declare @type: ApplicationConversionServiceFactoryBean: @Configurable;
    
    @Autowired
    SolutionRepository ApplicationConversionServiceFactoryBean.solutionRepository;
    
    public Converter<Solution, String> ApplicationConversionServiceFactoryBean.getSolutionToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<ru.lonedeveloper.flash.codingtask.domain.Solution, java.lang.String>() {
            public String convert(Solution solution) {
                return new StringBuilder().append(solution.getIp()).append(' ').append(solution.getAuthor()).append(' ').append(solution.getCode()).append(' ').append(solution.getCreatedAt()).toString();
            }
        };
    }
    
    public Converter<Long, Solution> ApplicationConversionServiceFactoryBean.getIdToSolutionConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, ru.lonedeveloper.flash.codingtask.domain.Solution>() {
            public ru.lonedeveloper.flash.codingtask.domain.Solution convert(java.lang.Long id) {
                return solutionRepository.findOne(id);
            }
        };
    }
    
    public Converter<String, Solution> ApplicationConversionServiceFactoryBean.getStringToSolutionConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, ru.lonedeveloper.flash.codingtask.domain.Solution>() {
            public ru.lonedeveloper.flash.codingtask.domain.Solution convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), Solution.class);
            }
        };
    }
    
    public Converter<Long, Task> ApplicationConversionServiceFactoryBean.getIdToTaskConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, ru.lonedeveloper.flash.codingtask.domain.Task>() {
            public ru.lonedeveloper.flash.codingtask.domain.Task convert(java.lang.Long id) {
                return Task.findTask(id);
            }
        };
    }
    
    public Converter<String, Task> ApplicationConversionServiceFactoryBean.getStringToTaskConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, ru.lonedeveloper.flash.codingtask.domain.Task>() {
            public ru.lonedeveloper.flash.codingtask.domain.Task convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), Task.class);
            }
        };
    }
    
    public void ApplicationConversionServiceFactoryBean.installLabelConverters(FormatterRegistry registry) {
        registry.addConverter(getSolutionToStringConverter());
        registry.addConverter(getIdToSolutionConverter());
        registry.addConverter(getStringToSolutionConverter());
        registry.addConverter(getTaskToStringConverter());
        registry.addConverter(getIdToTaskConverter());
        registry.addConverter(getStringToTaskConverter());
    }
    
    public void ApplicationConversionServiceFactoryBean.afterPropertiesSet() {
        super.afterPropertiesSet();
        installLabelConverters(getObject());
    }
    
}
