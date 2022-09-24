package org.glagan.market;

import org.glagan.core.Message;

public class Transaction {
    protected Message message;
    protected String status;

    public Transaction(Message message, String status) {
        this.message = message;
        this.status = status;
    }

    public Message getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}
