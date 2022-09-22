package org.glagan.router;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

import org.glagan.core.Message;
import org.glagan.core.MsgType;

public abstract class Client implements Runnable {
    protected Socket socket;
    protected String id;

    public Client(Socket socket) {
        this.socket = socket;
        this.id = UUID.randomUUID().toString();
        if (!socket.isClosed()) {
            send(Message.make(MsgType.Logon).auth(this.id).build());
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public String getId() {
        return id;
    }

    public abstract void onMessage(Message message);

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String text;
            do {
                text = reader.readLine();
                if (text != null) {
                    System.out.println("received text: " + text);
                    Message message = Message.fromString(text);
                    onMessage(message);
                }
            } while (text != null);

            System.out.println("Closing client port " + socket.getPort());
            Router.getInstance().removeClient(this);
            socket.close();
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public synchronized void send(Message message) {
        OutputStream output;
        try {
            output = socket.getOutputStream();
            output.write(message.toFix().getBytes());
        } catch (IOException e) {
            System.err.println("Failed to read socket output stream:");
            e.printStackTrace();
        }
    }
}
