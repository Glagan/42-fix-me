package org.glagan.broker.Handler;

import org.glagan.core.Client;
import org.glagan.core.Dictionary;
import org.glagan.core.Message;
import org.glagan.core.MsgType;
import org.glagan.core.Handler.Handler;

public class Rejected extends Handler {
    protected static Dictionary[] requiredFields = { Dictionary.OrderId };

    @Override
    public boolean handle(Client client, Message message) {
        if (message.getHeader().getMsgType().equals(MsgType.Rejected)) {
            // Validate the required fields
            if (!message.hasRequiredFields(requiredFields)) {
                System.out.println("Missing required fields in Rejected(J) message (Expected OrderId(37))");
                return false;
            }
            System.out.println("\u001B[31mTransaction #" + message.getBody().get(Dictionary.OrderId) + " rejected: "
                    + message.getBody().get(Dictionary.Text) + "\u001B[0m");
            return true;
        }
        return handleNext(client, message);
    }
}
