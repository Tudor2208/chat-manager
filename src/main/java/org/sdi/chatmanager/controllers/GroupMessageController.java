package org.sdi.chatmanager.controllers;

import jakarta.validation.Valid;
import org.sdi.chatmanager.dtos.CreateGroupMessageRequest;
import org.sdi.chatmanager.dtos.GroupMessageResponse;
import org.sdi.chatmanager.dtos.PatchGroupMessageRequest;
import org.sdi.chatmanager.services.GroupMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/v1/group-messages")
public class GroupMessageController {

    private final GroupMessageService groupMessageService;

    public GroupMessageController(GroupMessageService groupMessageService) {
        this.groupMessageService = groupMessageService;
    }

    @PostMapping
    public ResponseEntity<Void> createMessage(@RequestBody @Valid CreateGroupMessageRequest request) {
        groupMessageService.createGroupMessage(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<List<GroupMessageResponse>> getGroupMessages(@PathVariable("groupId") Long groupId) {
        return ResponseEntity.ok(groupMessageService.getGroupMessages(groupId));
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteGroupMessage(@PathVariable("messageId") Long messageId) {
        groupMessageService.deleteGroupMessage(messageId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{messageId}")
    public ResponseEntity<GroupMessageResponse> patchGroupMessage(@PathVariable("messageId") Long messageId,
                                                                  @RequestBody @Valid PatchGroupMessageRequest request) {
        return ResponseEntity.ok(groupMessageService.patchGroupMessage(messageId, request));
    }
}
