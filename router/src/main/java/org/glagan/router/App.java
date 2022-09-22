package org.glagan.router;

import org.glagan.core.Handler.Authentication;
import org.glagan.core.Handler.BaseHandler;
import org.glagan.core.Handler.Handler;
import org.glagan.router.Handler.ForwardToBroker;
import org.glagan.router.Handler.ForwardToMarket;

public class App {
    public static void main(String[] args) {
        Handler brokerChain = new BaseHandler();
        brokerChain.setNext(new Authentication())
                .setNext(new ForwardToMarket());
        Listener brokerListener = new Listener(5000, brokerChain);
        new Thread(brokerListener).start();
        System.out.println(brokerListener);

        Handler marketChain = new BaseHandler();
        marketChain.setNext(new Authentication())
                .setNext(new ForwardToBroker());
        Listener marketListener = new Listener(5001, marketChain);
        new Thread(marketListener).start();
        System.out.println(marketListener);
    }
}
