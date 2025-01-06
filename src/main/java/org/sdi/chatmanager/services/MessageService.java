package org.sdi.chatmanager.services;

import org.sdi.chatmanager.dtos.ConversationResponse;
import org.sdi.chatmanager.dtos.CreateMessageRequest;
import org.sdi.chatmanager.dtos.MessageResponse;
import org.sdi.chatmanager.entities.Message;
import org.springframework.stereotype.Component;
import org.sdi.chatmanager.dtos.PatchMessageRequest;
import java.util.List;

@Component
public interface MessageService {
    Message createMessage(CreateMessageRequest createMessageRequest);
    List<MessageResponse> getConversation(Long userId1, Long recipientId);
    Message deleteMessage(Long messageId);
    MessageResponse patchMessage(Long messageId, PatchMessageRequest patchMessageRequest);
    List<ConversationResponse> getConversations(Long userId);
}
