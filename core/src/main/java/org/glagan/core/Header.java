package org.glagan.core;

import java.util.Date;

public class Header {
    protected String beginString;
    protected int bodyLength;
    protected MsgType msgType;
    protected int msgSeqNum;
    protected Date sendTime;

    public Header() {
    }

    public String getBeginString() {
        return beginString;
    }

    public void setBeginString(String beginString) {
        this.beginString = beginString;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    public MsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(MsgType msgType) {
        this.msgType = msgType;
    }

    public int getMsgSeqNum() {
        return msgSeqNum;
    }

    public void setMsgSeqNum(int msgSeqNum) {
        this.msgSeqNum = msgSeqNum;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public int length() {
        int total = 0;

        if (msgType != null) {
            total += String.valueOf(Dictionary.MsgType.getValue()).length() + 1;
            total += msgType.getValue().length() + 1;
        }

        if (msgSeqNum > 0) {
            total += String.valueOf(Dictionary.MsgSeqNum.getValue()).length() + 1;
            total += String.valueOf(msgSeqNum).length() + 1;
        }

        if (sendTime != null) {
            total += String.valueOf(Dictionary.SendingTime.getValue()).length() + 1;
            total += String.valueOf(sendTime).length() + 1;
        }

        return total;
    }
}
