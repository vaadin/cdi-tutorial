package com.vaadin.cdi.tutorial;

import java.util.HashMap;
import java.util.Iterator;
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

    public static class Conversation {
        private User user1;
        private User user2;

        public Conversation(User o1, User o2) {
            if (o1.getId() < o2.getId()) {
                user1 = o1;
                user2 = o2;
            } else {
                user1 = o2;
                user2 = o1;
            }
        }

        @Override
        public int hashCode() {
            return (int) (user1.getId() ^ user2.getId());
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Conversation)) {
                return false;
            }
            Conversation c = (Conversation) o;
            return user1.getId() == c.user1.getId()
                    && user2.getId() == c.user2.getId();
        }
    }

    private Map<Conversation, LinkedList<Message>> messageLog = new HashMap<Conversation, LinkedList<Message>>();

    private Map<User, UI> activeUIMap = new HashMap<User, UI>();

    @Inject
    private Event<Message> messageEvent;

    private LinkedList<Message> getMessagesBetween(User user1, User user2) {
        return getMessagesIn(new Conversation(user1, user2));
    }

    private LinkedList<Message> getMessagesIn(Conversation conversation) {
        LinkedList<Message> messages = messageLog.get(conversation);
        if (messages == null) {
            messages = new LinkedList<Message>();
            messageLog.put(conversation, messages);
        }
        return messages;
    }

    @Override
    public List<Message> getLatestMessages(User user1, User user2, int n) {
        LinkedList<Message> messagesBetween = getMessagesBetween(user1, user2);
        Iterator<Message> iterator = messagesBetween.descendingIterator();
        LinkedList<Message> messages = new LinkedList<Message>();
        while (messages.size() < n && iterator.hasNext()) {
            messages.addFirst(iterator.next());
        }
        return messages;
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
        User sender = message.getSender();
        User recipient = message.getRecipient();
        getMessagesBetween(sender, recipient).add(message);

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
