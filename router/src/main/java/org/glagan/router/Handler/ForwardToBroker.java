package org.glagan.router.Handler;

import org.glagan.core.Dictionary;
import org.glagan.core.Message;
import org.glagan.core.Handler.Handler;
import org.glagan.router.Router;

public class ForwardToBroker extends Handler {
    public boolean handle(String clientId, Message message) {
        System.out.println("[Handler/ForwardToBroker]");

        String expectedFor = message.getBody().get(Dictionary.Broker);
        if (expectedFor != null) {
            Router.getInstance().forward(expectedFor, message);
            return handleNext(clientId, message);
        }

        return false;
    }
}
