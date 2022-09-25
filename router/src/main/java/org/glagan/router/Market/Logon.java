package org.glagan.router.Market;

import java.util.UUID;

import org.glagan.core.Client;
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

            client.setId(UUID.randomUUID().toString());
            client.send(Message.make(MsgType.Logon).auth(client.getId()).build());
            Router.getInstance().addMarket(client);
            System.out.println("[" + client.getId() + "] Connected new Market");
            return true;
        }

        return handleNext(client, message);
    }
}
