package org.glagan.router.Handler;

import java.lang.reflect.InvocationTargetException;

import org.glagan.core.Dictionary;
import org.glagan.core.Message;
import org.glagan.core.Handler.Handler;
import org.glagan.router.Client;
import org.glagan.router.Listener;

public class ForwardToBroker extends Handler {
    protected Listener brokerListener;

    public ForwardToBroker(Listener brokerListener) {
        this.brokerListener = brokerListener;
    }

    public boolean handle(Message message) {
        System.out.println("ForwardToBroker on " + message);
        // TODO Check if the Broker exists on the router (can be forwarded)

        String expectedFor = message.getBody().get(Dictionary.Broker);
        for (Client client : brokerListener.getClients()) {
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
            handler = getClass().getConstructor(Listener.class).newInstance(brokerListener);
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
