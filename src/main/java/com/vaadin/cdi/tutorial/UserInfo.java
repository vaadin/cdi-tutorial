package com.vaadin.cdi.tutorial;

import java.io.Serializable;

import com.vaadin.cdi.UIScoped;

@UIScoped
public class UserInfo implements Serializable {
    private User user;

    public UserInfo() {
        this.user = null;
    }

    public User getUser() {
        return user;
    }

    public String getName() {
        if (user == null) {
            return "anonymous user";
        } else {
            return user.getName();
        }
    }

    public void setUser(User user) {
        this.user = user;
    }
}