package org.glagan.broker.Handler;

import org.glagan.core.Client;
import org.glagan.core.Message;
import org.glagan.core.Handler.Handler;

public class Rejected extends Handler {
    public Rejected() {
    }

    public boolean handle(Client client, Message message) {
        return false;
    }
}
