package org.glagan.router.Market;

import java.net.Socket;

import org.glagan.core.Message;
import org.glagan.core.Handler.BaseHandler;
import org.glagan.core.Handler.Handler;
import org.glagan.router.Handler.Authentication;
import org.glagan.router.Handler.ForwardToBroker;

public class Client extends org.glagan.router.Connection {
    protected Handler handler;

    public Client(Socket socket) {
        super(socket);
        handler = new BaseHandler();
        handler.setNext(new Authentication())
                .setNext(new ForwardToBroker());
    }

    public void onMessage(Message message) {
        if (message != null) {
            System.out.println("Received message: " + message.pretty());
            handler.handle(id, message);
        }
    }
}
