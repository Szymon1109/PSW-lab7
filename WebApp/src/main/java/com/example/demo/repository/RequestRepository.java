package com.example.demo.repository;

import com.example.demo.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByStudent(User student);

    List<Request> findAllByCourse(Course course);

    @Transactional
    void deleteAllByCourse(Course course);

}
