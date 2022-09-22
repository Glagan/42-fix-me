package org.glagan.router;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

import org.glagan.core.Message;
import org.glagan.core.Handler.Handler;

public class Client implements Runnable {
    protected Socket socket;
    protected String id;
    protected Handler handler;

    public Client(Socket socket, Handler handler) {
        this.socket = socket;
        this.id = UUID.randomUUID().toString();
        this.handler = handler;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String text;
            do {
                text = reader.readLine();
                if (text != null) {
                    Message message = Message.fromBuffer(text);
                    if (message != null) {
                        System.out.println("Received message: " + message.pretty());
                        handler.handle(message);
                    } else {
                        System.out.println("Received invalid message: " + text);
                    }
                }
            } while (text != null);

            System.out.println("Closing client port " + socket.getPort());
            socket.close();
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void send(Message message) {
        try (OutputStream output = socket.getOutputStream()) {
            output.write(message.toFix().getBytes());
        } catch (IOException e) {
            System.err.println("Failed to read socket output stream:");
            e.printStackTrace();
        }
    }
}
