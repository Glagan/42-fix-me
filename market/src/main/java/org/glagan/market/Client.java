package org.glagan.market;

import java.net.Socket;

import org.glagan.market.Handler.Buy;
import org.glagan.market.Handler.CheckReceiver;
import org.glagan.market.Handler.Sell;
import org.glagan.core.Message;
import org.glagan.core.Handler.BaseHandler;
import org.glagan.core.Handler.Handler;
import org.glagan.core.Handler.Logon;
import org.glagan.core.Handler.Validate;

public class Client extends org.glagan.core.Client {
    protected Handler chain;

    public Client(Socket socket, Database database) {
        super(socket);
        this.chain = new BaseHandler();
        this.chain.setNext(new Validate())
                .setNext(new Logon())
                .setNext(new CheckReceiver())
                .setNext(new Buy(database))
                .setNext(new Sell(database));
    }

    @Override
    public void onMessage(Message message) {
        if (message != null) {
            chain.handle(this, message);
        }
    }
}
