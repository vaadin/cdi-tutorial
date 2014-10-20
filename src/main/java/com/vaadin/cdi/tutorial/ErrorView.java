package com.vaadin.cdi.tutorial;

import javax.inject.Inject;

import com.vaadin.cdi.access.AccessControl;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class ErrorView extends CustomComponent implements View {

    @Inject
    private AccessControl accessControl;

    @Inject
    private javax.enterprise.event.Event<NavigationEvent> navigationEvent;

    @Override
    public void enter(ViewChangeEvent event) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(true);
        layout.setSpacing(true);

        layout.addComponent(new Label(
                "Unfortunately, the page you've requested does not exists."));
        if (accessControl.isUserSignedIn()) {
            layout.addComponent(createChatButton());
        } else {
            layout.addComponent(createLoginButton());
        }
        setCompositionRoot(layout);
    }

    private Button createLoginButton() {
        Button button = new Button("To login page");
        button.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                navigationEvent.fire(new NavigationEvent("login"));
            }
        });
        return button;
    }

    private Button createChatButton() {
        Button button = new Button("Back to the main page");
        button.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                navigationEvent.fire(new NavigationEvent("chat"));
            }
        });
        return button;
    }
}
