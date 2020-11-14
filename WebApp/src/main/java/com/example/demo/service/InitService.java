package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.util.UserType;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;

@Service
public class InitService {

    private LessonSeriesRepository lessonSeriesRepository;
    private CourseRepository courseRepository;
    private MessageRepository messageRepository;
    private UserRepository userRepository;
    private LessonRepository lessonRepository;
    private RequestRepository requestRepository;

    @Autowired
    public InitService(LessonSeriesRepository lessonSeriesRepository,
                       CourseRepository courseRepository,
                       MessageRepository messageRepository,
                       UserRepository userRepository,
                       LessonRepository lessonRepository,
                       RequestRepository requestRepository) {
        this.lessonSeriesRepository = lessonSeriesRepository;
        this.courseRepository = courseRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
        this.requestRepository = requestRepository;
    }

    @PostConstruct
    public void init() {
        User u0 = new User(0L, "Szymon1104", Hashing.sha512().hashString("Pa$$word1", StandardCharsets.UTF_8).toString(), UserType.ADMIN, "Szymon", "Betlewski");
        User u1 = new User(0L, "Ala123", Hashing.sha512().hashString("Ala123", StandardCharsets.UTF_8).toString(), UserType.STUDENT, "Ala", "Kowalska");
        User u2 = new User(0L, "Kasia123", Hashing.sha512().hashString("Kasia123", StandardCharsets.UTF_8).toString(), UserType.STUDENT, "Kasia", "Nowak");
        User u3 = new User(0L, "Piotr123", Hashing.sha512().hashString("Piotr123", StandardCharsets.UTF_8).toString(), UserType.STUDENT, "Piotr", "Zieliński");
        User u4 = new User(0L, "Pawel123", Hashing.sha512().hashString("Pawel123", StandardCharsets.UTF_8).toString(), UserType.STUDENT, "Paweł", "Wiśniewski");
        User u5 = new User(0L, "Ania123", Hashing.sha512().hashString("Ania123", StandardCharsets.UTF_8).toString(), UserType.TRAINER, "Ania", "Kowalczyk");
        User u6 = new User(0L, "Michal123", Hashing.sha512().hashString("Michal123", StandardCharsets.UTF_8).toString(), UserType.TRAINER, "Michał", "Duda");
        u0 = userRepository.save(u0);
        u1 = userRepository.save(u1);
        u2 = userRepository.save(u2);
        u3 = userRepository.save(u3);
        u4 = userRepository.save(u4);
        u5 = userRepository.save(u5);
        u6 = userRepository.save(u6);

        Lesson z1 = new Lesson(0L, "Speaking and listening in English", LocalDate.of(2019, 7, 1), u5);
        Lesson z2 = new Lesson(0L, "Reading and writing in English", LocalDate.of(2019, 7, 2), u5);
        Lesson z3 = new Lesson(0L, "Deutsch sprechen und zuhoren", LocalDate.of(2019, 7, 4), u5);
        Lesson z4 = new Lesson(0L, "Deutsch lesen und schreiben", LocalDate.of(2019, 7, 5), u5);
        Lesson z5 = new Lesson(0L, "Inżynieria oprogramowania", LocalDate.of(2019, 7, 8), u6);
        Lesson z6 = new Lesson(0L, "Podstawy programowania obiektowego", LocalDate.of(2019, 7, 8), u6);
        z1 = lessonRepository.save(z1);
        z2 = lessonRepository.save(z2);
        z3 = lessonRepository.save(z3);
        z4 = lessonRepository.save(z4);
        z5 = lessonRepository.save(z5);
        z6 = lessonRepository.save(z6);

        LessonSeries b1 = new LessonSeries(0L, "English classes", Arrays.asList(z1, z2));
        LessonSeries b2 = new LessonSeries(0L, "Deutsch Stunden", Arrays.asList(z3, z4));
        LessonSeries b3 = new LessonSeries(0L, "Zajęcia programistyczne", Arrays.asList(z5, z6));
        b1 = lessonSeriesRepository.save(b1);
        b2 = lessonSeriesRepository.save(b2);
        b3 = lessonSeriesRepository.save(b3);

        Course k1 = new Course(0L, "Kurs języków obcych", Arrays.asList(b1, b2));
        Course k2 = new Course(0L, "Kurs informatyczny", Arrays.asList(b1, b3));
        k1 = courseRepository.save(k1);
        k2 = courseRepository.save(k2);

        requestRepository.save(new Request(0L, LocalDate.of(2019, 6, 20), "tak", u1, k1));
        requestRepository.save(new Request(0L, LocalDate.of(2019, 6, 22), "", u2, k1));
        requestRepository.save(new Request(0L, LocalDate.of(2019, 6, 23), "", u3, k2));
        requestRepository.save(new Request(0L, LocalDate.of(2019, 6, 24), "", u4, k2));

        messageRepository.save(new Message(0L, "Powiadomienie", "Niedługo pierwsze zajęcia z kursu", u1, z1));
        messageRepository.save(new Message(0L, "Powiadomienie", "Niedługo pierwsze zajęcia z kursu", u1, z5));
        messageRepository.save(new Message(0L, "Powiadomienie", "Niedługo pierwsze zajęcia z kursu", u2, z1));
        messageRepository.save(new Message(0L, "Powiadomienie", "Niedługo pierwsze zajęcia z kursu", u3, z5));
        messageRepository.save(new Message(0L, "Powiadomienie", "Niedługo pierwsze zajęcia z kursu", u4, z5));
    }

}