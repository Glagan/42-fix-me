package org.glagan.broker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import org.glagan.core.Dictionary;
import org.glagan.core.Message;
import org.glagan.core.MsgType;

public class App {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5000;
        try (Socket socket = new Socket(host, port)) {
            System.out.println("Connected to " + host + ":" + port);
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String id = null;
            String text;
            do {
                text = reader.readLine();
                if (text != null) {
                    System.out.println("received text: " + text);
                    Message message = Message.fromString(text);
                    System.out.println("received message: " + message.pretty());
                    if (message != null) {
                        if (message.getHeader().getMsgType().equals(MsgType.Logon)) {
                            System.out.println("received id " + message.getHeader().getBeginString());
                            id = message.getHeader().getBeginString();

                            OutputStream output;
                            try {
                                output = socket.getOutputStream();
                                Message sendingMessage = Message.make(MsgType.Buy)
                                        .auth(id).continueFrom(1)
                                        .add(Dictionary.OrderId, "1")
                                        .add(Dictionary.Market, "d9364487-b501-4711-8424-aa15881c9d58")
                                        .add(Dictionary.Instrument, "chair")
                                        .add(Dictionary.Quantity, "42")
                                        .add(Dictionary.Price, "42.42")
                                        .build();
                                System.out.println("Sending message " + sendingMessage.toFix());
                                output.write((sendingMessage.toFix() + "\n").getBytes());
                            } catch (IOException e) {
                                System.err.println("Failed to read socket output stream:");
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } while (text != null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
