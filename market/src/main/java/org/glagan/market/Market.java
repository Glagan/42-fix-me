package org.glagan.market;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.github.javafaker.Faker;

public class Market {
    static protected Market instance = new Market();
    protected Instrument[] instruments;

    public Market() {
        this.instruments = null;
    }

    static public boolean hasInstrument(String id) {
        if (instance.instruments == null) {
            return false;
        }

        for (Instrument instrument : instance.instruments) {
            if (instrument.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    static public boolean hasInstruments() {
        if (instance.instruments == null || instance.instruments.length == 0) {
            return false;
        }
        return true;
    }

    static public Instrument getInstrument(String id) {
        if (instance.instruments == null) {
            return null;
        }

        for (Instrument instrument : instance.instruments) {
            if (instrument.getId().equals(id)) {
                return instrument;
            }
        }

        return null;
    }

    static public void setInstruments(Instrument[] instruments) {
        instance.instruments = instruments;
    }

    static public Market getInstance() {
        return Market.instance;
    }

    static public void printInstruments() {
        System.out.println("---- Instruments");
        if (instance.instruments != null && instance.instruments.length > 0) {
            for (Instrument instrument : instance.instruments) {
                System.out.println(instrument.toString());
            }
        } else {
            System.out.println("No Instruments");
        }
        System.out.println("----");
    }

    static public void loadFromFilePath(String filePath) {
        try {
            Path path = Path.of(filePath);
            String content = Files.readString(path);
            if (content != null) {
                List<Instrument> instruments = new ArrayList<>();
                String[] lines = content.split("\n");
                for (String line : lines) {
                    String[] parts = line.split("\\|");
                    if (parts.length != 2) {
                        System.out.println(
                                "\u001B[33mInvalid Market entry: \u001B[0m" + line + "(" + parts.length + ")");
                        continue;
                    }
                    try {
                        int quantity = Integer.parseInt(parts[1]);
                        if (quantity < 0) {
                            throw new NumberFormatException();
                        }
                        instruments.add(new Instrument(parts[0], quantity));
                    } catch (NumberFormatException e) {
                        System.out.println("\u001B[33mInvalid Market quantity: \u001B[0m" + parts[1]);
                    }
                }
                Market.setInstruments(instruments.toArray(new Instrument[instruments.size()]));
            } else {
                throw new IOException();
            }
        } catch (IOException e) {
            System.out.println(
                    "\u001B[31mThe market file path doesn't exists or you don't have the permissions\u001B[0m");
        }
    }

    static public void generateRandom(int amount) {
        instance.instruments = new Instrument[amount];
        Faker faker = new Faker();
        for (int i = 0; i < amount; i++) {
            instance.instruments[i] = new Instrument(faker.commerce().productName(),
                    faker.number().numberBetween(0, 9999));
        }
    }
}
