package com.example.demo.controller;

import com.example.demo.model.Lesson;
import com.example.demo.model.Message;
import com.example.demo.model.User;
import com.example.demo.repository.MessageRepository;
import com.example.demo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/rest/message")
public class MessageController {

    private MessageService messageService;
    private MessageRepository messageRepository;

    @Autowired
    public MessageController(MessageService messageService,
                             MessageRepository messageRepository) {
        this.messageService = messageService;
        this.messageRepository = messageRepository;
    }

    @GetMapping("/all/byStudent")
    @ResponseBody
    public List<Message> findAllByStudent(@RequestBody User user) {
        return messageRepository.findAllByStudent(user);
    }

    @GetMapping("/all/byLesson")
    @ResponseBody
    public List<Message> findAllByLesson(@RequestBody Lesson lesson) {
        return messageRepository.findAllByLesson(lesson);
    }

    @PostMapping
    public ResponseEntity<Message> addMessage(@RequestBody Message message) {
        Message newMessage = messageService.addMessage(message);
        if (newMessage != null) {
            return new ResponseEntity<>(newMessage, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    public ResponseEntity<Message> editMessage(@RequestBody Message message) {
        Message updateMessage = messageService.updateMessage(message);
        if (updateMessage != null) {
            return new ResponseEntity<>(updateMessage, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping
    public ResponseEntity deleteMessage(@RequestParam Long id) {
        boolean success = messageService.deleteMessage(id);
        if (success) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
