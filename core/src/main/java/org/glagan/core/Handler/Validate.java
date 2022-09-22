package org.glagan.core.Handler;

import org.glagan.core.Message;

/**
 * Default handler to use as a boostrap in the chain
 */
public class Validate extends Handler {
    public boolean handle(String clientId, Message message) {
        System.out.println("[Handler/Validate]");

        if (!message.isCorrupted()) {
            return handleNext(clientId, message);
        }

        System.out.println("Corrupted message: " + message.pretty());
        return false;
    }
}
