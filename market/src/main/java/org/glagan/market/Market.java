package org.glagan.market;

public class Market {
    static protected Market instance = new Market();
    protected Instrument[] instruments;

    public Market() {
        this.instruments = new Instrument[1];
        this.instruments[0] = new Instrument("Test", 4242);
    }

    public boolean hasInstrument(String id) {
        if (instruments == null) {
            return false;
        }

        for (Instrument instrument : instruments) {
            if (instrument.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    public Instrument getInstrument(String id) {
        if (instruments == null) {
            return null;
        }

        for (Instrument instrument : instruments) {
            if (instrument.getId().equals(id)) {
                return instrument;
            }
        }

        return null;
    }

    static public Market getInstance() {
        return Market.instance;
    }

    static public void printInstruments() {
        System.out.println("---- Instruments");
        for (Instrument instrument : instance.instruments) {
            System.out.println(instrument.toString());
        }
        System.out.println("----");
    }
}
