package org.sdi.chatmanager.services.impl;

import org.sdi.chatmanager.dtos.CreateGroupMessageRequest;
import org.sdi.chatmanager.dtos.GroupMessageResponse;
import org.sdi.chatmanager.dtos.PatchGroupMessageRequest;
import org.sdi.chatmanager.entities.Group;
import org.sdi.chatmanager.entities.GroupMessage;
import org.sdi.chatmanager.entities.User;
import org.sdi.chatmanager.exceptions.NotFoundException;
import org.sdi.chatmanager.repositories.GroupMessageRepository;
import org.sdi.chatmanager.repositories.GroupRepository;
import org.sdi.chatmanager.repositories.UserRepository;
import org.sdi.chatmanager.services.GroupMessageService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class GroupMessageServiceImpl implements GroupMessageService {

    private final GroupMessageRepository groupMessageRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public GroupMessageServiceImpl(GroupMessageRepository groupMessageRepository, UserRepository userRepository, GroupRepository groupRepository) {
        this.groupMessageRepository = groupMessageRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public void createGroupMessage(CreateGroupMessageRequest request) {
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new NotFoundException("Group with ID " + request.getGroupId() + " not found"));

        User user = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new NotFoundException("User with ID " + request.getSenderId() + " not found"));

        GroupMessage groupMessage = new GroupMessage();
        groupMessage.setEdited(false);
        groupMessage.setTimestamp(new Date());
        groupMessage.setText(request.getText());
        groupMessage.setGroup(group);
        groupMessage.setSender(user);
        groupMessageRepository.save(groupMessage);
    }

    @Override
    public List<GroupMessageResponse> getGroupMessages(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group with ID " + groupId + " not found"));

        List<GroupMessage> allByGroup = groupMessageRepository.findAllByGroup(group);
        return allByGroup.stream()
                .map(message -> {
                    GroupMessageResponse groupMessageResponse = new GroupMessageResponse();
                    groupMessageResponse.setEdited(message.isEdited());
                    groupMessageResponse.setTimestamp(message.getTimestamp());
                    groupMessageResponse.setText(message.getText());
                    groupMessageResponse.setGroupId(message.getGroup().getId());
                    groupMessageResponse.setSenderId(message.getSender().getId());
                    groupMessageResponse.setId(message.getId());
                    return groupMessageResponse;
                })
                .toList();
    }

    @Override
    public void deleteGroupMessage(Long messageId) {
        GroupMessage groupMessage = groupMessageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Group message with ID " + messageId + " not found"));
        groupMessageRepository.delete(groupMessage);
    }

    @Override
    public GroupMessageResponse patchGroupMessage(Long messageId, PatchGroupMessageRequest request) {
        GroupMessage groupMessage = groupMessageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Group message with ID " + messageId + " not found"));
        groupMessage.setText(request.getText());
        groupMessage.setEdited(true);
        groupMessageRepository.save(groupMessage);

        GroupMessageResponse groupMessageResponse = new GroupMessageResponse();
        groupMessageResponse.setId(groupMessage.getId());
        groupMessageResponse.setEdited(groupMessage.isEdited());
        groupMessageResponse.setTimestamp(groupMessage.getTimestamp());
        groupMessageResponse.setText(request.getText());
        groupMessageResponse.setGroupId(groupMessage.getGroup().getId());
        groupMessageResponse.setSenderId(groupMessage.getSender().getId());
        return groupMessageResponse;
    }
}
