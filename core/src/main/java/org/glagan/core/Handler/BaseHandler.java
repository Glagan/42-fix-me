package org.glagan.core.Handler;

import org.glagan.core.Message;

/**
 * Default handler to use as a boostrap in the chain
 */
public class BaseHandler extends Handler {
    public boolean handle(Message message) {
        return handleNext(message);
    }
}
