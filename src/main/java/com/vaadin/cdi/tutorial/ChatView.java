package com.vaadin.cdi.tutorial;

import static javax.enterprise.event.Reception.IF_EXISTS;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.access.AccessControl;
import com.vaadin.cdi.internal.Conventions;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@CDIView
public class ChatView extends CustomComponent implements View {

    @Inject
    private UserDAO userDAO;

    @Inject
    private UserInfo userInfo;

    private User targetUser;

    private Layout messageLayout;

    @Inject
    private MessageService messageService;

    @Inject
    @OriginalAuthor
    private javax.enterprise.event.Event<Message> messageEvent;

    @Inject
    private javax.enterprise.event.Event<NavigationEvent> navigationEvent;

    @Inject
    private AccessControl accessControl;

    private static final int MAX_MESSAGES = 16;

    @Override
    public void enter(ViewChangeEvent event) {
        String parameters = event.getParameters();
        Layout layout;
        if (parameters.isEmpty()) {
            targetUser = null;
            layout = buildUserSelectionLayout();
        } else {
            targetUser = userDAO.getUserBy(parameters);
            if (targetUser == null) {
                layout = buildErrorLayout();
            } else {
                layout = buildUserLayout();
            }
        }
        setCompositionRoot(layout);
        messageService.registerParticipant(userInfo.getUser(), getUI());
    }

    @Override
    public void detach() {
        messageService.unregisterParticipant(userInfo.getUser());
        super.detach();
    }

    private Layout buildErrorLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.addComponent(new Label("No such user"));
        layout.addComponent(generateBackButton());
        return layout;
    }

    private Layout buildUserLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.addComponent(new Label("Talking to " + targetUser.getName()));
        layout.addComponent(generateBackButton());
        layout.addComponent(buildChatLayout());
        return layout;
    }

    private Component buildChatLayout() {
        VerticalLayout chatLayout = new VerticalLayout();
        chatLayout.setSizeFull();
        chatLayout.setSpacing(true);
        messageLayout = new VerticalLayout();
        messageLayout.setWidth("100%");
        for (Message message : messageService.getLatestMessages(
                userInfo.getUser(), targetUser, MAX_MESSAGES)) {
            observeMessage(message);
        }
        final TextField messageField = new TextField();
        messageField.setWidth("100%");
        final Button sendButton = new Button("Send");
        sendButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                String message = messageField.getValue();
                if (!message.isEmpty()) {
                    messageField.setValue("");
                    messageEvent.fire(new Message(userInfo.getUser(),
                            targetUser, message));
                }
            }
        });
        sendButton.setClickShortcut(KeyCode.ENTER);
        Panel messagePanel = new Panel(messageLayout);
        messagePanel.setHeight("400px");
        messagePanel.setWidth("100%");
        chatLayout.addComponent(messagePanel);
        HorizontalLayout entryLayout = new HorizontalLayout(sendButton,
                messageField);
        entryLayout.setWidth("100%");
        entryLayout.setExpandRatio(messageField, 1);
        entryLayout.setSpacing(true);
        chatLayout.addComponent(entryLayout);
        return chatLayout;
    }

    private Layout buildUserSelectionLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.addComponent(new Label("Select user to talk to:"));
        for (User user : userDAO.getUsers()) {
            if (user.equals(userInfo.getUser())) {
                continue;
            }
            layout.addComponent(generateUserSelectionButton(user));
        }
        if (accessControl.isUserInRole("admin")) {
            layout.addComponent(new Label("Admin:"));
            Button createUserButton = new Button("Create user");
            createUserButton.addClickListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    navigationEvent.fire(new NavigationEvent("create-user"));
                }
            });
            layout.addComponent(createUserButton);
        }
        return layout;
    }

    private Button generateBackButton() {
        Button button = new Button("Back");
        button.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                navigationEvent.fire(new NavigationEvent(Conventions
                        .deriveMappingForView(ChatView.class)));
            }
        });
        return button;
    }

    private Button generateUserSelectionButton(final User user) {
        Button button = new Button(user.getName());
        button.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                navigationEvent.fire(new NavigationEvent(Conventions
                        .deriveMappingForView(ChatView.class)
                        + "/"
                        + user.getUsername()));
            }
        });
        return button;
    }

    private void observeMessage(
            @Observes(notifyObserver = IF_EXISTS) @Any Message message) {
        User currentUser = userInfo.getUser();
        if (targetUser != null && message.involves(currentUser, targetUser)) {
            if (messageLayout != null) {
                if (messageLayout.getComponentCount() >= MAX_MESSAGES) {
                    messageLayout.removeComponent(messageLayout
                            .getComponentIterator().next());
                }
                messageLayout.addComponent(new Label(message.toString()));
            }
        }
    }

}