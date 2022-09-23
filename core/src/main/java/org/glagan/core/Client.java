package org.glagan.core;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Client implements Runnable {
    protected Socket socket;
    protected String id;

    public Client(Socket socket) {
        this.socket = socket;
        this.id = null;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void onMessage(Message message) {
    }

    public void onClose() {
        try {
            socket.close();
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {
        try {
            MessageInputReader reader = new MessageInputReader(socket.getInputStream());
            while (true) {
                String packet = reader.read();
                if (packet != null) {
                    Message message = Message.fromString(packet);
                    if (message != null) {
                        if (id != null) {
                            System.out.print("[" + id + "]");
                        }
                        System.out.println("<" + message.pretty());
                    }
                    onMessage(message);
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Closing port " + socket.getPort());
            onClose();
        }
    }

    public synchronized void send(Message message) {
        OutputStream output;
        try {
            output = socket.getOutputStream();
            output.write(message.toFix().getBytes());
            if (id != null) {
                System.out.print("[" + id + "]");
            }
            System.out.println(">" + message.pretty());
        } catch (IOException e) {
            System.err.println("Failed to write to socket output stream:");
            e.printStackTrace();
        }
    }
}
