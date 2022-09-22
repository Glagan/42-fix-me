package org.glagan.core.Handler;

import java.lang.reflect.InvocationTargetException;

import org.glagan.core.Message;

/**
 * A single handler in the chain of responsibility
 */
public abstract class Handler {
    protected Handler next;

    abstract public boolean handle(Message message);

    final public boolean handleNext(Message message) {
        if (next != null) {
            return next.handle(message);
        }
        return true;
    }

    final public Handler setNext(Handler next) {
        this.next = next;
        return next;
    }

    final public Handler clone() {
        Handler handler = null;
        try {
            handler = getClass().getConstructor().newInstance();
            if (next != null) {
                handler.setNext(next.clone());
            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            // No errors to handle
            e.printStackTrace();
        }
        return handler;
    }
}
