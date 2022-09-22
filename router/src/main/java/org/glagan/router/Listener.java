package org.glagan.router;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.glagan.core.Handler.Handler;

public class Listener implements Runnable {
    protected int port;
    protected Handler chain;
    protected List<Client> clients;

    public Listener(int port, Handler chain) {
        this.port = port;
        this.chain = chain;
        this.clients = new ArrayList<Client>();
    }

    public void run() {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Listening on port " + port);
            while (true) {
                Socket socket = server.accept();
                System.out.println("Accepted new client on port " + socket.getPort());
                Client client = new Client(socket, chain.clone());
                clients.add(client);
                new Thread(client).start();
            }
        } catch (IOException e) {
            System.err.println("Failed to open socket server:");
            e.printStackTrace();
        }

    }
}
