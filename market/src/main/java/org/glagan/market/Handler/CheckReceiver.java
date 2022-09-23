package org.glagan.market.Handler;

import org.glagan.core.Client;
import org.glagan.core.Dictionary;
import org.glagan.core.Message;
import org.glagan.core.Handler.Handler;

public class CheckReceiver extends Handler {
    public CheckReceiver() {
    }

    public boolean handle(Client client, Message message) {
        if (message.getBody() != null) {
            String expectedMarket = message.getBody().get(Dictionary.Market);
            if (expectedMarket != null && expectedMarket.equals(client.getId())) {
                return handleNext(client, message);
            } else {
                System.out.println("Invalid receiver");
            }
        }
        return false;
    }
}
