package com.example.demo.controller;

import com.example.demo.model.Lesson;
import com.example.demo.model.User;
import com.example.demo.repository.LessonRepository;
import com.example.demo.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/rest/lesson")
public class LessonController {

    private LessonService lessonService;
    private LessonRepository lessonRepository;

    @Autowired
    public LessonController(LessonService lessonService,
                            LessonRepository lessonRepository) {
        this.lessonService = lessonService;
        this.lessonRepository = lessonRepository;
    }

    @GetMapping("/all/{subject}")
    @ResponseBody
    public List<Lesson> findAllBySubject(@PathVariable String subject) {
        return lessonRepository.findAllBySubject(subject);
    }

    @GetMapping("/all/byTrainer")
    @ResponseBody
    public List<Lesson> findAllByTrainer(@RequestBody User user) {
        return lessonRepository.findAllByTrainer(user);
    }

    @PostMapping
    public ResponseEntity<Lesson> addLesson(@RequestBody Lesson lesson) {
        Lesson newLesson = lessonService.addLesson(lesson);
        if (newLesson != null) {
            return new ResponseEntity<>(newLesson, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    public ResponseEntity<Lesson> editLesson(@RequestBody Lesson lesson) {
        Lesson updateLesson = lessonService.updateLesson(lesson);
        if (updateLesson != null) {
            return new ResponseEntity<>(updateLesson, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping
    public ResponseEntity deleteLesson(@RequestParam Long id) {
        boolean success = lessonService.deleteLesson(id);
        if (success) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
