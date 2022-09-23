package org.glagan.broker.Handler;

import org.glagan.core.Client;
import org.glagan.core.Message;
import org.glagan.core.Handler.Handler;

public class Executed extends Handler {
    public Executed() {
    }

    public boolean handle(Client client, Message message) {
        return false;
    }
}
