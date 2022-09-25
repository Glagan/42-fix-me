package org.glagan.router.Market;

import java.net.Socket;

import org.glagan.core.Message;
import org.glagan.core.Handler.BaseHandler;
import org.glagan.core.Handler.Handler;
import org.glagan.router.RouterClient;
import org.glagan.router.Handler.Authentication;
import org.glagan.router.Handler.ForwardToBroker;

public class Client extends RouterClient {
    protected Handler handler;

    public Client(Socket socket) {
        super(socket);
        handler = new BaseHandler();
        handler.setNext(new Logon())
                .setNext(new Authentication())
                .setNext(new ForwardToBroker());
    }

    @Override
    public void onMessage(Message message) {
        if (message != null) {
            handler.handle(this, message);
        }
    }
}
