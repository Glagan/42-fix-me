package org.glagan.core.Handler;

import org.glagan.core.Client;
import org.glagan.core.Message;

/**
 * Default handler to use as a boostrap in the chain
 */
public class Validate extends Handler {
    public boolean handle(Client client, Message message) {
        if (!message.isCorrupted()) {
            return handleNext(client, message);
        }

        System.out.println("Corrupted message: " + message.pretty());
        return false;
    }
}
