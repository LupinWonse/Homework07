package com.group32.homework07;

import java.util.Date;

public class Message {
    private String senderUserUid;
    private String toUserUid;
    private String messageText;
    private String messageImageUrl;
    private Date messageSentDate;

    public Message() {
    }

    public Message(String senderUserUid, String toUserUid, String messageText, String messageImageUrl, Date messageSentDate) {
        this.senderUserUid = senderUserUid;
        this.toUserUid = toUserUid;
        this.messageText = messageText;
        this.messageImageUrl = messageImageUrl;
        this.messageSentDate = messageSentDate;
    }

    public String getSenderUserUid() {
        return senderUserUid;
    }

    public void setSenderUserUid(String senderUserUid) {
        this.senderUserUid = senderUserUid;
    }

    public String getToUserUid() {
        return toUserUid;
    }

    public void setToUserUid(String toUserUid) {
        this.toUserUid = toUserUid;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageImageUrl() {
        return messageImageUrl;
    }

    public void setMessageImageUrl(String messageImageUrl) {
        this.messageImageUrl = messageImageUrl;
    }

    public Date getMessageSentDate() {
        return messageSentDate;
    }

    public void setMessageSentDate(Date messageSentDate) {
        this.messageSentDate = messageSentDate;
    }
}
