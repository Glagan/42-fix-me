package org.glagan.router.Broker;

import java.net.Socket;

import org.glagan.router.Router;
import org.glagan.router.RouterListener;

public class Listener extends RouterListener {
    public Listener(int port) {
        super(port);
    }

    public org.glagan.core.Client createClient(Socket socket) {
        return new Client(socket);
    }

    @Override
    public void onConnection(Socket socket, org.glagan.core.Client client) {
        super.onConnection(socket, client);
        System.out.println("[" + client.getId() + "] Connected new Broker");
        Router.getInstance().addMarket(client);
    }
}
