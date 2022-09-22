package org.glagan.core;

public enum Dictionary {
    BeginSeqNo("BeginSeqNo", 7),
    EndSeqNo("EndSeqNo", 16),
    BeginString("BeginString", 8),
    BodyLength("BodyLength", 9),
    MsgType("MsgType", 35),
    MsgSeqNum("MsgSeqNum", 34),
    SendingTime("SendingTime", 52),
    OrderId("OrderId", 37),
    Instrument("Instrument", 42),
    Quantity("Quantity", 53),
    Market("Market", 64),
    Broker("Market", 74),
    Price("Price", 65),
    TestReqId("TestReqId", 112),
    RefSeqNum("RefSeqNum", 45),
    RefMsgType("RefMsgType", 372),
    Text("Text", 58),
    CheckSum("CheckSum", 10);

    private String name;
    private int value;

    Dictionary(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    static public Dictionary fromString(String key) {
        int keyValue = Integer.parseInt(key);
        for (Dictionary dictionary : Dictionary.values()) {
            if (dictionary.getValue() == keyValue) {
                return dictionary;
            }
        }
        return null;
    }
}
