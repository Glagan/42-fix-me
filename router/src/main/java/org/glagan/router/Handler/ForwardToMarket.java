package org.glagan.router.Handler;

import org.glagan.core.Dictionary;
import org.glagan.core.Message;
import org.glagan.core.Handler.Handler;
import org.glagan.router.Router;

public class ForwardToMarket extends Handler {
    public boolean handle(String clientId, Message message) {
        System.out.println("[Handler/ForwardToMarket]");

        String expectedFor = message.getBody().get(Dictionary.Market);
        if (expectedFor != null) {
            if (Router.getInstance().forward(expectedFor, message)) {
                System.out.println("[" + clientId + "] Forwaded message to " + expectedFor);
                return handleNext(clientId, message);
            } else {
                System.out.println("[" + clientId + "] Can't forward message to inexisting market " + expectedFor);
            }
        }

        return false;
    }
}
