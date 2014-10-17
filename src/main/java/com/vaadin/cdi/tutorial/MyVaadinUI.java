package com.vaadin.cdi.tutorial;

import javax.inject.Inject;

import com.vaadin.annotations.Theme;
import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

@Theme("valo")
@CDIUI("")
@SuppressWarnings("serial")
public class MyVaadinUI extends UI {

    @Inject
    private CDIViewProvider viewProvider;

    @Override
    protected void init(VaadinRequest request) {
        Navigator navigator = new Navigator(this, this);
        navigator.addProvider(viewProvider);

        navigator.navigateTo("login");

    }

}
