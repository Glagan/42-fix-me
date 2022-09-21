package org.glagan.core;

public abstract class Handler {
    protected Handler next;

    abstract public boolean handle(Message message);

    final public Handler setNext(Handler next) {
        this.next = next;
        return next;
    }
}
