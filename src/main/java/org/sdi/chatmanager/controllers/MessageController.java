package org.sdi.chatmanager.controllers;

import jakarta.validation.Valid;
import org.sdi.chatmanager.dtos.ConversationResponse;
import org.sdi.chatmanager.dtos.CreateMessageRequest;
import org.sdi.chatmanager.dtos.MessageResponse;
import org.sdi.chatmanager.dtos.PatchMessageRequest;
import org.sdi.chatmanager.services.MessageService;
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

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<Void> createMessage(@RequestBody @Valid CreateMessageRequest createMessageRequest) {
        messageService.createMessage(createMessageRequest);
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
        messageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{messageId}")
    public ResponseEntity<MessageResponse> patchMessage(@PathVariable Long messageId,
                                                        @RequestBody @Valid PatchMessageRequest patchMessageRequest) {
        return ResponseEntity.ok(messageService.patchMessage(messageId, patchMessageRequest));
    }
}
