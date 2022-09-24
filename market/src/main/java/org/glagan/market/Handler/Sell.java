package org.glagan.market.Handler;

import org.glagan.core.Client;
import org.glagan.core.Dictionary;
import org.glagan.core.Message;
import org.glagan.core.MsgType;
import org.glagan.core.Handler.Handler;
import org.glagan.market.Database;
import org.glagan.market.Instrument;
import org.glagan.market.Market;

public class Sell extends Handler {
    protected static Dictionary[] requiredFields = { Dictionary.OrderId, Dictionary.Instrument, Dictionary.Quantity,
            Dictionary.Price };
    protected Database database;

    public Sell(Database database) {
        this.database = database;
    }

    protected void sendAndLogRejected(Client client, Message message, String text) {
        Message rejected = Message.make(MsgType.Rejected)
                .auth(client.getId())
                .continueFrom(1)
                .add(Dictionary.Broker, message.getHeader().getBeginString())
                .add(Dictionary.OrderId, message.getBody().get(Dictionary.OrderId))
                .add(Dictionary.Text, text)
                .build();
        database.pushTransaction(message, "rejected");
        client.send(rejected);
    }

    @Override
    public boolean handle(Client client, Message message) {
        if (message.getHeader().getMsgType().equals(MsgType.Sell)) {
            // Validate the required fields
            if (!message.hasRequiredFields(requiredFields)) {
                System.out.println(
                        "Missing required fields in Sell(SE) message (Expected OrderId(37), Market(64), Instrument(42), Quantity(53) and Price(65)");
                return false;
            }

            if (!message.validateId(message.getHeader().getBeginString())) {
                System.out.println(
                        "Invalid Broker(74) ID format");
                return false;
            }

            if (!message.validateId(message.getBody().get(Dictionary.Instrument))) {
                System.out.println(
                        "Invalid Instrument(42) ID format");
                return false;
            }

            try {
                int brokerOrderId = Integer.parseInt(message.getBody().get(Dictionary.OrderId));
                if (brokerOrderId < 1) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                System.out.println("The OrderId(53) must be greater than zero");
                return false;
            }

            int quantity = 0;
            try {
                quantity = Integer.parseInt(message.getBody().get(Dictionary.Quantity));
                if (quantity < 1) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                System.out.println("The trading Quantity(53) must be greater than zero");
                return false;
            }

            try {
                double price = Double.parseDouble(message.getBody().get(Dictionary.Price));
                if (price <= 0 || Double.isNaN(price)) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                System.out.println("The trading Price(65) must be valid and greater than zero");
                return false;
            }

            Instrument instrument = Market.getInstrument(message.getBody().get(Dictionary.Instrument));
            if (instrument == null) {
                sendAndLogRejected(client, message, "This Market does not trade this instrument");
                return false;
            }

            // Process transaction
            instrument.buy(quantity);
            System.out.println("\u001B[32mTransaction processed\u001B[0m");
            Message executed = Message.make(MsgType.Executed)
                    .auth(client.getId())
                    .continueFrom(1)
                    .add(Dictionary.Broker, message.getHeader().getBeginString())
                    .add(Dictionary.OrderId, message.getBody().get(Dictionary.OrderId))
                    .build();
            database.pushTransaction(message, "executed");
            client.send(executed);
            return true;
        }
        return handleNext(client, message);
    }
}
