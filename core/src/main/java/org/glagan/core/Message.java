package org.glagan.core;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A parsed valid (not for the MsgType) FIX message.
 */
public class Message {
    protected Header header;
    protected Map<Dictionary, String> body;
    protected Trailer trailer;
    protected static DateFormat dateFormat = new SimpleDateFormat("YYYYMMDD-HH:mm:ss");

    public Message(Header header, Map<Dictionary, String> body) {
        this.header = header;
        this.body = body;
        this.trailer = new Trailer();
        this.trailer.setChecksum(this.checksum());
    }

    public Header getHeader() {
        return header;
    }

    public Map<Dictionary, String> getBody() {
        return body;
    }

    public Trailer getTrailer() {
        return trailer;
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
                String utcTimestamp = dateFormat.format(header.getSendTime());
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
        if (buffer == null) {
            return null;
        }

        String[] parts = buffer.split("" + (char) 0x1);
        // BeginString(8), MsgType(35), CheckSum(10) and the empty string after
        // CheckSum(10) are required
        // if (parts.length < 4) {
        // return null;
        // }

        Header header = new Header();
        List<Dictionary> seenTags = new ArrayList<>();
        Map<Dictionary, String> body = new HashMap<>();
        int checksum = 0;

        for (String part : parts) {
            if (part.equals("")) {
                continue;
            }
            String[] pair = part.split("=");
            if (pair.length != 2 || pair[0] == null || pair[1] == null) {
                return null;
            }
            Dictionary key = Dictionary.fromString(pair[0]);
            if (key == null) {
                System.out.println("Key " + pair[0] + " not found in Dictionary");
                return null;
            }
            if (seenTags.contains(key)) {
                System.out.println("Duplicate key " + key.getName());
                return null;
            }
            // TODO check fields order
            switch (key) {
                case BeginString:
                    header.setBeginString(pair[1]);
                    break;
                case BodyLength:
                    header.setBodyLength(Integer.parseInt(pair[1]));
                    break;
                case MsgType:
                    header.setMsgType(MsgType.fromString(pair[1]));
                    break;
                case MsgSeqNum:
                    header.setMsgSeqNum(Integer.parseInt(pair[1]));
                    break;
                case SendingTime:
                    try {
                        header.setSendTime(dateFormat.parse(pair[1]));
                    } catch (ParseException e) {
                        System.out.println("Invalid SendingTime " + pair[1]);
                        return null;
                    }
                    break;
                case CheckSum:
                    checksum = Integer.parseInt(pair[1]);
                    break;
                default:
                    body.put(key, pair[1]);
                    break;
            }
            seenTags.add(key);
        }

        Message message = new Message(header, body);
        message.getTrailer().setChecksum(checksum);
        return message;
    }

    public static MessageBuilder make(MsgType type) {
        return new MessageBuilder().type(type);
    }

    public String print(String separator) {
        if (header == null || body == null || trailer == null) {
            return null;
        }

        String result = Dictionary.BeginString.getValue() + "=" + header.getBeginString() + separator;
        result += Dictionary.BodyLength.getValue() + "=" + header.getBodyLength() + separator;
        result += Dictionary.MsgType.getValue() + "=" + header.getMsgType().getValue() + separator;
        if (header.getMsgSeqNum() > 0) {
            result += Dictionary.MsgSeqNum.getValue() + "=" + header.getMsgSeqNum() + separator;
        }
        if (header.getSendTime() != null) {
            DateFormat formatter = new SimpleDateFormat("YYYYMMDD-HH:mm:ss");
            String utcTimestamp = formatter.format(header.getSendTime());
            result += Dictionary.SendingTime.getValue() + "=" + utcTimestamp + separator;
        }

        for (Map.Entry<Dictionary, String> entry : body.entrySet()) {
            result += entry.getKey().getValue() + "=" + entry.getValue() + separator;
        }

        String paddedChecksum = String.format("%03d", trailer.getChecksum());
        result += Dictionary.CheckSum.getValue() + "=" + paddedChecksum + separator;

        return result;
    }

    public String pretty() {
        return print("|");
    }

    public String toFix() {
        return print("" + (char) 0x1);
    }

    public static DateFormat getDateFormat() {
        return dateFormat;
    }
}
