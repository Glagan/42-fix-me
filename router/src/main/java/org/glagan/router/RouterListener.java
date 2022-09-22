package org.glagan.router;

import java.net.Socket;
import java.util.UUID;

import org.glagan.core.Client;
import org.glagan.core.Message;
import org.glagan.core.MsgType;

public abstract class RouterListener extends Listener {
    public RouterListener(int port) {
        super(port);
    }

    @Override
    public void onConnection(Socket socket, Client client) {
        client.setId(UUID.randomUUID().toString());
        if (!socket.isClosed()) {
            client.send(Message.make(MsgType.Logon).auth(client.getId()).continueFrom(1).build());
        }
    }
}
