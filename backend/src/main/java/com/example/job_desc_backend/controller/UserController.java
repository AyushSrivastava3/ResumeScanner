package com.example.job_desc_backend.controller;

import com.example.job_desc_backend.model.User;
import com.example.job_desc_backend.repository.UserRepository;
import com.example.job_desc_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        boolean authenticated = userService.authenticateUser(user.getUsername(), user.getPassword());
        if (authenticated) {
            return new ResponseEntity<>("Login successful", HttpStatus.OK);
        } else {
            throw new RuntimeException("Either username or password wrong"+HttpStatus.BAD_REQUEST);
            //return new ResponseEntity<>("Login failed", HttpStatus.UNAUTHORIZED);
        }
    }


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        // Check if username or email already exists
//        if (userRepository.findByUsername(user.getUsername()) != null) {
//            throw new RuntimeException("Username already exists"+HttpStatus.BAD_REQUEST);
//            //return new ResponseEntity<>("Username already exists", HttpStatus.BAD_REQUEST);
//        }
//        if (userRepository.findByEmail(user.getEmail())!=null) {
//            throw new RuntimeException("Email already exists"+HttpStatus.BAD_REQUEST);
//            //return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
//        }
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return new ResponseEntity<>("Username already exists", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
        }
        User newUser=new User();
        newUser.setEmail(user.getEmail());
        user.setUsername(user.getUsername());
        user.setPassword(user.getPassword());
        // Save the user to the database
        userRepository.save(user);

        // Redirect to index.html (or any login page) upon successful registration
        return new ResponseEntity<>("Registration successful. Please proceed to login.", HttpStatus.OK);
    }
}
