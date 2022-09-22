package org.glagan.router;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.glagan.core.Client;

public abstract class Listener implements Runnable {
    protected int port;

    public Listener(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public abstract Client createClient(Socket socket);

    public abstract void onConnection(Socket socket, Client client);

    public void run() {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Listening on port " + port);
            while (true) {
                Socket socket = server.accept();
                Client client = createClient(socket);
                onConnection(socket, client);
                new Thread(client).start();
            }
        } catch (IOException e) {
            System.err.println("Failed to open socket server:");
            e.printStackTrace();
        }
    }

}
