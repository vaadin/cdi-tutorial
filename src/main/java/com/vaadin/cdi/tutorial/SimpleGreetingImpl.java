package com.vaadin.cdi.tutorial;

public class SimpleGreetingImpl implements Greeting {

    @Override
    public String getText() {
        return "Hello, World!";
    }
}