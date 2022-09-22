package org.glagan.router;

import java.net.Socket;

import org.glagan.core.Client;

public abstract class RouterClient extends Client {
    public RouterClient(Socket socket) {
        super(socket);
    }

    @Override
    public void onClose() {
        Router.getInstance().removeClient(this);
        super.onClose();
    }
}
