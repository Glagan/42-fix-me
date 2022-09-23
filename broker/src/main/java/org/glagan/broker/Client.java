package org.glagan.broker;

import java.net.Socket;

import org.glagan.broker.Handler.CheckReceiver;
import org.glagan.broker.Handler.Executed;
import org.glagan.broker.Handler.Rejected;
import org.glagan.core.Message;
import org.glagan.core.Handler.BaseHandler;
import org.glagan.core.Handler.Handler;
import org.glagan.core.Handler.Logon;
import org.glagan.core.Handler.Validate;

public class Client extends org.glagan.core.Client {
    protected Handler chain;

    public Client(Socket socket) {
        super(socket);
        this.chain = new BaseHandler();
        this.chain.setNext(new Validate())
                .setNext(new Logon())
                .setNext(new CheckReceiver())
                .setNext(new Executed())
                .setNext(new Rejected());
    }

    @Override
    public void onMessage(Message message) {
        if (message != null) {
            chain.handle(this, message);
        }
    }
}
