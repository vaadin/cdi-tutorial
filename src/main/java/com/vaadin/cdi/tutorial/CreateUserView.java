package com.vaadin.cdi.tutorial;

import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

import com.vaadin.cdi.CDIView;
import com.vaadin.data.BeanBinder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.ValidationResult;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@CDIView
@RolesAllowed({ "admin" })
public class CreateUserView extends CustomComponent implements View {

    @Inject
    UserDAO userDAO;
    
    private TextField firstName = new TextField();
    private TextField lastName = new TextField();
    private TextField username = new TextField();
    private TextField password = new TextField();
    private TextField email = new TextField();

    private static final AtomicLong ID_FACTORY = new AtomicLong(3);

    @Override
    public void enter(ViewChangeEvent event) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.addComponent(new Label("Create new user"));

        final BeanBinder<User> binder = new BeanBinder<>(User.class);
        layout.addComponent(firstName);
        layout.addComponent(lastName);
        layout.addComponent(username);
        layout.addComponent(password);
        layout.addComponent(email);

        binder.forField(username).withValidator((value, context) -> {
            if (value.isEmpty()) {
                return ValidationResult.error("Username cannot be empty");
            }

            if (userDAO.getUserBy(value) != null) {
            	return ValidationResult.error("Username is taken");
            }
            return ValidationResult.ok();
        }).bind("username");

        final Label messageLabel = new Label();
        layout.addComponent(messageLabel);

        Button commitButton = new Button("Create");
		commitButton.addClickListener(clickEvent -> {
			binder.getBean().ifPresent(user -> {
				try {
					binder.writeBean(user);
					userDAO.saveUser(user);
					binder.setBean(new User(ID_FACTORY.incrementAndGet(), "", "", "", "", "", false));
					messageLabel.setValue("User created");
				} catch (ValidationException e) {
					messageLabel.setValue(e.getMessage());
				}
			});
		});

        // bind remaining fields
        binder.bindInstanceFields(this);
        binder.setBean(new User(ID_FACTORY.incrementAndGet(), "",
                "", "", "", "", false));

        layout.addComponent(commitButton);
        setCompositionRoot(layout);
    }

}
