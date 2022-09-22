package org.glagan.router;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

import org.glagan.core.Message;
import org.glagan.core.MessageInputReader;
import org.glagan.core.MsgType;

public abstract class Connection implements Runnable {
    protected Socket socket;
    protected String id;

    public Connection(Socket socket) {
        this.socket = socket;
        this.id = UUID.randomUUID().toString();
        if (!socket.isClosed()) {
            send(Message.make(MsgType.Logon).auth(this.id).continueFrom(1).build());
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
            MessageInputReader reader = new MessageInputReader(socket.getInputStream());

            while (true) {
                String packet = reader.read();
                if (packet != null) {
                    Message message = Message.fromString(packet);
                    if (message != null) {
                        System.out.println("[" + id + "] " + message.pretty());
                    }
                    onMessage(message);
                } else {
                    break;
                }
            }

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
            output.write((message.toFix()).getBytes());
        } catch (IOException e) {
            System.err.println("Failed to write to socket output stream:");
            e.printStackTrace();
        }
    }
}
