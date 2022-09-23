package org.glagan.core.Handler;

import org.glagan.core.Client;
import org.glagan.core.Message;
import org.glagan.core.MsgType;

public class Logon extends Handler {
    protected boolean hasId;

    public Logon() {
        this.hasId = false;
    }

    public boolean handle(Client client, Message message) {
        if (!hasId) {
            if (message.getHeader().getMsgType().equals(MsgType.Logon)) {
                client.setId(message.getHeader().getBeginString());
                System.out.println("Assigned ID " + client.getId());
                hasId = true;
                return true;
            }
            return false;
        }

        return handleNext(client, message);
    }
}
