package org.glagan.router.Handler;

import java.lang.reflect.InvocationTargetException;

import org.glagan.core.Dictionary;
import org.glagan.core.Message;
import org.glagan.core.Handler.Handler;
import org.glagan.router.Client;
import org.glagan.router.Listener;

public class ForwardToMarket extends Handler {
    protected Listener marketListener;

    public ForwardToMarket(Listener marketListener) {
        this.marketListener = marketListener;
    }

    public boolean handle(Message message) {
        System.out.println("ForwardToMarket on " + message);
        // TODO Check if the Market exists on the router (can be forwarded)

        String expectedFor = message.getBody().get(Dictionary.Market);
        for (Client client : marketListener.getClients()) {
            System.out.println("Checking client for messsage " + client.getId() + "/" + expectedFor);
            if (client.getId().equals(expectedFor)) {
                client.send(message);
                return handleNext(message);
            }
        }

        return false;
    }

    @Override
    public Handler clone() {
        Handler handler = null;
        try {
            handler = getClass().getConstructor(Listener.class).newInstance(marketListener);
            if (next != null) {
                handler.setNext(next.clone());
            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            // No errors to handle
            e.printStackTrace();
        }
        return handler;
    }
}
