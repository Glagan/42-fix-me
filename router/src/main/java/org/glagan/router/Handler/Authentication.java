package org.glagan.router.Handler;

import org.glagan.core.Message;
import org.glagan.core.Handler.Handler;

public class Authentication extends Handler {
    public boolean handle(String clientId, Message message) {
        if (clientId.equals(message.getHeader().getBeginString())) {
            return handleNext(clientId, message);
        }

        System.out.println("Unauthorized message with BeginString(8)=" + message.getHeader().getBeginString()
                + " (expected " + clientId + ")");
        return false;
    }
}
