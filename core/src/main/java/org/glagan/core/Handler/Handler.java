package org.glagan.core.Handler;

import org.glagan.core.Client;
import org.glagan.core.Message;

/**
 * A single handler in the chain of responsibility
 */
public abstract class Handler {
    protected Handler next;

    abstract public boolean handle(Client client, Message message);

    final public boolean handleNext(Client client, Message message) {
        if (next != null) {
            return next.handle(client, message);
        }
        return true;
    }

    final public Handler setNext(Handler next) {
        this.next = next;
        return next;
    }
}
