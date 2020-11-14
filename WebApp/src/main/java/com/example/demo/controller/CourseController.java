package com.example.demo.controller;

import com.example.demo.model.Course;
import com.example.demo.model.LessonSeries;
import com.example.demo.repository.CourseRepository;
import com.example.demo.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/rest/course")
public class CourseController {

    private CourseService courseService;
    private CourseRepository courseRepository;

    @Autowired
    public CourseController(CourseService courseService,
                            CourseRepository courseRepository) {
        this.courseService = courseService;
        this.courseRepository = courseRepository;
    }

    @GetMapping("/all/{name}")
    @ResponseBody
    public List<Course> findAllByName(@PathVariable String name) {
        return courseRepository.findAllByName(name);
    }

    @GetMapping("/all/bySeries")
    @ResponseBody
    public List<Course> findAllByLessonSeries(@RequestBody LessonSeries lessonSeries) {
        return courseRepository.findAllByLessonSeriesListContains(lessonSeries);
    }

    @PostMapping
    public ResponseEntity<Course> addCourse(@RequestBody Course course) {
        Course newCourse = courseService.addCourse(course);
        if (newCourse != null) {
            return new ResponseEntity<>(newCourse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    public ResponseEntity<Course> editCourse(@RequestBody Course course) {
        Course updateCourse = courseService.updateCourse(course);
        if (updateCourse != null) {
            return new ResponseEntity<>(updateCourse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping
    public ResponseEntity deleteCourse(@RequestParam Long id) {
        boolean success = courseService.deleteCourse(id);
        if (success) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
