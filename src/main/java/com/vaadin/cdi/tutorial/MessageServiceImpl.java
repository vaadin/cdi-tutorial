package com.vaadin.cdi.tutorial;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.vaadin.ui.UI;

@ApplicationScoped
public class MessageServiceImpl implements MessageService {

    private Map<User, UI> activeUIMap = new HashMap<User, UI>();

    @Inject
    private Event<Message> messageEvent;

    @Override
    public List<Message> getLatestMessages(User user1, User user2, int n) {
        // TODO Dummy implementation
        return new LinkedList<Message>();
    }

    @Override
    public void registerParticipant(User user, UI ui) {
        activeUIMap.put(user, ui);
    }

    @Override
    public void unregisterParticipant(User user) {
        activeUIMap.remove(user);
    }

    private void observeMessage(@Observes @OriginalAuthor final Message message) {
        UI recipientUI = activeUIMap.get(message.getRecipient());
        if (recipientUI != null && recipientUI.isAttached()
                && !recipientUI.isClosing()) {
            recipientUI.access(new Runnable() {

                @Override
                public void run() {
                    messageEvent.fire(message);
                }
            });
        }
    }
}
