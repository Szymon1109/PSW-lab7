package com.example.demo.repository;

import com.example.demo.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findAllByName(String name);

    List<Course> findAllByLessonSeriesListContains(LessonSeries lessonSeries);

}
