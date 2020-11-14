package com.example.demo.service;

import com.example.demo.model.Course;
import com.example.demo.model.LessonSeries;
import com.example.demo.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course addCourse(Course course) {
        String name = course.getName();
        List<LessonSeries> lessonsSeries = course.getLessonSeriesList();
        Course newCourse = null;

        if (name != null && lessonsSeries != null) {
            newCourse = new Course();
            newCourse.setName(name);
            newCourse.setLessonSeriesList(lessonsSeries);
            newCourse = courseRepository.save(newCourse);
        }
        return newCourse;
    }

    public Course updateCourse(Course course) {
        String name = course.getName();
        List<LessonSeries> lessonsSeries = course.getLessonSeriesList();
        Long id = course.getId();
        Course updateCourse = courseRepository.findById(id).orElse(null);

        if (updateCourse != null && name != null && lessonsSeries != null) {
            updateCourse.setName(name);
            updateCourse.setLessonSeriesList(lessonsSeries);
            updateCourse = courseRepository.save(updateCourse);
        }
        return updateCourse;
    }

    public boolean deleteCourse(Long id) {
        boolean success = false;
        Course course = courseRepository.findById(id).orElse(null);
        if (course != null) {
            courseRepository.delete(course);
            success = true;
        }
        return success;
    }

}
