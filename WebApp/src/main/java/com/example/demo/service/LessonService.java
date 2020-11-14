package com.example.demo.service;

import com.example.demo.model.Lesson;
import com.example.demo.model.User;
import com.example.demo.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class LessonService {

    private LessonRepository lessonRepository;

    @Autowired
    public LessonService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    public Lesson addLesson(Lesson lesson) {
        String subject = lesson.getSubject();
        LocalDate date = lesson.getDate();
        User user = lesson.getTrainer();
        Lesson newLesson = null;

        if (subject != null && date != null && user != null) {
            newLesson = new Lesson();
            newLesson.setSubject(subject);
            newLesson.setDate(date);
            newLesson.setTrainer(user);
            newLesson = lessonRepository.save(newLesson);
        }
        return newLesson;
    }

    public Lesson updateLesson(Lesson lesson) {
        String subject = lesson.getSubject();
        LocalDate date = lesson.getDate();
        Long id = lesson.getId();
        Lesson updateLesson = lessonRepository.findById(id).orElse(null);

        if (updateLesson != null && subject != null && date != null) {
            updateLesson.setSubject(subject);
            updateLesson.setDate(date);
            updateLesson = lessonRepository.save(updateLesson);
        }
        return updateLesson;
    }

    public boolean deleteLesson(Long id) {
        boolean success = false;
        Lesson lesson = lessonRepository.findById(id).orElse(null);
        if (lesson != null) {
            lessonRepository.delete(lesson);
            success = true;
        }
        return success;
    }

}
