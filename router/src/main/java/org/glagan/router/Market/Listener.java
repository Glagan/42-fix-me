package org.glagan.router.Market;

import java.net.Socket;
import java.util.logging.Handler;

import org.glagan.router.Router;

public class Listener extends org.glagan.router.Listener {
    public Listener(int port) {
        super(port);
    }

    protected Handler chain;

    public void onConnection(Socket socket) {
        Client client = new Client(socket);
        System.out.println("[" + client.getId() + "] Connected new Market");
        Router.getInstance().addMarket(client);
        new Thread(client).start();
    }
}
