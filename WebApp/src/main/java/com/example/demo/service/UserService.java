package com.example.demo.service;

import com.example.demo.model.Lesson;
import com.example.demo.model.Request;
import com.example.demo.model.User;
import com.example.demo.repository.LessonRepository;
import com.example.demo.repository.RequestRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.UserType;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.util.DataUtils.checkPassword;

@Service
public class UserService {

    private UserRepository userRepository;
    private LessonRepository lessonRepository;
    private RequestRepository requestRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       LessonRepository lessonRepository,
                       RequestRepository requestRepository) {
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
        this.requestRepository = requestRepository;
    }

    public List<User> findAllByUserType(UserType userType) {
        return userRepository.findAll()
                .stream()
                .filter(u -> u.getUserType().equals(userType))
                .collect(Collectors.toList());
    }

    public User addUser(User user) {
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String login = user.getLogin();
        String password = user.getPassword();
        UserType userType = user.getUserType();
        User newUser = null;

        if (firstName.length() > 2 && lastName.length() > 2 && login.length() > 2
                && checkPassword(password) && !userRepository.findAllLogin().contains(login)) {
            newUser = new User();
            newUser.setLogin(login);
            newUser.setPassword(Hashing.sha512().hashString(password, StandardCharsets.UTF_8).toString());
            newUser.setUserType(userType);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser = userRepository.save(newUser);
        }
        return newUser;
    }

    public User updateUser(User user) {
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String login = user.getLogin();
        User updateUser = userRepository.findByLogin(login).orElse(null);

        if (updateUser != null && firstName.length() > 2 && lastName.length() > 2) {
            updateUser.setFirstName(firstName);
            updateUser.setLastName(lastName);
            updateUser = userRepository.save(updateUser);
        }
        return updateUser;
    }

    public boolean deleteUser(String login) {
        boolean success = false;
        User user = userRepository.findByLogin(login).orElse(null);

        if (user != null && user.getUserType().equals(UserType.TRAINER)) {
            List<Lesson> lessons = lessonRepository.findAllByTrainer(user);
            lessons.forEach(z -> z.setTrainer(null));
            lessonRepository.saveAll(lessons);
            userRepository.delete(user);
            success = true;

        } else if (user != null && user.getUserType().equals(UserType.STUDENT)) {
            List<Request> requests = requestRepository.findAllByStudent(user);
            requests.forEach(z -> z.setStudent(null));
            requestRepository.saveAll(requests);
            userRepository.delete(user);
            success = true;
        }
        return success;
    }

}
