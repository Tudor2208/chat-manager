package org.sdi.chatmanager.services.impl;

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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
        messageRepository.save(message);
    }

    @Override
    public List<MessageResponse> getConversation(Long senderId, Long recipientId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("User with ID " + senderId + " not found"));

        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new NotFoundException("User with ID " + recipientId + " not found"));

        List<Message> allBySenderAndRecipient = messageRepository.findAllBySenderAndRecipient(sender, recipient);
        List<Message> allByRecipientAndSender = messageRepository.findAllBySenderAndRecipient(recipient, sender);

        List<Message> combinedMessages = new ArrayList<>();
        combinedMessages.addAll(allBySenderAndRecipient);
        combinedMessages.addAll(allByRecipientAndSender);

        return combinedMessages.stream()
                .sorted(Comparator.comparing(Message::getTimestamp))
                .map(message -> new MessageResponse(
                        message.getId(),
                        message.getSender(),
                        message.getRecipient(),
                        message.getText(),
                        message.getTimestamp()
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
        message.setTimestamp(new Date());
        messageRepository.save(message);
        return new MessageResponse(
                message.getId(),
                message.getSender(),
                message.getRecipient(),
                message.getText(),
                message.getTimestamp()
        );
    }
}
