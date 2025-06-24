package com.ccz.department.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    
    boolean admin() default false;
    boolean manageUsers() default false;
    boolean viewSalary() default false;
    boolean manageDepartments() default false;
    boolean manageEmployees() default false;
    boolean manageProjects() default false;
    
    boolean viewUserList() default false;
    
    boolean viewUserDetails() default false;
    
    boolean modifyUser() default false;
}
