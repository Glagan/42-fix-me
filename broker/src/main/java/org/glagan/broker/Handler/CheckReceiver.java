package org.glagan.broker.Handler;

import org.glagan.core.Client;
import org.glagan.core.Dictionary;
import org.glagan.core.Message;
import org.glagan.core.Handler.Handler;

public class CheckReceiver extends Handler {
    @Override
    public boolean handle(Client client, Message message) {
        if (message.getBody() != null) {
            String expectedBroker = message.getBody().get(Dictionary.Broker);
            if (expectedBroker != null && expectedBroker.equals(client.getId())) {
                return handleNext(client, message);
            } else {
                System.out.println("Invalid receiver");
            }
        } else {
            System.out.println("Received nothing while expecting a Body with a valid received");
        }
        return false;
    }
}
