package org.glagan.core.Messages;

import java.util.Map;

import org.glagan.core.Dictionary;
import org.glagan.core.Header;
import org.glagan.core.Message;
import org.glagan.core.Validable;

public class Logon extends Message implements Validable {
    public Logon(Header header, Map<Dictionary, String> body) {
        super(header, body);
    }

    public boolean isValid() {
        // Only the BeginString is needed and should always be set
        return true;
    }

}
