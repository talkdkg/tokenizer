package com.test;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.netflix.astyanax.util.TimeUUIDUtils;

public class ChatSession implements Comparable<ChatSession> {

    private String channelId;
    private UUID chatSessionUUID;
    private Date startDate;
    private Date sessionEndTimestamp;
    private String ownerID;
    private String userID;

    private List<Message> messages;

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public ChatSession() {
    }

    public ChatSession(final Date startDate) {
        this.chatSessionUUID = TimeUUIDUtils.getTimeUUID(startDate.getTime());
        this.startDate = startDate;
    }

    public Date getSessionEndTimestamp() {
        return sessionEndTimestamp;
    }

    public void setSessionEndTimestamp(final Date sessionEndTimestamp) {
        this.sessionEndTimestamp = sessionEndTimestamp;
    }

    public UUID getChatSessionUUID() {
        return chatSessionUUID;
    }

    public void setChatSessionUUID(final UUID chatSessionUUID) {
        this.chatSessionUUID = chatSessionUUID;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(final String ownerID) {
        this.ownerID = ownerID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(final String userID) {
        this.userID = userID;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    @Override
    public int compareTo(ChatSession o) {

        int k = this.channelId.compareTo(o.channelId);
        if (k != 0)
            return k;

        return startDate.compareTo(o.startDate);

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((chatSessionUUID == null) ? 0 : chatSessionUUID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChatSession other = (ChatSession) obj;
        if (chatSessionUUID == null) {
            if (other.chatSessionUUID != null)
                return false;
        } else if (!chatSessionUUID.equals(other.chatSessionUUID))
            return false;
        return true;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return "ChatSession [channelId=" + channelId + ", chatSessionUUID="
                + chatSessionUUID + ", startDate=" + startDate
                + ", sessionEndTimestamp=" + sessionEndTimestamp + ", ownerID="
                + ownerID + ", userID=" + userID + "]";
    }

}
