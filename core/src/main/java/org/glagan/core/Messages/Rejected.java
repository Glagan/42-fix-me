package org.glagan.core.Messages;

import java.util.Map;

import org.glagan.core.Dictionary;
import org.glagan.core.Header;
import org.glagan.core.Message;
import org.glagan.core.Validable;

public class Rejected extends Message implements Validable {
    public Rejected(Header header, Map<Dictionary, String> body) {
        super(header, body);
    }

    public boolean isValid() {
        // TODO Validate body for fields: OrderId, RefSeqNum, RefMsgType
        // TODO Optional fields: Text
        return false;
    }

}
