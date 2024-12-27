package org.sdi.chatmanager.services.impl;

import org.sdi.chatmanager.dtos.ConversationResponse;
import org.sdi.chatmanager.dtos.CreateMessageRequest;
import org.sdi.chatmanager.dtos.MessageResponse;
import org.sdi.chatmanager.dtos.PatchMessageRequest;
import org.sdi.chatmanager.entities.Message;
import org.sdi.chatmanager.entities.User;
import org.sdi.chatmanager.exceptions.NotFoundException;
import org.sdi.chatmanager.repositories.MessageRepository;
import org.sdi.chatmanager.repositories.UserRepository;
import org.sdi.chatmanager.services.MessageService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageServiceImpl(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void createMessage(CreateMessageRequest createMessageRequest) {
        User sender = userRepository.findById(createMessageRequest.getSenderId())
                .orElseThrow(() -> new NotFoundException("User with ID " + createMessageRequest.getSenderId() + " not found"));

        User recipient = userRepository.findById(createMessageRequest.getRecipientId())
                .orElseThrow(() -> new NotFoundException("User with ID " + createMessageRequest.getRecipientId() + " not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setText(createMessageRequest.getText());
        message.setTimestamp(new Date());
        message.setEdited(false);
        messageRepository.save(message);
    }

    @Override
    public List<MessageResponse> getConversation(Long userId1, Long userId2) {
        User sender = userRepository.findById(userId1)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId1 + " not found"));

        User recipient = userRepository.findById(userId2)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId2 + " not found"));

        List<Message> allBySenderAndRecipient = messageRepository.findAllBySenderAndRecipient(sender, recipient);
        List<Message> allByRecipientAndSender = messageRepository.findAllBySenderAndRecipient(recipient, sender);

        List<Message> combinedMessages = new ArrayList<>();
        combinedMessages.addAll(allBySenderAndRecipient);
        combinedMessages.addAll(allByRecipientAndSender);

        return combinedMessages.stream()
                .sorted(Comparator.comparing(Message::getTimestamp))
                .map(message -> new MessageResponse(
                        message.getId(),
                        message.getSender().getId(),
                        message.getRecipient().getId(),
                        message.getText(),
                        message.getTimestamp(),
                        message.isEdited()
                ))
                .toList();
    }

    @Override
    public void deleteMessage(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Message with ID " + messageId + " not found"));
        messageRepository.delete(message);
    }

    @Override
    public MessageResponse patchMessage(Long messageId, PatchMessageRequest patchMessageRequest) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Message with ID " + messageId + " not found"));

        message.setText(patchMessageRequest.getText());
        message.setEdited(true);
        messageRepository.save(message);

        return new MessageResponse(
                message.getId(),
                message.getSender().getId(),
                message.getRecipient().getId(),
                message.getText(),
                message.getTimestamp(),
                message.isEdited()
        );
    }

    @Override
    public List<ConversationResponse> getConversations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));

        List<Message> allBySender = messageRepository.findAllBySender(user);
        List<Message> allByRecipient = messageRepository.findAllByRecipient(user);

        List<Message> combinedMessages = new ArrayList<>();
        combinedMessages.addAll(allBySender);
        combinedMessages.addAll(allByRecipient);

        Map<String, List<Message>> conversations = new HashMap<>();
        for (Message message : combinedMessages) {
            String conversationKey = getConversationKey(message, user);
            conversations.computeIfAbsent(conversationKey, k -> new ArrayList<>()).add(message);
        }

        List<ConversationResponse> conversationResponses = new ArrayList<>();
        for (Map.Entry<String, List<Message>> entry : conversations.entrySet()) {
            List<Message> conversationMessages = entry.getValue();

            Message lastMessage = conversationMessages.stream()
                    .max(Comparator.comparing(Message::getTimestamp))
                    .orElseThrow(() -> new RuntimeException("No message found in the conversation"));

            ConversationResponse conversationResponse = getConversationResponse(userId, lastMessage);
            conversationResponses.add(conversationResponse);
        }

        return conversationResponses;
    }

    private static ConversationResponse getConversationResponse(Long userId, Message lastMessage) {
        ConversationResponse conversationResponse = new ConversationResponse();
        conversationResponse.setLastMessage(lastMessage.getText());
        conversationResponse.setLastMessageTimestamp(lastMessage.getTimestamp());
        conversationResponse.setSent(Objects.equals(lastMessage.getSender().getId(), userId));

        if (Objects.equals(lastMessage.getSender().getId(), userId)) {
            conversationResponse.setFirstName(lastMessage.getRecipient().getFirstName());
            conversationResponse.setLastName(lastMessage.getRecipient().getLastName());
            conversationResponse.setFriendId(lastMessage.getRecipient().getId());
        } else {
            conversationResponse.setFirstName(lastMessage.getSender().getFirstName());
            conversationResponse.setLastName(lastMessage.getSender().getLastName());
            conversationResponse.setFriendId(lastMessage.getSender().getId());
        }
        return conversationResponse;
    }

    private String getConversationKey(Message message, User user) {
        if (message.getSender().equals(user)) {
            return message.getRecipient().getId() + "-" + message.getSender().getId();
        } else {
            return message.getSender().getId() + "-" + message.getRecipient().getId();
        }
    }
}
