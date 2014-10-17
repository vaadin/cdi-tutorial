package com.vaadin.cdi.tutorial;

import javax.inject.Inject;

import com.vaadin.cdi.CDIView;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@CDIView("login")
public class LoginView extends CustomComponent implements View, ClickListener {

    @Inject
    private UserInfo user;

    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;

    private Navigator navigator;

    @Override
    public void enter(ViewChangeEvent event) {

        navigator = getUI().getNavigator();

        usernameField = new TextField("Username");
        passwordField = new PasswordField("Password");
        loginButton = new Button("Login");
        loginButton.addClickListener(this);
        loginButton.setClickShortcut(KeyCode.ENTER);

        VerticalLayout layout = new VerticalLayout();
        setCompositionRoot(layout);
        layout.setSizeFull();
        layout.setMargin(true);
        layout.setSpacing(true);

        layout.addComponent(usernameField);
        layout.addComponent(passwordField);
        layout.addComponent(loginButton);

    }

    @Override
    public void buttonClick(ClickEvent event) {
        // Dummy implementation
        String username = usernameField.getValue();
        String password = passwordField.getValue();
        user.setName(username);
        if (navigator != null) {
            navigator.navigateTo("hello");
        }
    }
}