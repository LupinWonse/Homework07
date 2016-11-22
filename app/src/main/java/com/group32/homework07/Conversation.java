package com.group32.homework07;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Conversation {

    private String conversationId;
    private String withUser;
    private Date lastMessageDate;
    private Boolean hasNewMessages;
    private HashMap<String,Message> messages;

    public Conversation() {
        this.messages = new HashMap<>();
    }

    public Conversation(String withUser, String conversationId) {
        this.withUser = withUser;
        this.lastMessageDate = new Date();
        this.messages = new HashMap<>();
        this.conversationId = conversationId;
        this.hasNewMessages = false;
    }

    public Boolean getHasNewMessages() {
        return hasNewMessages;
    }

    public void setHasNewMessages(Boolean hasNewMessages) {
        this.hasNewMessages = hasNewMessages;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getWithUser() {
        return withUser;
    }

    public void setWithUser(String withUser) {
        this.withUser = withUser;
    }

    public Date getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(Date lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public HashMap<String, Message> getMessages() {
        return messages;
    }

    public void setMessages(HashMap<String, Message> messages) {
        this.messages = messages;
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass().equals(Conversation.class)){
            return ((Conversation) o).getConversationId().equals(this.conversationId);
        } else {
            return super.equals(o);
        }
    }
}
