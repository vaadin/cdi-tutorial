package com.vaadin.cdi.tutorial;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.cdi.UIScoped;

@UIScoped
public class UserInfo implements Serializable {
    private User user;

    private List<String> roles = new LinkedList<String>();

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
        roles.clear();
        if (user != null) {
            roles.add("user");
            if (user.isAdmin()) {
                roles.add("admin");
            }
        }
    }

    public List<String> getRoles() {
        return roles;
    }
}