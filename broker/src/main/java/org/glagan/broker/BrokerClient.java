package org.glagan.broker;

import java.net.Socket;

import org.glagan.core.Client;
import org.glagan.core.Dictionary;
import org.glagan.core.Message;
import org.glagan.core.MsgType;

public class BrokerClient extends Client {
    public BrokerClient(Socket socket) {
        super(socket);
    }

    @Override
    public void onMessage(Message message) {
        System.out.println("received message " + message.pretty());
        if (message != null) {
            if (message.getHeader().getMsgType().equals(MsgType.Logon)) {
                System.out.println("received id " + message.getHeader().getBeginString());
                id = message.getHeader().getBeginString();
                send(Message.make(MsgType.Buy)
                        .auth(id).continueFrom(1)
                        .add(Dictionary.OrderId, "1")
                        .add(Dictionary.Market, "36641908-4928-4df5-8e24-0ce0e4aa47b9")
                        .add(Dictionary.Instrument, "chair")
                        .add(Dictionary.Quantity, "42")
                        .add(Dictionary.Price, "42.42")
                        .build());
            }
        }
    }
}
