package org.glagan.router.Handler;

import org.glagan.core.Message;
import org.glagan.core.Handler.Handler;

public class ForwardToMarket extends Handler {
    public boolean handle(Message message) {
        System.out.println("ForwardToMarket on " + message);
        // TODO Check if the Market exists on the router (can be forwarded)
        return false;
    }
}
