package com.example.demo.repository;

import com.example.demo.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonSeriesRepository extends JpaRepository<LessonSeries, Long> {

    List<LessonSeries> findAllByName(String name);

    List<LessonSeries> findAllByLessonsContains(Lesson lesson);

}
