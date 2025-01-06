package org.sdi.chatmanager.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.utils.CopyOnWriteMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final CopyOnWriteMap<Long, WebSocketSession> sessions;

    public ChatWebSocketHandler() {
        sessions = new CopyOnWriteMap<>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("New connection: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        sessions.put(Long.valueOf(message.getPayload()), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.forEach((userId, userSession) -> {
            if (userSession.getId().equals(session.getId())) {
                sessions.remove(userId);
            }
        });
        System.out.println("Connection closed: " + session.getId());
    }

    public void sendMessage(Long userId, MessageType messageType, Object message) {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Message messageToSend = new Message(messageType, message);
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(messageToSend)));
            } catch (Exception e) {
                System.out.println("Error sending message to user: " + userId);
            }
        }
    }

    private record Message(MessageType type, Object message) {
    }

    public enum MessageType {

        DIRECT_MESSAGE_CREATE,
        DIRECT_MESSAGE_DELETE,
        DIRECT_MESSAGE_PATCH,
        GROUP_MESSAGE_CREATE,
        GROUP_MESSAGE_DELETE,
        GROUP_MESSAGE_PATCH
    }
}
