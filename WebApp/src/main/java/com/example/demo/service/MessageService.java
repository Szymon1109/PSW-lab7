package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message addMessage(Message message) {
        String subject = message.getSubject();
        String text = message.getText();
        User user = message.getStudent();
        Lesson lesson = message.getLesson();
        Message newMessage = null;

        if (subject != null && text != null && user != null && lesson != null) {
            newMessage = new Message();
            newMessage.setSubject(subject);
            newMessage.setText(text);
            newMessage.setStudent(user);
            newMessage.setLesson(lesson);
            newMessage = messageRepository.save(message);
        }
        return newMessage;
    }

    public Message updateMessage(Message message) {
        String subject = message.getSubject();
        String text = message.getText();
        Long id = message.getId();
        Message updateMessage = messageRepository.findById(id).orElse(null);

        if (updateMessage != null && subject != null && text != null) {
            updateMessage.setSubject(subject);
            updateMessage.setText(text);
            updateMessage = messageRepository.save(updateMessage);
        }
        return updateMessage;
    }

    public boolean deleteMessage(Long id) {
        boolean success = false;
        Message message = messageRepository.findById(id).orElse(null);
        if (message != null) {
            messageRepository.delete(message);
            success = true;
        }
        return success;
    }

}
