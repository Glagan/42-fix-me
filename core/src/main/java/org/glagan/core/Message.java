package org.glagan.core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * A parsed valid (not for the MsgType) FIX message.
 */
public class Message {
    protected Header header;
    protected Map<Dictionary, String> body;
    protected Trailer trailer;

    public Message(Header header, Map<Dictionary, String> body) {
        this.header = header;
        this.body = body;
        this.trailer = new Trailer();
        this.trailer.setChecksum(this.checksum());
    }

    public int checksum() {
        int total = 0;

        if (header != null) {
            total += header.length();
            // MsgType(35)
            total += String.valueOf(Dictionary.MsgType.getValue()).length() + 1; // + '='
            total += String.valueOf(header.getMsgType().getValue()).length() + 1; // + 'SOH'
            // MsgSeqNum(34)
            if (header.getMsgSeqNum() > 0) {
                total += String.valueOf(Dictionary.MsgSeqNum.getValue()).length() + 1; // + '='
                total += String.valueOf(header.getMsgSeqNum()).length() + 1; // + 'SOH'
            }
            // SendingTime(52)
            if (header.getSendTime() != null) {
                DateFormat formatter = new SimpleDateFormat("YYYYMMDD-HH:mm:ss");
                String utcTimestamp = formatter.format(header.getSendTime());
                total += String.valueOf(Dictionary.SendingTime.getValue()).length() + 1; // + '='
                total += utcTimestamp.length() + 1; // + 'SOH'
            }
        }

        if (body != null) {
            for (Map.Entry<Dictionary, String> entry : body.entrySet()) {
                total += String.valueOf(entry.getKey().getValue()).length() + 1; // + '='
                total += entry.getValue().length() + 1; // + 'SOH'
            }
        }

        return total % 256;
    }

    public boolean isCorrupted() {
        if (header == null || body == null || trailer == null) {
            return true;
        }

        // Check that header.bodyLength is valid
        if (header.getBodyLength() != body.size()) {
            return true;
        }

        // Check that trailer.checksum is valid
        if (trailer.getChecksum() != this.checksum()) {
            return true;
        }

        return false;
    }

    public static Message fromBuffer(String buffer) {
        // TODO parse message from string
        // TODO split on 'SOH' and then split on '='
        return new Message(null, null);
    }

    public static MessageBuilder make(MsgType type) {
        return new MessageBuilder().type(type);
    }

    public String toFix() {
        if (header == null || body == null || trailer == null) {
            return null;
        }

        String result = Dictionary.BeginString.getValue() + "=" + header.getBeginString() + (char) 0x1;
        result += Dictionary.BodyLength.getValue() + "=" + header.getBodyLength() + (char) 0x1;
        result += Dictionary.MsgType.getValue() + "=" + header.getMsgType().getValue() + (char) 0x1;
        if (header.getMsgSeqNum() > 0) {
            result += Dictionary.MsgSeqNum.getValue() + "=" + header.getMsgSeqNum() + (char) 0x1;
        }
        if (header.getSendTime() != null) {
            DateFormat formatter = new SimpleDateFormat("YYYYMMDD-HH:mm:ss");
            String utcTimestamp = formatter.format(header.getSendTime());
            result += Dictionary.SendingTime.getValue() + "=" + utcTimestamp + (char) 0x1;
        }

        for (Map.Entry<Dictionary, String> entry : body.entrySet()) {
            result += entry.getKey().getValue() + "=" + entry.getValue() + (char) 0x1;
        }

        String paddedChecksum = String.format("%03d", trailer.getChecksum());
        result += Dictionary.CheckSum.getValue() + "=" + paddedChecksum + (char) 0x1;

        return result;
    }
}
