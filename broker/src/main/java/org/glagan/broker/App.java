package org.glagan.broker;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import org.glagan.core.Dictionary;
import org.glagan.core.Message;
import org.glagan.core.MessageInputReader;
import org.glagan.core.MsgType;

public class App {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5000;
        try (Socket socket = new Socket(host, port)) {
            System.out.println("Connected to " + host + ":" + port);
            MessageInputReader reader = new MessageInputReader(socket.getInputStream());

            String id = null;
            while (true) {
                String packet = reader.read();
                if (packet != null) {
                    Message message = Message.fromString(packet);
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
                                        .add(Dictionary.Market, "36641908-4928-4df5-8e24-0ce0e4aa47b9")
                                        .add(Dictionary.Instrument, "chair")
                                        .add(Dictionary.Quantity, "42")
                                        .add(Dictionary.Price, "42.42")
                                        .build();
                                System.out.println("Sending message " + sendingMessage.toFix());
                                output.write((sendingMessage.toFix()).getBytes());
                            } catch (IOException e) {
                                System.err.println("Failed to read socket output stream:");
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
