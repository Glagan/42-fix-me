package org.glagan.market;

import java.util.UUID;

public class Instrument {
    protected String id;
    protected String name;
    protected int quantity;

    public Instrument(String name, int quantity) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    /**
     * Buy an Instrument from a Broker
     * (increase the available quantity on the Market)
     */
    public void buy(int quantity) {
        this.quantity += quantity;
    }

    /**
     * Sell an Instrument to a Broker
     * (decrease the available quantity on the Market)
     */
    public boolean sell(int quantity) {
        if (this.quantity >= quantity) {
            this.quantity -= quantity;
            return true;
        }
        return false;
    }

    public String toString() {
        return id + "\t" + String.format("%6d", quantity) + "\t" + name;
    }
}
