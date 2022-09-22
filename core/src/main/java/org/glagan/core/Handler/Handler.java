package org.glagan.core.Handler;

import org.glagan.core.Message;

/**
 * A single handler in the chain of responsibility
 */
public abstract class Handler {
    protected Handler next;

    abstract public boolean handle(String clientId, Message message);

    final public boolean handleNext(String clientId, Message message) {
        if (next != null) {
            return next.handle(clientId, message);
        }
        return true;
    }

    final public Handler setNext(Handler next) {
        this.next = next;
        return next;
    }
}
