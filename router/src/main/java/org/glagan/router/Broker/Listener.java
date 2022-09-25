package org.glagan.router.Broker;

import java.net.Socket;

public class Listener extends org.glagan.router.Listener {
    public Listener(int port) {
        super(port);
    }

    public org.glagan.core.Client createClient(Socket socket) {
        return new Client(socket);
    }
}
