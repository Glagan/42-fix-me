package org.glagan.market;

import org.glagan.core.Message;
import org.glagan.core.MsgType;

public class App {
    public static void main(String[] args) {
        Message message = Message.make(MsgType.Buy).build();
        System.out.println("Object " + message);
        System.out.println("FIX " + message.toFix());
        System.out.println("FIX " + message.pretty());
    }
}
