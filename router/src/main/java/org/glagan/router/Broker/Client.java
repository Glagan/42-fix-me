package org.glagan.router.Broker;

import java.net.Socket;

import org.glagan.core.Message;
import org.glagan.core.Handler.BaseHandler;
import org.glagan.core.Handler.Handler;
import org.glagan.router.RouterClient;
import org.glagan.router.Handler.Authentication;
import org.glagan.router.Handler.ForwardToMarket;

public class Client extends RouterClient {
    protected Handler handler;

    public Client(Socket socket) {
        super(socket);
        handler = new BaseHandler();
        handler.setNext(new Authentication())
                .setNext(new ForwardToMarket());
    }

    @Override
    public void onMessage(Message message) {
        if (message != null) {
            handler.handle(this, message);
        }
    }
}
