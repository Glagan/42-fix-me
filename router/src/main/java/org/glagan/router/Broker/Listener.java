package org.glagan.router.Broker;

import java.net.Socket;

import org.glagan.router.Servers;

public class Listener extends org.glagan.router.Listener {
    public Listener(Servers servers, int port) {
        super(servers, port);
    }

    public org.glagan.core.Client createClient(Socket socket) {
        return new Client(socket);
    }
}
