package org.glagan.core.Messages;

import java.util.Map;

import org.glagan.core.Dictionary;
import org.glagan.core.Header;
import org.glagan.core.Message;
import org.glagan.core.Validable;

public class Buy extends Message implements Validable {
    public Buy(Header header, Map<Dictionary, String> body) {
        super(header, body);
    }

    public boolean isValid() {
        // TODO Validate body for fields: Instrument, Quantity, Market, Price
        // TODO -- and OrderId
        return false;
    }

}
