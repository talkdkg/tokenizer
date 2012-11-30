package com.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.StopWatch;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

/**
 * 
 */
public class App {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(App.class);
    private static Random random = new Random();
    private static final String CHANNEL_ID_PREFIX = "channel_";
    private static final String CHANNEL_OWNER_ID_PREFIX = "channel_owner_";
    private static final String CHANNEL_SUBSCRIBER_ID_PREFIX = "channel_subscriber_";
    private static final String US_START = "*******************************************************************";
    private static final String US_END = US_START;

    private static TreeSet<ChatSession> chatSessions = new TreeSet<ChatSession>();

    public static void main(final String[] args) throws ConnectionException,
            InterruptedException {
        CassandraUtils.setup();
        /*
         * remove an inactive chat session -
         * 
         * 
         * - mark a chat message to read -
         */

        LOG.info("UC-001: insert an active chat session");
        testCreateSession(1000);
        LOG.info(US_END);

        LOG.info("UC-002: get all chat sessions for a channel ID");
        testGetActiveChatSessionsUUIDs(1000);
        LOG.info(US_END);

        LOG.info("UC-003: count active chat sessions for a channel ID");
        testCountActiveChatSessions(1000);
        LOG.info(US_END);

        LOG.info("UC-004: get active chat sessions between 2 dates");
        testGetActiveChatSessionsBetweenTwoDates();
        LOG.info(US_END);

        LOG.info("UC-005: get active chat sessions after a date");
        testGetActiveChatSessionsAfterDate();
        LOG.info(US_END);

        LOG.info("UC-006: close an active chat session");
        testCloseActiveChatSession();
        LOG.info(US_END);

        LOG.info("UC-007: insert a chat message");
        testCreateMessage(100);
        LOG.info(US_END);

        LOG.info("UC-008: get all chat messages using a chat session ID");
        testGetAllChatMessagesBySession();
        LOG.info(US_END);

        LOG.info("UC-009: get all chat messages using a list of 10 chat session IDs");
        testGetAllChatMessagesBySessionList(10);
        LOG.info(US_END);

        LOG.info("UC-010: mark a chat message as \"delivered\", parameters: SenderID, SequenceNum");
        testMarkDelivered();
        LOG.info(US_END);

        LOG.info("UC-010-B: mark a chat message as \"delivered\", parameters: Message UUID Type 1");
        testMarkDeliveredBYUUID();
        LOG.info(US_END);

        LOG.info("UC-011: get chat message UUIDs from a sender using 2 sequence numbers");
        testGetChatMessageUUIDsByTwoSequenceNumbers(100);
        LOG.info(US_END);

        LOG.info("UC-012: get chat message TEXTs (random text up to 2000 bytes) from a sender using 2 sequence numbers");
        testGetChatMessagesByTwoSequenceNumbers(100);
        LOG.info(US_END);

        LOG.info("UC-013: get the last sequence number from a sender using Chat Session ID");
        testGetLastSequenceNumberBySender();
        LOG.info(US_END);

        LOG.info("UC-014: delete all messages using inactive session UUID");
        testDeleteMessagesBySessionUUID();
        LOG.info(US_END);

        // RandomMessageGenerator r = new RandomMessageGenerator(10, 10, 10);
    }

    public static void testCreateSession(int count) throws ConnectionException {
        StopWatch clock = new StopWatch();
        for (int i = 0; i < count; i++) {
            chatSessions.add(randomChatSession());
        }
        clock.start();
        for (ChatSession chatSession : chatSessions) {
            CassandraUtils.insertActiveChatSession(chatSession);
        }
        clock.stop();
        long time = clock.getTime();
        float average = (float) time / count;
        LOG.info("Total time: {}ms", time);
        LOG.info("Total transactions: {}", count);
        LOG.info("Average time per transaction: {}", average);
    }

