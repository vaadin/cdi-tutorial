package com.vaadin.cdi.tutorial;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

@Alternative
public class UserGreetingImpl implements Greeting {

    @Inject
    private UserInfo user;

    @Override
    public String getText() {
        return "Hello, " + user.getName() + "!";
    }

}