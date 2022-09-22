package org.glagan.core.Handler;

import org.glagan.core.Message;

public class Authentication extends Handler {
    public boolean handle(Message message) {
        System.out.println("Authentication on " + message);
        // TODO Check that the message has an ID
        // TODO Check that the ID corresponds to the sender
        return handleNext(message);
    }
}
