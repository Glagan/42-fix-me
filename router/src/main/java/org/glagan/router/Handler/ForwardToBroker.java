package org.glagan.router.Handler;

import org.glagan.core.Message;
import org.glagan.core.Handler.Handler;

public class ForwardToBroker extends Handler {
    public boolean handle(Message message) {
        System.out.println("ForwardToBroker on " + message);
        // TODO Check if the Broker exists on the router (can be forwarded)
        return false;
    }
}