    private static ChatSession randomChatSession() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -random.nextInt(1000000));
        calendar.add(Calendar.MILLISECOND, -random.nextInt(1000000));
        // LOG.debug("Random date created: {}", calendar.getTime());

        ChatSession chatSession = new ChatSession(calendar.getTime());

        calendar.add(Calendar.MINUTE, random.nextInt(1000));
        calendar.add(Calendar.MILLISECOND, random.nextInt(1000));
        chatSession.setSessionEndTimestamp(calendar.getTime());

        chatSession.setChannelId(CHANNEL_ID_PREFIX
                + RandomStringUtils.randomAscii(1));
        chatSession.setOwnerID(CHANNEL_OWNER_ID_PREFIX
                + RandomStringUtils.randomAscii(2));
        chatSession.setUserID(CHANNEL_SUBSCRIBER_ID_PREFIX
                + RandomStringUtils.randomAscii(2));
        return chatSession;
    }

    private static void testGetActiveChatSessionsUUIDs(int count)
            throws ConnectionException {
        StopWatch clock = new StopWatch();
        int total = 0;
        clock.start();
        for (int i = 0; i < count; i++) {
            String channelId = CHANNEL_ID_PREFIX
                    + RandomStringUtils.randomAscii(2);
            // System.out.println(i);
            total += CassandraUtils.getActiveChatSessionsUUIDs(channelId)
                    .size();
        }
        clock.stop();
        long time = clock.getTime();
        float average = (float) time / count;
        LOG.info("Total time: {}ms", time);
        LOG.info("Total transactions: {}", count);
        LOG.info("Average time per transaction: {}", average);
    }

    private static void testCountActiveChatSessions(int count)
            throws ConnectionException {
        StopWatch clock = new StopWatch();
        clock.start();
        for (int i = 0; i < count; i++) {
            String channelId = CHANNEL_ID_PREFIX
                    + RandomStringUtils.randomAscii(2);
            CassandraUtils.countActiveChatSessions(channelId);
        }
        clock.stop();
        long time = clock.getTime();
        float average = (float) time / count;
        LOG.info("Total time: {}ms", time);
        LOG.info("Total transactions: {}", count);
        LOG.info("Average time per transaction: {}", average);
    }

    private static void testGetActiveChatSessionsBetweenTwoDates()
            throws ConnectionException {

        Object[] chatSessionsArray = chatSessions.toArray();

        StopWatch clock = new StopWatch();
        clock.start();
        int transaction = 0;
        for (int i = 0; i < chatSessionsArray.length; i++) {

            ChatSession start = (ChatSession) chatSessionsArray[i];
            for (int k = i; k < chatSessionsArray.length; k++) {
                ChatSession end = (ChatSession) chatSessionsArray[k];

                if (!end.getChannelId().equals(start.getChannelId()))
                    break;
                if (transaction > 10000)
                    break;
                transaction++;
                int count = CassandraUtils.getActiveChatSessions(
                        start.getChannelId(), start.getStartDate(),
                        end.getStartDate()).size();
            }

        }
        clock.stop();
        long time = clock.getTime();
        float average = (float) time / transaction;
        LOG.info("Total time: {}ms", time);
        LOG.info("Total transactions: {}", transaction);
        LOG.info("Average time per transaction: {}", average);
    }

    private static void testGetActiveChatSessionsAfterDate()
            throws ConnectionException {

        Object[] chatSessionsArray = chatSessions.toArray();

        StopWatch clock = new StopWatch();
        clock.start();
        int transaction = 0;
        for (int i = 0; i < chatSessionsArray.length; i++) {

            ChatSession start = (ChatSession) chatSessionsArray[i];
            if (transaction > 10000)
                break;
            transaction++;
            int count = CassandraUtils.getActiveChatSessions(
                    start.getChannelId(), start.getStartDate()).size();
        }

        clock.stop();
        long time = clock.getTime();
        float average = (float) time / transaction;
        LOG.info("Total time: {}ms", time);
        LOG.info("Total transactions: {}", transaction);
        LOG.info("Average time per transaction: {}", average);
    }

    public static void testCloseActiveChatSession() throws ConnectionException {
        StopWatch clock = new StopWatch();
        clock.start();
        int count = 0;
        for (ChatSession chatSession : chatSessions) {

            CassandraUtils.updateActiveChatSession(chatSession.getChannelId(),
                    chatSession.getChatSessionUUID(),
                    chatSession.getSessionEndTimestamp());

            CassandraUtils.insertArchiveChatSession(chatSession.getChannelId(),
                    chatSession.getChatSessionUUID(),
                    chatSession.getSessionEndTimestamp());

            CassandraUtils.deleteActiveChatSession(chatSession.getChannelId(),
                    chatSession.getChatSessionUUID());
            count++;

        }
        clock.stop();
        long time = clock.getTime();
        float average = (float) time / count;
        LOG.info("Total time: {}ms", time);
        LOG.info("Total transactions: {}", count);
        LOG.info("Average time per transaction: {}", average);
    }

    public static void testCreateMessage(int maxMessages)
            throws ConnectionException {
        StopWatch clock = new StopWatch();
        clock.start();
        int count = 0;
        for (ChatSession chatSession : chatSessions) {
            List<Message> messages = randomMessage(chatSession, maxMessages);

            for (Message message : messages) {
                CassandraUtils.insert(message);
                count++;
            }
        }
        clock.stop();
        long time = clock.getTime();
        float average = (float) time / count;
        LOG.info("Total time: {}ms", time);
        LOG.info("Total transactions: {}", count);
        LOG.info("Average time per transaction: {}", average);
    }

    private static List<Message> randomMessage(ChatSession chatSession,
            int maxMessages) {

        int totalMessages = random.nextInt(maxMessages);

        int ownerSeqNum = 0;
        int userSeqNum = 0;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(chatSession.getStartDate());

        List<Message> chatMessages = new ArrayList<Message>();

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
            } else if (event == 2) {
                message.setLastEvent(MessageEvents.OFFLINE);
            }

            chatMessages.add(message);
        }

        chatSession.setMessages(chatMessages);

        return chatMessages;
    }

    public static void testGetAllChatMessagesBySession()
            throws ConnectionException {

        StopWatch clock = new StopWatch();
        clock.start();
        int count = 0;

        for (ChatSession chatSession : chatSessions) {
            CassandraUtils.getMessages(chatSession);
            count++;
        }

        clock.stop();
        long time = clock.getTime();
        float average = (float) time / count;
        LOG.info("Total time: {}ms", time);
        LOG.info("Total transactions: {}", count);
        LOG.info("Average time per transaction: {}", average);
    }

    public static void testGetAllChatMessagesBySessionList(int sessionListSize)
            throws ConnectionException {

        StopWatch clock = new StopWatch();
        clock.start();
        int count = 0;

        Object[] array = chatSessions.toArray();

        for (ChatSession chatSession : chatSessions) {

            Set<UUID> sessions = new TreeSet<UUID>();

            for (int i = 0; i < sessionListSize; i++) {

                int index = random.nextInt(chatSessions.size());
                sessions.add(((ChatSession) array[index]).getChatSessionUUID());
            }

            CassandraUtils.getMessages(sessions);

            count++;
        }

        clock.stop();
        long time = clock.getTime();
        float average = (float) time / count;
        LOG.info("Total time: {}ms", time);
        LOG.info("Total transactions: {}", count);
        LOG.info("Average time per transaction: {}ms", average);
    }

    public static void testMarkDelivered() throws ConnectionException {

        StopWatch clock = new StopWatch();
        clock.start();
        int count = 0;

        for (ChatSession chatSession : chatSessions) {

            for (Message message : chatSession.getMessages()) {
                CassandraUtils.updateMessageEvent(message);
                count++;
            }

        }

        clock.stop();
        long time = clock.getTime();
        float average = (float) time / count;
        LOG.info("Total time: {}ms", time);
        LOG.info("Total transactions: {}", count);
        LOG.info("Average time per transaction: {}ms", average);
    }

    public static void testMarkDeliveredBYUUID() throws ConnectionException {

        StopWatch clock = new StopWatch();
        clock.start();
        int count = 0;

        for (ChatSession chatSession : chatSessions) {

            for (Message message : chatSession.getMessages()) {
                CassandraUtils.updateMessageEventByUUID(
                        message.getChatSessionUUID(), message.getUuid(),
                        message.getLastEvent());

                count++;
            }

        }

        clock.stop();
        long time = clock.getTime();
        float average = (float) time / count;
        LOG.info("Total time: {}ms", time);
        LOG.info("Total transactions: {}", count);
        LOG.info("Average time per transaction: {}ms", average);
    }

    public static void testGetChatMessageUUIDsByTwoSequenceNumbers(int maxNumber)
            throws ConnectionException {

        StopWatch clock = new StopWatch();
        clock.start();
        int count = 0;
        int countNonEmptyResults = 0;

        for (ChatSession chatSession : chatSessions) {

            int start = random.nextInt(50);
            int end = start + random.nextInt(50);
            String senderId = chatSession.getOwnerID();
            if (random.nextInt(2) == 0) {
                senderId = chatSession.getUserID();
            }

            List<Message> messageKeys = CassandraUtils.getMessageKeys(
                    chatSession.getChatSessionUUID(), senderId, start, end);

            if (messageKeys.size() > 0) {
                countNonEmptyResults++;
            }
            count++;

        }

        clock.stop();
        long time = clock.getTime();
        float average = (float) time / count;
        LOG.info("Total time: {}ms", time);
        LOG.info("Total transactions: {}", count);
        LOG.info("Transactions with non-empty results: {}",
                countNonEmptyResults);
        LOG.info("Average time per transaction: {}", average);
    }

    public static void testGetChatMessagesByTwoSequenceNumbers(int maxNumber)
            throws ConnectionException {

        StopWatch clock = new StopWatch();
        clock.start();
        int count = 0;
        int countNonEmptyResults = 0;

        for (ChatSession chatSession : chatSessions) {

            int start = random.nextInt(50);
            int end = start + random.nextInt(50);
            String senderId = chatSession.getOwnerID();
            if (random.nextInt(2) == 0) {
                senderId = chatSession.getUserID();
            }

            List<Message> messageKeys = CassandraUtils.getMessageKeys(
                    chatSession.getChatSessionUUID(), senderId, start, end);

            List<Message> messages = CassandraUtils.getMessagesByKeys(
                    chatSession.getChatSessionUUID(), messageKeys);

            if (messages.size() > 0) {
                countNonEmptyResults++;
            }
            count++;

        }

        clock.stop();
        long time = clock.getTime();
        float average = (float) time / count;
        LOG.info("Total time: {}ms", time);
        LOG.info("Total transactions: {}", count);
        LOG.info("Transactions with non-empty results: {}",
                countNonEmptyResults);
        LOG.info("Average time per transaction: {}", average);
    }

    public static void testGetLastSequenceNumberBySender()
            throws ConnectionException {

        StopWatch clock = new StopWatch();
        clock.start();
        int count = 0;

        for (ChatSession chatSession : chatSessions) {

            List<Message> messageKeys = CassandraUtils.getMessageKeys(
                    chatSession.getChatSessionUUID(), chatSession.getOwnerID(),
                    0, Integer.MAX_VALUE);

            if (!messageKeys.isEmpty()) {
                int lastSequenceNumber = messageKeys
                        .get(messageKeys.size() - 1).getSequenceNumber();
                LOG.info("Last sequence number for {}: {}",
                        chatSession.getOwnerID(), lastSequenceNumber);
            }

            count++;

        }

        clock.stop();
        long time = clock.getTime();
        float average = (float) time / count;
        LOG.info("Total time: {}ms", time);
        LOG.info("Total transactions: {}", count);
        LOG.info("Average time per transaction: {}", average);
    }

    public static void testDeleteMessagesBySessionUUID()
            throws ConnectionException {
        StopWatch clock = new StopWatch();
        clock.start();
        int count = 0;
        for (ChatSession chatSession : chatSessions) {

            CassandraUtils.deleteChatSessionMessages(chatSession
                    .getChatSessionUUID());
            count++;

        }
        clock.stop();
        long time = clock.getTime();
        float average = (float) time / count;
        LOG.info("Total time: {}ms", time);
        LOG.info("Total transactions: {}", count);
        LOG.info("Average time per transaction: {}", average);
    }

}
