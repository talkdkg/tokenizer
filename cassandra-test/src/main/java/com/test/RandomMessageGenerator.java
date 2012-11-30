package com.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;

import com.eaio.uuid.UUIDGen;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

public class RandomMessageGenerator {

    public final List<ChatSession> chatSessions = new ArrayList<ChatSession>();

    public final List<String> senderIDs = new ArrayList<String>();

    public final List<UUID> messageUUIDs = new ArrayList<UUID>();

    Random random = new Random();

    public RandomMessageGenerator(final int sessionsCount,
            final int sendersCount, final int maxMessagesPerSession) {

        for (int i = 0; i < sendersCount; i++) {
            String sender = "user_" + i;
            senderIDs.add(sender);
        }

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_MONTH, -1000);

        for (int sessionCounter = 0; sessionCounter < sessionsCount; sessionCounter++) {
            calendar.add(Calendar.SECOND, random.nextInt(100));
            UUID chatSessionUUID = new UUID(UUIDGen.createTime(calendar
                    .getTimeInMillis()), UUIDGen.getClockSeqAndNode());

            ChatSession chatSession = new ChatSession();
            chatSessions.add(chatSession);
            chatSession.setChatSessionUUID(chatSessionUUID);

            chatSession.setOwnerID(senderIDs.get(random.nextInt(sendersCount)));
            chatSession.setUserID(senderIDs.get(random.nextInt(sendersCount)));

            int totalMessages = random.nextInt(maxMessagesPerSession);

            int ownerSeqNum = 0;
            int userSeqNum = 0;

            for (int i = 0; i < totalMessages; i++) {
                calendar.add(Calendar.MILLISECOND, random.nextInt(1000));

                Message message = new Message(calendar.getTime());

                String senderId = null;
                if (random.nextInt(2) == 0) {
                    senderId = chatSession.getOwnerID();
                    message.setSequenceNumber(ownerSeqNum++);
                } else {
                    senderId = chatSession.getUserID();
                    message.setSequenceNumber(userSeqNum++);
                }

                message.setSenderId(senderId);
                message.setChatSessionUUID(chatSession.getChatSessionUUID());
                message.setText(RandomStringUtils.randomAscii(2000));

                int event = random.nextInt(3);
                if (event == 0) {
                    message.setLastEvent(MessageEvents.DELIVERED);
                } else if (event == 1) {
                    message.setLastEvent(MessageEvents.DISPLAYED);
                } else if (event == 1) {
                    message.setLastEvent(MessageEvents.OFFLINE);
                }

                try {
                    CassandraUtils.insert(message);
                } catch (ConnectionException e) {
                    e.printStackTrace();
                }
            }

        }

    }

}
