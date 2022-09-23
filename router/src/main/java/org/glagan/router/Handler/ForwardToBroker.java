package org.glagan.router.Handler;

import org.glagan.core.Client;
import org.glagan.core.Dictionary;
import org.glagan.core.Message;
import org.glagan.core.Handler.Handler;
import org.glagan.router.Router;

public class ForwardToBroker extends Handler {
    public boolean handle(Client client, Message message) {
        String expectedFor = message.getBody().get(Dictionary.Broker);
        if (expectedFor != null) {
            if (Router.getInstance().forward(expectedFor, message)) {
                System.out.println("[" + client.getId() + "] Forwaded message to " + expectedFor);
                return handleNext(client, message);
            } else {
                System.out
                        .println("[" + client.getId() + "] Can't forward message to inexisting broker " + expectedFor);
            }
        } else {
            System.out.println("[" + client.getId() + "] Missing target in message to forward");
        }

        return false;
    }
}
