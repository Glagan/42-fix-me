package org.glagan.router;

public class App {
    public static void main(String[] args) {
        Servers servers = new Servers();
        Listener brokerListener = new org.glagan.router.Broker.Listener(servers, 5000);
        servers.setBrokerListener(brokerListener);
        Listener marketListener = new org.glagan.router.Market.Listener(servers, 5001);
        servers.setMarketListener(marketListener);
        new Thread(brokerListener).start();
        new Thread(marketListener).start();
    }
}
