package org.glagan.core;

public enum MsgType {
    Heartbeat("0"),
    Test("1"),
    Resend("2"),
    Reject("3"),
    Buy("D"),
    Sell("SE"),
    Executed("EX"),
    Rejected("RJ");

    private String value;

    MsgType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
