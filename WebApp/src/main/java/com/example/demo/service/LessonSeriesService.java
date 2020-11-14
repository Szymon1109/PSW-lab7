package com.example.demo.service;

import com.example.demo.model.Lesson;
import com.example.demo.model.LessonSeries;
import com.example.demo.repository.LessonSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonSeriesService {

    private LessonSeriesRepository lessonSeriesRepository;

    @Autowired
    public LessonSeriesService(LessonSeriesRepository lessonSeriesRepository) {
        this.lessonSeriesRepository = lessonSeriesRepository;
    }

    public LessonSeries addLessonSeries(LessonSeries lessonSeries) {
        String name = lessonSeries.getName();
        List<Lesson> lessons = lessonSeries.getLessons();
        LessonSeries newLessonSeries = null;

        if (name != null && lessons != null) {
            newLessonSeries = new LessonSeries();
            newLessonSeries.setName(name);
            newLessonSeries.setLessons(lessons);
            newLessonSeries = lessonSeriesRepository.save(newLessonSeries);
        }
        return newLessonSeries;
    }

    public LessonSeries updateLessonSeries(LessonSeries lessonSeries) {
        String name = lessonSeries.getName();
        List<Lesson> lessons = lessonSeries.getLessons();
        Long id = lessonSeries.getId();
        LessonSeries updateLessonSeries = lessonSeriesRepository.findById(id).orElse(null);

        if (updateLessonSeries != null && name != null && lessons != null) {
            updateLessonSeries.setName(name);
            updateLessonSeries.setLessons(lessons);
            updateLessonSeries = lessonSeriesRepository.save(updateLessonSeries);
        }
        return updateLessonSeries;
    }

    public boolean deleteLessonSeries(Long id) {
        boolean success = false;
        LessonSeries lessonSeries = lessonSeriesRepository.findById(id).orElse(null);
        if (lessonSeries != null) {
            lessonSeriesRepository.delete(lessonSeries);
            success = true;
        }
        return success;
    }

}
