package com.example.demo.controller;

import com.example.demo.model.Course;
import com.example.demo.model.Request;
import com.example.demo.model.User;
import com.example.demo.repository.RequestRepository;
import com.example.demo.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/rest/request")
public class RequestController {

    private RequestService requestService;
    private RequestRepository requestRepository;

    @Autowired
    public RequestController(RequestService requestService,
                             RequestRepository requestRepository) {
        this.requestService = requestService;
        this.requestRepository = requestRepository;
    }

    @GetMapping("/all/byUser")
    @ResponseBody
    public List<Request> findAllByStudent(@RequestBody User user) {
        return requestRepository.findAllByStudent(user);
    }

    @GetMapping("/all/byCourse")
    @ResponseBody
    public List<Request> findAllByCourse(@RequestBody Course course) {
        return requestRepository.findAllByCourse(course);
    }

    @PostMapping
    public ResponseEntity<Request> addRequest(@RequestBody User user,
                                              @RequestBody Course course) {
        Request newRequest = requestService.addRequest(user, course);
        if (newRequest != null) {
            return new ResponseEntity<>(newRequest, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    public ResponseEntity<Request> answerRequest(@RequestParam Long id,
                                                 @RequestBody boolean accepted) {
        Request updateRequest = requestService.answerRequest(id, accepted);
        if (updateRequest != null) {
            return new ResponseEntity<>(updateRequest, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping
    public ResponseEntity deleteRequest(@RequestParam Long id) {
        boolean success = requestService.deleteRequest(id);
        if (success) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
