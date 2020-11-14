package com.example.demo.controller;

import com.example.demo.model.Lesson;
import com.example.demo.model.LessonSeries;
import com.example.demo.repository.LessonSeriesRepository;
import com.example.demo.service.LessonSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/rest/series")
public class LessonSeriesController {

    private LessonSeriesService lessonSeriesService;
    private LessonSeriesRepository lessonSeriesRepository;

    @Autowired
    public LessonSeriesController(LessonSeriesService lessonSeriesService,
                                  LessonSeriesRepository lessonSeriesRepository) {
        this.lessonSeriesService = lessonSeriesService;
        this.lessonSeriesRepository = lessonSeriesRepository;
    }

    @GetMapping("/all/{name}")
    @ResponseBody
    public List<LessonSeries> findAllByName(@PathVariable String name) {
        return lessonSeriesRepository.findAllByName(name);
    }

    @GetMapping("/all/byLesson")
    @ResponseBody
    public List<LessonSeries> findAllByLessonsContains(@RequestBody Lesson lesson) {
        return lessonSeriesRepository.findAllByLessonsContains(lesson);
    }

    @PostMapping
    public ResponseEntity<LessonSeries> addLessonSeries(@RequestBody LessonSeries lessonSeries) {
        LessonSeries newLessonSeries = lessonSeriesService.addLessonSeries(lessonSeries);
        if (newLessonSeries != null) {
            return new ResponseEntity<>(newLessonSeries, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    public ResponseEntity<LessonSeries> editLessonSeries(@RequestBody LessonSeries lessonSeries) {
        LessonSeries updateLessonSeries = lessonSeriesService.updateLessonSeries(lessonSeries);
        if (updateLessonSeries != null) {
            return new ResponseEntity<>(updateLessonSeries, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping
    public ResponseEntity deleteLessonSeries(@RequestParam Long id) {
        boolean success = lessonSeriesService.deleteLessonSeries(id);
        if (success) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
