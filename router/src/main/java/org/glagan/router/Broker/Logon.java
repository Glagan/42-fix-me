package org.glagan.router.Broker;

import java.util.UUID;

import org.glagan.core.Client;
import org.glagan.core.Dictionary;
import org.glagan.core.Message;
import org.glagan.core.MsgType;
import org.glagan.core.Handler.Handler;
import org.glagan.router.Router;

public class Logon extends Handler {
    public boolean handle(Client client, Message message) {
        if (message.getHeader().getMsgType().equals(MsgType.Logon)) {
            if (client.getId() != null) {
                return false;
            }

            // Reconnect the user if a Token was supplied
            String id = null;
            String token = message.getBody().get(Dictionary.Token);
            boolean reconnected = false;
            if (token != null) {
                id = Router.getInstance().idForToken(token);
            }
            if (id == null) {
                id = UUID.randomUUID().toString();
                token = UUID.randomUUID().toString();
            } else {
                reconnected = true;
            }

            // TODO Check if another user is already connected with this token

            client.setId(id);
            client.send(Message.make(MsgType.Logon).auth(client.getId()).add(Dictionary.Token, token).build());
            Router.getInstance().addBroker(client, token);
            System.out.println("[" + client.getId() + "] Connected new Broker");

            // Send pending messages (non-blocking)
            if (reconnected && Router.getInstance().hasPendingMessages(id)) {
                final String clientId = id;
                new Thread() {
                    @Override
                    public void run() {
                        Message pendingMessage = null;
                        while ((pendingMessage = Router.getInstance().getNextPendingMessage(clientId)) != null) {
                            Router.getInstance().forward(clientId, pendingMessage);
                        }
                    }
                }.start();
            }

            return true;
        }

        return handleNext(client, message);
    }
}
