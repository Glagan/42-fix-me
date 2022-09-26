package org.glagan.router;

public class Servers {
    protected Listener marketListener;
    protected Listener brokerListener;

    public Listener getMarketListener() {
        return marketListener;
    }

    public void setMarketListener(Listener marketListener) {
        this.marketListener = marketListener;
    }

    public Listener getBrokerListener() {
        return brokerListener;
    }

    public void setBrokerListener(Listener brokerListener) {
        this.brokerListener = brokerListener;
    }

    public void closeAll() {
        if (marketListener != null) {
            marketListener.close();
        }
        if (brokerListener != null) {
            brokerListener.close();
        }
    }
}
