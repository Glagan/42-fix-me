package org.glagan.router;

public class App {
    public static void main(String[] args) {
        Listener brokerListener = new org.glagan.router.Broker.Listener(5000);
        new Thread(brokerListener).start();
        Listener marketListener = new org.glagan.router.Market.Listener(5001);
        new Thread(marketListener).start();
    }
}
