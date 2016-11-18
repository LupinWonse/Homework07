package com.group32.homework07;

import java.util.ArrayList;
import java.util.Date;

public class Conversation {

    private String conversationId;
    private String withUser;
    private Date lastMessageDate;
    private ArrayList<Message> messages;

    public Conversation() {
        this.messages = new ArrayList<>();
    }

    public Conversation(String withUser, String conversationId) {
        this.withUser = withUser;
        this.lastMessageDate = new Date();
        this.messages = new ArrayList<>();
        this.conversationId = conversationId;
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

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
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
