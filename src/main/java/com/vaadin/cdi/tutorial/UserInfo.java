package com.vaadin.cdi.tutorial;

import java.io.Serializable;

import com.vaadin.cdi.UIScoped;

@UIScoped
public class UserInfo implements Serializable {
    private String name;

    public UserInfo() {
        this.name = "stranger";
    }

    public UserInfo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}