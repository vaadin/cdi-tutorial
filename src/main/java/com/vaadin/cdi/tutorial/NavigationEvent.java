package com.vaadin.cdi.tutorial;

public class NavigationEvent {
    private final String navigateTo;

    public NavigationEvent(String navigateTo) {
        this.navigateTo = navigateTo;
    }

    public String getNavigateTo() {
        return navigateTo;
    }

}
