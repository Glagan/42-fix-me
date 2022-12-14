package org.glagan.core;

public enum MsgType {
    Buy("D"),
    Sell("SE"),
    Executed("EX"),
    Rejected("RJ"),
    Logon("A");

    private String value;

    MsgType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    static public MsgType fromString(String value) {
        for (MsgType msgType : MsgType.values()) {
            if (msgType.getValue().equals(value)) {
                return msgType;
            }
        }
        return null;
    }
}
