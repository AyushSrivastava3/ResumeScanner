package com.example.job_desc_backend.controller;

import com.example.job_desc_backend.dto.MailBody;
import com.example.job_desc_backend.model.ForgetPassword;
import com.example.job_desc_backend.model.User;
import com.example.job_desc_backend.repository.ForgetPasswordRepository;
import com.example.job_desc_backend.repository.UserRepository;
import com.example.job_desc_backend.service.EmailService;
import com.example.job_desc_backend.utility.ChangePassword;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/forgotPassword")
public class ForgetPasswordController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    EmailService emailService;
    @Autowired
    ForgetPasswordRepository forgetPasswordRepository;

    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email){
       User user= userRepository.findByEmail(email);
       if(user==null){
           throw new RuntimeException("User not found exception");
       }
       int otp=otpGenerator();
        MailBody mailBody=MailBody.builder()
                .to(email)
                .text("This is otp for your forgot password request :"+otp)
                .subject("Otp for forgot password request")
                .build();
        ForgetPassword fp=ForgetPassword.builder()
                .otp(otp)
                .expirationDate(new Date(System.currentTimeMillis()+70*1000))
                .userId(user.getId())
                .build();
        emailService.sendSimpleMessage(mailBody);
        forgetPasswordRepository.save(fp);
        return ResponseEntity.ok("Email is send for verification");
    }
    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email){
        User user= userRepository.findByEmail(email);
        if(user==null){
            throw new RuntimeException("User not found exception");
        }
        ForgetPassword forgetPassword=forgetPasswordRepository.findByOtpAndUserId(otp, user.getId());
        if (forgetPassword==null){
            throw new RuntimeException("Invalid otp for the email :"+email);
        }
        if (forgetPassword.getExpirationDate().before(Date.from(Instant.now()))){
            forgetPasswordRepository.deleteById(forgetPassword.getId());
            return new ResponseEntity<>("Otp has expired", HttpStatus.EXPECTATION_FAILED);
        }
        return ResponseEntity.ok("Otp is verified");
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(@RequestBody ChangePassword changePassword,
                                                        @PathVariable String email){
        if (!Objects.equals(changePassword.password(),changePassword.repeatPassword())){
            return new ResponseEntity<>("Again enter the password",HttpStatus.EXPECTATION_FAILED);

        }
        User user= userRepository.findByEmail(email);
        user.setPassword(changePassword.password());
        userRepository.save(user);
        return new ResponseEntity<>("Password has been changed",HttpStatus.ACCEPTED);
    }

    private Integer otpGenerator(){
        Random random=new Random();
        return random.nextInt(100_000,999_999);
    }
}
