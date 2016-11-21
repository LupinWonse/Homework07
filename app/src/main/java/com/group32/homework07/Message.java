package com.group32.homework07;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

public class Message {
    private String senderUserUid;
    private String toUserUid;
    private String messageText;
    private String messageImageUrl;
    private String messageId;
    private Date messageSentDate;

    public Message() {
        this.setMessageSentDate(new Date());
    }

    public Message(String senderUserUid, String toUserUid, String messageText, String messageImageUrl, String messageId, Date messageSentDate) {
        this.senderUserUid = senderUserUid;
        this.toUserUid = toUserUid;
        this.messageText = messageText;
        this.messageImageUrl = messageImageUrl;
        this.messageId = messageId;
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

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass().equals(Message.class)){
            return this.messageId.equals(((Message) o).getMessageId());
        }

        return super.equals(o);
    }

    public String getPrettyTime(){
        PrettyTime pt = new PrettyTime();
        return pt.format(this.messageSentDate);
    }
}
