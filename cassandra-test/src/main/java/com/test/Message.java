package com.test;

import java.util.Date;
import java.util.UUID;

import com.eaio.uuid.UUIDGen;

public class Message {
    private UUID chatSessionUUID;
    private UUID uuid;
    private Date date;
    private String senderId;
    private Integer sequenceNumber;
    private String text;
    private MessageEvents lastEvent;

    public Message() {
    }

    public Message(final Date date) {
        // this won't be unique key:
        // this.uuid = TimeUUIDUtils.getTimeUUID(date.getTime());
        long clockSeqAndNode = 0x8000000000000000L;
        clockSeqAndNode |= (long) (Math.random() * 0x7FFFFFFF);
        clockSeqAndNode |= (long) (Math.random() * 0x3FFF) << 48;
        // perhaps it will be unique:
        this.uuid = new java.util.UUID(UUIDGen.createTime(date.getTime()),
                clockSeqAndNode);
        this.date = date;
    }

    public UUID getChatSessionUUID() {
        return chatSessionUUID;
    }

    public void setChatSessionUUID(final UUID chatSessionUUID) {
        this.chatSessionUUID = chatSessionUUID;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(final String senderId) {
        this.senderId = senderId;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(final Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public MessageEvents getLastEvent() {
        return lastEvent;
    }

    public void setLastEvent(final MessageEvents lastEvent) {
        this.lastEvent = lastEvent;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Message [chatSessionUUID=" + chatSessionUUID + ", uuid=" + uuid
                + ", date=" + date + ", senderId=" + senderId
                + ", sequenceNumber=" + sequenceNumber + ", text=" + text
                + ", lastEvent=" + lastEvent + "]";
    }
}
