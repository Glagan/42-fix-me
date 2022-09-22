package org.glagan.router;

import java.util.HashMap;
import java.util.Map;

import org.glagan.core.Message;

public class Router {
    protected static Router instance = new Router();
    protected Map<String, Connection> clients;
    protected Map<String, Connection> brokers;
    protected Map<String, Connection> markets;

    public Router() {
        clients = new HashMap<>();
        brokers = new HashMap<>();
        markets = new HashMap<>();
    }

    public synchronized void addBroker(Connection client) {
        clients.put(client.getId(), client);
        brokers.put(client.getId(), client);
    }

    public synchronized void addMarket(Connection client) {
        clients.put(client.getId(), client);
        markets.put(client.getId(), client);
    }

    public synchronized void removeClient(Connection client) {
        clients.remove(client.getId());
        brokers.remove(client.getId());
        markets.remove(client.getId());
    }

    public synchronized boolean forward(String id, Message message) {
        Connection client = clients.get(id);
        if (client != null) {
            client.send(message);
            return true;
        }
        return false;
    }

    public static Router getInstance() {
        return Router.instance;
    }
}
