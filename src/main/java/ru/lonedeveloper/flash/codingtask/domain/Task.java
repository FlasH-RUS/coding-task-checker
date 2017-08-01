package ru.lonedeveloper.flash.codingtask.domain;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class Task {

    /**
     */
    @NotNull
    @Size(min = 3, max = 50)
    private String name;

    /**
     */
    @NotNull
    @DecimalMin("1")
    private long timeLimit;

    /**
     */
    @NotNull
    private String validationServiceClassName;
}
