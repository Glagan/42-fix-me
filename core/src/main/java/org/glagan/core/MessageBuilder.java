package org.glagan.core;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MessageBuilder {
    protected Header header;
    protected Map<Dictionary, String> body;

    public MessageBuilder() {
        this.header = new Header();
        this.body = new HashMap<Dictionary, String>();
    }

    public MessageBuilder type(MsgType type) {
        if (type != null) {
            header.setMsgType(type);
        }
        return this;
    }

    public MessageBuilder auth(String id) {
        header.setBeginString(id);
        return this;
    }

    public MessageBuilder continueFrom(int msgSeqNum) {
        header.setMsgSeqNum(msgSeqNum);
        return this;
    }

    public MessageBuilder add(Dictionary key, String value) {
        if (value != null) {
            body.put(key, value);
        }
        return this;
    }

    public Message build() {
        header.setBodyLength(body.size());
        header.setSendTime(new Date());
        return new Message(header, body);
    }
}
