package org.glagan.router;

import java.util.HashMap;
import java.util.Map;

import org.glagan.core.Client;
import org.glagan.core.Message;

public class Router {
    protected static Router instance = new Router();
    protected Map<String, Client> clients;
    protected Map<String, Client> brokers;
    protected Map<String, Client> markets;

    public Router() {
        clients = new HashMap<>();
        brokers = new HashMap<>();
        markets = new HashMap<>();
    }

    public synchronized void addBroker(Client client) {
        clients.put(client.getId(), client);
        brokers.put(client.getId(), client);
    }

    public synchronized void addMarket(Client client) {
        clients.put(client.getId(), client);
        markets.put(client.getId(), client);
    }

    public synchronized void removeClient(Client client) {
        clients.remove(client.getId());
        brokers.remove(client.getId());
        markets.remove(client.getId());
    }

    public synchronized boolean forward(String id, Message message) {
        Client client = clients.get(id);
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
