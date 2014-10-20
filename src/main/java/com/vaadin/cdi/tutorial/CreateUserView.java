package com.vaadin.cdi.tutorial;

import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

import com.vaadin.cdi.CDIView;
import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@CDIView
@RolesAllowed({ "admin" })
public class CreateUserView extends CustomComponent implements View {

    @Inject
    UserDAO userDAO;

    private static final AtomicLong ID_FACTORY = new AtomicLong(3);

    @Override
    public void enter(ViewChangeEvent event) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.addComponent(new Label("Create new user"));

        final BeanFieldGroup<User> fieldGroup = new BeanFieldGroup<User>(
                User.class);
        layout.addComponent(fieldGroup.buildAndBind("firstName"));
        layout.addComponent(fieldGroup.buildAndBind("lastName"));
        layout.addComponent(fieldGroup.buildAndBind("username"));
        layout.addComponent(fieldGroup.buildAndBind("password"));
        layout.addComponent(fieldGroup.buildAndBind("email"));

        fieldGroup.getField("username").addValidator(new Validator() {

            @Override
            public void validate(Object value) throws InvalidValueException {
                String username = (String) value;
                if (username.isEmpty()) {
                    throw new InvalidValueException("Username cannot be empty");
                }

                if (userDAO.getUserBy(username) != null) {
                    throw new InvalidValueException("Username is taken");
                }
            }

        });

        fieldGroup.setItemDataSource(new User(ID_FACTORY.incrementAndGet(), "",
                "", "", "", "", false));

        final Label messageLabel = new Label();
        layout.addComponent(messageLabel);

        fieldGroup.addCommitHandler(new CommitHandler() {
            @Override
            public void preCommit(CommitEvent commitEvent)
                    throws CommitException {
            }

            @Override
            public void postCommit(CommitEvent commitEvent)
                    throws CommitException {
                userDAO.saveUser(fieldGroup.getItemDataSource().getBean());
                fieldGroup.setItemDataSource(new User(ID_FACTORY
                        .incrementAndGet(), "", "", "", "", "", false));
            }
        });
        Button commitButton = new Button("Create");
        commitButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    fieldGroup.commit();
                    messageLabel.setValue("User created");
                } catch (CommitException e) {
                    messageLabel.setValue(e.getMessage());
                }
            }
        });

        layout.addComponent(commitButton);
        setCompositionRoot(layout);
    }

}
