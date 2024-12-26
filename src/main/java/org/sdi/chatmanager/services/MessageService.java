package org.sdi.chatmanager.services;

import org.sdi.chatmanager.dtos.CreateMessageRequest;
import org.sdi.chatmanager.dtos.MessageResponse;
import org.springframework.stereotype.Component;
import org.sdi.chatmanager.dtos.PatchMessageRequest;
import java.util.List;

@Component
public interface MessageService {
    void createMessage(CreateMessageRequest createMessageRequest);
    List<MessageResponse> getConversation(Long senderId, Long recipientId);
    void deleteMessage(Long messageId);
    MessageResponse patchMessage(Long messageId, PatchMessageRequest patchMessageRequest);
}
