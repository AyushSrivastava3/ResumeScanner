package com.example.job_desc_backend.controller;
import com.example.job_desc_backend.model.User;
import com.example.job_desc_backend.repository.UserRepository;
import com.example.job_desc_backend.service.UserDetailsServiceImpl;
import com.example.job_desc_backend.service.UserService;
import com.example.job_desc_backend.utilis.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtUtil jwtUtil;

    private static final PasswordEncoder passwordencoder=new BCryptPasswordEncoder();
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
////        boolean authenticated = userService.authenticateUser(user.getUsername(), user.getPassword());
////        if (authenticated) {
////            return new ResponseEntity<>("Login successful", HttpStatus.OK);
////        } else {
////            throw new RuntimeException("Either username or password wrong"+HttpStatus.BAD_REQUEST);
////            //return new ResponseEntity<>("Login failed", HttpStatus.UNAUTHORIZED);
////        }
//        String name= authentication.getName();
//        String password= (String) authentication.getCredentials();
//        if (authentication.isAuthenticated()){
//           return new ResponseEntity<>("Login successful", HttpStatus.OK);
//        }
//        else {
//            throw new RuntimeException("Either username or password wrong"+HttpStatus.BAD_REQUEST);
//        }



        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        }catch (Exception e){
            log.error("Exception occurred while createAuthenticationToken ", e);
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return new ResponseEntity<>("Username already exists", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
        }
        User newUser=new User();
        newUser.setEmail(user.getEmail());
        newUser.setUsername(user.getUsername());
        //user.setPassword(user.getPassword());
        newUser.setPassword(passwordencoder.encode(user.getPassword()));
        // Save the user to the database
        userRepository.save(newUser);

        // Redirect to index.html (or any login page) upon successful registration
        return new ResponseEntity<>("Registration successful. Please proceed to login.", HttpStatus.OK);
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<String> deleteUser(@RequestParam User user){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        userRepository.deleteByUsername(authentication.getName());
        return new ResponseEntity<>("User deleted Successfully",HttpStatus.ACCEPTED);
    }
}


