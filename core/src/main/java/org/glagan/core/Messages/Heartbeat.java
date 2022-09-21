package org.glagan.core.Messages;

import java.util.Map;

import org.glagan.core.Dictionary;
import org.glagan.core.Header;
import org.glagan.core.Message;
import org.glagan.core.Validable;

public class Heartbeat extends Message implements Validable {
    public Heartbeat(Header header, Map<Dictionary, String> body) {
        super(header, body);
    }

    public boolean isValid() {
        // TODO Optional fields: TestReqId
        return false;
    }

}
