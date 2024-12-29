package org.sdi.chatmanager.services;

import org.sdi.chatmanager.dtos.CreateGroupMessageRequest;
import org.sdi.chatmanager.dtos.GroupMessageResponse;
import org.sdi.chatmanager.dtos.PatchGroupMessageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface GroupMessageService {

    void createGroupMessage(CreateGroupMessageRequest request);
    List<GroupMessageResponse> getGroupMessages(Long groupId);
    void deleteGroupMessage(Long messageId);
    GroupMessageResponse patchGroupMessage(Long messageId, PatchGroupMessageRequest request);
}
