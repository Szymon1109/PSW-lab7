package com.example.demo.service;

import com.example.demo.model.Course;
import com.example.demo.model.Request;
import com.example.demo.model.User;
import com.example.demo.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class RequestService {

    private RequestRepository requestRepository;

    @Autowired
    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public Request addRequest(User user, Course course) {
        Request request = null;
        if (user != null && course != null) {
            request = new Request();
            request.setDate(LocalDate.now());
            request.setAccepted("");
            request.setStudent(user);
            request.setCourse(course);
            request = requestRepository.save(request);
        }
        return request;
    }

    public Request answerRequest(Long id, boolean accepted) {
        Request request = requestRepository.findById(id).orElse(null);
        if (request != null) {
            if (accepted) {
                request.setAccepted("tak");
            } else {
                request.setAccepted("nie");
            }
            request = requestRepository.save(request);
        }
        return request;
    }

    public boolean deleteRequest(Long id) {
        boolean success = false;
        Request request = requestRepository.findById(id).orElse(null);
        if (request != null) {
            requestRepository.delete(request);
            success = true;
        }
        return success;
    }

}
