// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package ru.lonedeveloper.flash.codingtask.domain;

import ru.lonedeveloper.flash.codingtask.domain.Task;

privileged aspect Task_Roo_JavaBean {
    
    public String Task.getName() {
        return this.name;
    }
    
    public void Task.setName(String name) {
        this.name = name;
    }
    
    public long Task.getTimeLimit() {
        return this.timeLimit;
    }
    
    public void Task.setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
    }
    
}