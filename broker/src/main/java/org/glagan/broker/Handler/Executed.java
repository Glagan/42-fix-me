package org.glagan.broker.Handler;

import org.glagan.core.Client;
import org.glagan.core.Dictionary;
import org.glagan.core.Message;
import org.glagan.core.MsgType;
import org.glagan.core.Handler.Handler;

public class Executed extends Handler {
    protected static Dictionary[] requiredFields = { Dictionary.OrderId };

    @Override
    public boolean handle(Client client, Message message) {
        if (message.getHeader().getMsgType().equals(MsgType.Executed)) {
            // Validate the required fields
            if (!message.hasRequiredFields(requiredFields)) {
                System.out.println("Missing required fields in Executed(EX) message (Expected OrderId(37))");
                return false;
            }
            System.out.println(
                    "\u001B[32mTransaction #" + message.getBody().get(Dictionary.OrderId) + " executed\u001B[0m");
            return true;
        }
        return handleNext(client, message);
    }
}
