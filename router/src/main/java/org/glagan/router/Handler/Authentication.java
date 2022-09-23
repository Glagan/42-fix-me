package org.glagan.router.Handler;

import org.glagan.core.Client;
import org.glagan.core.Message;
import org.glagan.core.Handler.Handler;

public class Authentication extends Handler {
    public boolean handle(Client client, Message message) {
        if (client.getId().equals(message.getHeader().getBeginString())) {
            return handleNext(client, message);
        }

        System.out.println("Unauthorized message with BeginString(8)=" + message.getHeader().getBeginString()
                + " (expected " + client + ")");
        return false;
    }
}
