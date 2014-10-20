package com.vaadin.cdi.tutorial;

import java.io.Serializable;

import javax.enterprise.event.Observes;

public interface NavigationService extends Serializable {

    public void onNavigationEvent(@Observes NavigationEvent event);
}
