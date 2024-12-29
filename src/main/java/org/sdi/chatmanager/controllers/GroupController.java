package org.sdi.chatmanager.controllers;

import jakarta.validation.Valid;
import org.sdi.chatmanager.dtos.CreateGroupRequest;
import org.sdi.chatmanager.dtos.GroupResponse;
import org.sdi.chatmanager.dtos.PatchGroupRequest;
import org.sdi.chatmanager.services.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
@Validated
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<Void> createGroup(@RequestBody @Valid CreateGroupRequest createGroupRequest) {
        groupService.createGroup(createGroupRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupResponse> getGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupService.getGroup(groupId));
    }

    @PostMapping("/{groupId}/add-member")
    public ResponseEntity<GroupResponse> addMember(@PathVariable("groupId") Long groupId,
                                                   @RequestParam("userId") Long userId) {
        return ResponseEntity.ok(groupService.addMember(groupId, userId));
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long groupId) {
        groupService.deleteGroup(groupId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{groupId}/remove-member")
    public ResponseEntity<Void> removeMember(@PathVariable("groupId") Long groupId,
                                             @RequestParam("userId") Long userId) {
        groupService.removeMember(groupId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<GroupResponse> patchGroup(@PathVariable("groupId") Long groupId,
                                                    @RequestBody @Valid PatchGroupRequest patchGroupRequest) {
        return ResponseEntity.ok(groupService.patchGroup(groupId, patchGroupRequest));
    }

    @GetMapping
    public ResponseEntity<List<GroupResponse>> getUsersGroups(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(groupService.getUsersGroups(userId));
    }
}
