package org.glagan.router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.glagan.core.Client;
import org.glagan.core.Message;

public class Router {
    protected static Router instance = new Router();
    // Map<Client.id, ...>
    protected Map<String, Client> clients;
    // Map<Client.id, ...>
    protected Map<String, Client> brokers;
    // Map<Client.id, ...>
    protected Map<String, Client> markets;
    protected List<Client> pendingClients;
    // Map<Client.token, Client.id>
    protected Map<String, String> clientTokens;
    // Map<Client.id, ...>
    protected Map<String, List<Message>> pendingMessages;

    public Router() {
        clients = new HashMap<>();
        brokers = new HashMap<>();
        markets = new HashMap<>();
        pendingClients = new ArrayList<>();
        clientTokens = new HashMap<>();
        pendingMessages = new HashMap<>();
    }

    public synchronized String idForToken(String token) {
        if (token == null) {
            return null;
        }
        return clientTokens.get(token);
    }

    public synchronized void addBroker(Client client, String token) {
        clientTokens.put(token, client.getId());
        clients.put(client.getId(), client);
        brokers.put(client.getId(), client);
        pendingClients.remove(client);
    }

    public synchronized void addMarket(Client client, String token) {
        clientTokens.put(token, client.getId());
        clients.put(client.getId(), client);
        markets.put(client.getId(), client);
        pendingClients.remove(client);
    }

    public synchronized void addPending(Client client) {
        pendingClients.add(client);
    }

    public synchronized void removeClient(Client client) {
        clients.remove(client.getId());
        brokers.remove(client.getId());
        markets.remove(client.getId());
        pendingClients.remove(client);
    }

    public synchronized boolean hasPendingMessages(String id) {
        List<Message> pendingMessages = this.pendingMessages.get(id);
        return pendingMessages != null && pendingMessages.size() > 0;
    }

    public synchronized Message getNextPendingMessage(String id) {
        if (clients.get(id) != null) {
            List<Message> pendingMessages = this.pendingMessages.get(id);
            if (pendingMessages != null && pendingMessages.size() > 0) {
                Message pendingMessage = pendingMessages.get(0);
                pendingMessages.remove(0);
                return pendingMessage;
            }
        }
        return null;
    }

    public synchronized boolean forward(String id, Message message) {
        // Send the message to the Client if it exists and is connected
        Client client = clients.get(id);
        if (client != null) {
            client.send(message);
            return true;
        }
        // If the client is currently disconnected but exists
        // Add the message to a pending list
        if (clientTokens.containsValue(id)) {
            List<Message> pendingMessages = this.pendingMessages.get(id);
            if (pendingMessages == null) {
                pendingMessages = new ArrayList<>();
                this.pendingMessages.put(id, pendingMessages);
            }
            pendingMessages.add(message);
        }
        return false;
    }

    public static Router getInstance() {
        return Router.instance;
    }
}
