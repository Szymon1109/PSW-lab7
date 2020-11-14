package com.example.demo.repository;

import com.example.demo.model.Message;
import com.example.demo.model.User;
import com.example.demo.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findAllByStudent(User student);

    List<Message> findAllByLesson(Lesson lesson);

}
