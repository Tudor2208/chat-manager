package org.sdi.chatmanager.controllers;

import jakarta.validation.Valid;
import org.sdi.chatmanager.dtos.ConversationResponse;
import org.sdi.chatmanager.dtos.CreateMessageRequest;
import org.sdi.chatmanager.dtos.MessageResponse;
import org.sdi.chatmanager.dtos.PatchMessageRequest;
import org.sdi.chatmanager.services.MessageService;
import org.sdi.chatmanager.websocket.ChatWebSocketHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@Validated
@CrossOrigin
public class MessageController {

    private final MessageService messageService;
    private final ChatWebSocketHandler chatWebSocketHandler;

    public MessageController(MessageService messageService, ChatWebSocketHandler chatWebSocketHandler) {
        this.messageService = messageService;
        this.chatWebSocketHandler = chatWebSocketHandler;
    }

    @PostMapping
    public ResponseEntity<Void> createMessage(@RequestBody @Valid CreateMessageRequest createMessageRequest) {
        var message = messageService.createMessage(createMessageRequest);
        MessageResponse messageResponse = new MessageResponse(
                message.getId(),
                message.getSender().getId(),
                message.getRecipient().getId(),
                message.getText(),
                message.getTimestamp(),
                message.isEdited()
        );
        chatWebSocketHandler.sendMessage(createMessageRequest.getSenderId(), ChatWebSocketHandler.MessageType.DIRECT_MESSAGE_CREATE, messageResponse);
        chatWebSocketHandler.sendMessage(createMessageRequest.getRecipientId(), ChatWebSocketHandler.MessageType.DIRECT_MESSAGE_CREATE, messageResponse);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/conversation")
    public ResponseEntity<List<MessageResponse>> getConversation(@RequestParam("userId1") Long userId1,
                                                                 @RequestParam("userId2") Long userId2) {
        return ResponseEntity.ok(messageService.getConversation(userId1, userId2));
    }

    @GetMapping("/conversations/{userId}")
    public ResponseEntity<List<ConversationResponse>> getAllConversations(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(messageService.getConversations(userId));
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        var message = messageService.deleteMessage(messageId);
        chatWebSocketHandler.sendMessage(message.getSender().getId(), ChatWebSocketHandler.MessageType.DIRECT_MESSAGE_DELETE, messageId);
        chatWebSocketHandler.sendMessage(message.getRecipient().getId(), ChatWebSocketHandler.MessageType.DIRECT_MESSAGE_DELETE, messageId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{messageId}")
    public ResponseEntity<MessageResponse> patchMessage(@PathVariable Long messageId,
                                                        @RequestBody @Valid PatchMessageRequest patchMessageRequest) {
        var patchedMessage = messageService.patchMessage(messageId, patchMessageRequest);
        chatWebSocketHandler.sendMessage(patchedMessage.getSenderId(), ChatWebSocketHandler.MessageType.DIRECT_MESSAGE_PATCH, patchedMessage);
        chatWebSocketHandler.sendMessage(patchedMessage.getRecipientId(), ChatWebSocketHandler.MessageType.DIRECT_MESSAGE_PATCH, patchedMessage);
        return ResponseEntity.ok(patchedMessage);
    }
}
