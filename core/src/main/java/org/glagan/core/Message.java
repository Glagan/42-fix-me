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
    protected static DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");

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
        if (header == null || trailer == null) {
            return true;
        }

        // Check that header.bodyLength is valid
        if (header.getBodyLength() > 0 && (body == null || header.getBodyLength() != body.size())) {
            return true;
        }

        // Check that trailer.checksum is valid
        if (trailer.getChecksum() != this.checksum()) {
            System.out.println("Invalid CheckSum(10), found " + trailer.getChecksum() + " expected " + this.checksum());
            return true;
        }

        return false;
    }

    public boolean hasRequiredFields(Dictionary[] fields) {
        if (fields != null && body == null) {
            return false;
        }
        if (fields != null) {
            for (Dictionary field : fields) {
                String value = body.get(field);
                if (value == null || value.equals("")) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean validateId(String id) {
        return id != null && id.length() == 36;
    }

    public static Message fromString(String buffer) {
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

        boolean foundBodyLength = false;
        boolean foundCheckSum = false;

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.equals("")) {
                if (i == parts.length - 1) {
                    break;
                } else {
                    System.out.println("Invalid empty Tag");
                    return null;
                }
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

            switch (key) {
                case BeginString:
                    if (i != 0) {
                        System.out.println("BeginString(8) should be on the first position");
                        return null;
                    }
                    header.setBeginString(pair[1]);
                    break;
                case BodyLength:
                    if (i != 1) {
                        System.out.println("BodyLength(9) should be on the second position");
                        return null;
                    }
                    foundBodyLength = true;
                    header.setBodyLength(Integer.parseInt(pair[1]));
                    break;
                case MsgType:
                    if (i != 2) {
                        System.out.println("MsgType(35) should be on the third position");
                        return null;
                    }
                    MsgType msgType = MsgType.fromString(pair[1]);
                    if (msgType == null) {
                        System.out.println("Invalid MsgType(35) " + pair[1]);
                        return null;
                    }
                    header.setMsgType(msgType);
                    break;
                case SendingTime:
                    if (i != 3) {
                        System.out.println("SendingTime(52) should be on the fourth position");
                        return null;
                    }
                    try {
                        header.setSendTime(dateFormat.parse(pair[1]));
                    } catch (ParseException e) {
                        System.out.println("Invalid SendingTime(52) " + pair[1]);
                        return null;
                    }
                    break;
                case CheckSum:
                    if (i != parts.length - 1) {
                        System.out.println("CheckSum(10) should be on the last position");
                        return null;
                    }
                    foundCheckSum = true;
                    checksum = Integer.parseInt(pair[1]);
                    break;
                default:
                    body.put(key, pair[1]);
                    break;
            }
            seenTags.add(key);
        }

        if (header.getBeginString() == null) {
            System.out.println("BeginString(8) is required");
            return null;
        }
        if (!foundBodyLength) {
            System.out.println("BodyLength(9) is required");
            return null;
        }
        if (header.getBodyLength() < 0) {
            System.out.println("BodyLength(9) should be greater or equal to 0");
            return null;
        }
        if (header.getMsgType() == null) {
            System.out.println("MsgType(35) is required");
            return null;
        }
        if (header.getSendTime() == null) {
            System.out.println("SendingTime(52) is required");
            return null;
        }
        if (!foundCheckSum) {
            System.out.println("CheckSum(10) is required");
            return null;
        }
        if (checksum < 0 || checksum > 255) {
            System.out.println("CheckSum(10) should be between 0 and 255");
            return null;
        }

        Message message = new Message(header, body);
        message.getTrailer().setChecksum(checksum);
        return message;
    }

    public static MessageBuilder make(MsgType type) {
        return new MessageBuilder().type(type);
    }

    public String pretty() {
        if (header == null || trailer == null) {
            return null;
        }

        String result = Dictionary.BeginString.getName() + "(" + Dictionary.BeginString.getValue() + ")="
                + header.getBeginString() + '|';
        result += Dictionary.BodyLength.getName() + "(" + Dictionary.BodyLength.getValue() + ")="
                + header.getBodyLength() + '|';
        result += Dictionary.MsgType.getName() + "(" + Dictionary.MsgType.getValue() + ")="
                + header.getMsgType().getValue() + '|';
        if (header.getSendTime() != null) {
            String utcTimestamp = dateFormat.format(header.getSendTime());
            result += Dictionary.SendingTime.getName() + "(" + Dictionary.SendingTime.getValue() + ")=" + utcTimestamp
                    + '|';
        }

        if (body != null) {
            for (Map.Entry<Dictionary, String> entry : body.entrySet()) {
                result += entry.getKey().getName() + "(" + entry.getKey().getValue() + ")=" + entry.getValue() + '|';
            }
        }

        String paddedChecksum = String.format("%03d", trailer.getChecksum());
        result += Dictionary.CheckSum.getName() + "(" + Dictionary.CheckSum.getValue() + ")=" + paddedChecksum + '|';

        return result;
    }

    public String toFix() {
        if (header == null || trailer == null) {
            return null;
        }

        String result = Dictionary.BeginString.getValue() + "=" + header.getBeginString() + (char) 0x1;
        result += Dictionary.BodyLength.getValue() + "=" + header.getBodyLength() + (char) 0x1;
        result += Dictionary.MsgType.getValue() + "=" + header.getMsgType().getValue() + (char) 0x1;
        if (header.getSendTime() != null) {
            String utcTimestamp = dateFormat.format(header.getSendTime());
            result += Dictionary.SendingTime.getValue() + "=" + utcTimestamp + (char) 0x1;
        }

        if (body != null) {
            for (Map.Entry<Dictionary, String> entry : body.entrySet()) {
                result += entry.getKey().getValue() + "=" + entry.getValue() + (char) 0x1;
            }
        }

        String paddedChecksum = String.format("%03d", trailer.getChecksum());
        result += Dictionary.CheckSum.getValue() + "=" + paddedChecksum + (char) 0x1;

        return result;
    }

    public static DateFormat getDateFormat() {
        return dateFormat;
    }
}
