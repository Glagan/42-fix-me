package org.glagan.router;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import org.glagan.core.Client;

public abstract class Listener implements Runnable {
    protected Servers servers;
    protected int port;
    protected ServerSocket server;

    public Listener(Servers servers, int port) {
        this.servers = servers;
        this.port = port;
        this.server = null;
    }

    public int getPort() {
        return port;
    }

    public abstract Client createClient(Socket socket);

    public void run() {
        try {
            server = new ServerSocket(port);
            System.out.println("Listening on port " + port);
            while (true) {
                Socket socket = server.accept();
                Client client = createClient(socket);
                Router.getInstance().addPending(client);
                new Thread(client).start();
            }
        } catch (BindException e) {
            System.err.println("\u001B[31mFailed to bind to port\u001B[0m " + port);
        } catch (IOException e) {
            System.err.println("\u001B[31mFailed to accept client (port " + port + "): \u001B[0m" + e.getMessage());
        } finally {
            servers.closeAll();
        }
    }

    public void close() {
        if (server != null) {
            try {
                server.close();
            } catch (IOException e) {
                // Ignore excepted errors
            }
        }
    }
}
