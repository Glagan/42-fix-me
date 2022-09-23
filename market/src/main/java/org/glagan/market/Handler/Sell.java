package org.glagan.market.Handler;

import org.glagan.core.Client;
import org.glagan.core.Dictionary;
import org.glagan.core.Message;
import org.glagan.core.MsgType;
import org.glagan.core.Handler.Handler;
import org.glagan.market.Instrument;
import org.glagan.market.Market;

public class Sell extends Handler {
    protected static Dictionary[] requiredFields = { Dictionary.OrderId, Dictionary.Instrument, Dictionary.Quantity,
            Dictionary.Price };

    @Override
    public boolean handle(Client client, Message message) {
        if (message.getHeader().getMsgType().equals(MsgType.Sell)) {
            // Validate the required fields
            if (!message.hasRequiredFields(requiredFields)) {
                System.out.println(
                        "Missing required fields in Sell(SE) message (Expected OrderId(37), Market(64), Instrument(42), Quantity(53) and Price(65)");
                return false;
            }
            int quantity = Integer.parseInt(message.getBody().get(Dictionary.Quantity));
            if (quantity < 1) {
                System.out.println("The trading Quantity(53) must greater than zero");
                return false;
            }
            Instrument instrument = Market.getInstance().getInstrument(message.getBody().get(Dictionary.Instrument));
            if (instrument == null) {
                System.out.println(
                        "This Market does not trade this Instrument (" + message.getBody().get(Dictionary.Instrument));
                return false;
            }
            instrument.buy(quantity);
            System.out.println("Transaction processed");
            Message executed = Message.make(MsgType.Executed)
                    .auth(client.getId())
                    .continueFrom(1)
                    .add(Dictionary.Broker, message.getHeader().getBeginString())
                    .add(Dictionary.OrderId, message.getBody().get(Dictionary.OrderId))
                    .build();
            client.send(executed);
            return true;
        }
        return handleNext(client, message);
    }
}
