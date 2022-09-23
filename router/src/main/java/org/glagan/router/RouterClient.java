package org.glagan.router;

import java.net.Socket;

import org.glagan.core.Client;
import org.glagan.core.Message;

public abstract class RouterClient extends Client {
    public RouterClient(Socket socket) {
        super(socket);
    }

    @Override
    public void onClose() {
        Router.getInstance().removeClient(this);
        super.onClose();
    }

    @Override
    public void onMessage(Message message) {
    }
}
