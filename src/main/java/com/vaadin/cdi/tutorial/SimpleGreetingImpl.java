package com.vaadin.cdi.tutorial;

import javax.enterprise.inject.Default;

@Default
public class SimpleGreetingImpl implements Greeting {

    @Override
    public String getText() {
        return "Hello, World!";
    }
}