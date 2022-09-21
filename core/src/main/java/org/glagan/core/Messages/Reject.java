package org.glagan.core.Messages;

import java.util.Map;

import org.glagan.core.Dictionary;
import org.glagan.core.Header;
import org.glagan.core.Message;
import org.glagan.core.Validable;

public class Reject extends Message implements Validable {
    public Reject(Header header, Map<Dictionary, String> body) {
        super(header, body);
    }

    public boolean isValid() {
        // TODO Validate body for fields: RefSeqNum, RefMsgType
        // TODO Optional fields: Text
        return false;
    }

}
