package org.glagan.router.Handler;

import org.glagan.core.Dictionary;
import org.glagan.core.Message;
import org.glagan.core.Handler.Handler;
import org.glagan.router.Router;

public class ForwardToBroker extends Handler {
    public boolean handle(String clientId, Message message) {
        String expectedFor = message.getBody().get(Dictionary.Broker);
        if (expectedFor != null) {
            if (Router.getInstance().forward(expectedFor, message)) {
                System.out.println("[" + clientId + "] Forwaded message to " + expectedFor);
                return handleNext(clientId, message);
            } else {
                System.out.println("[" + clientId + "] Can't forward message to inexisting broker " + expectedFor);
            }
        }

        return false;
    }
}
