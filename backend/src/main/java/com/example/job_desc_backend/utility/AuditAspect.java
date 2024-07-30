package com.example.job_desc_backend.utility;

import com.example.job_desc_backend.model.*;
import com.example.job_desc_backend.utilis.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AuditAspect {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private JwtUtil jwtUtil;


    @Pointcut("execution(* com.example.job_desc_backend.service.*.*(..)) || execution(* com.example.job_desc_backend.controller.*.*(..))")
    public void createPointcut() {
    }


    @Before("createPointcut()")
    public void setCreatedBy(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String token = extractTokenFromRequest();
        if (token != null) {
            String username = jwtUtil.extractUsername(token);
            for (Object arg : args) {
                if (arg instanceof Profile) {
                    ((Profile) arg).setCreatedBy(username);
                } else if (arg instanceof Job_description) {
                    ((Job_description) arg).setCreatedBy(username);
                } else if (arg instanceof Resume) {
                    ((Resume) arg).setCreatedBy(username);
                }else if (arg instanceof Resume) {
                    ((Resume) arg).setCreatedBy(username);
                } else if (arg instanceof Client) {
                    ((Client) arg).setCreatedBy(username);
                } else if (arg instanceof Bill) {
                    ((Bill) arg).setCreatedBy(username);
                } else if (arg instanceof Billpdf) {
                    ((Billpdf) arg).setCreatedBy(username);
                } else if (arg instanceof Invoice) {
                    ((Invoice) arg).setCreatedBy(username);
                } else if (arg instanceof Profile_detail_page) {
                    ((Profile_detail_page) arg).setCreatedBy(username);
                } else if (arg instanceof Skill) {
                    ((Skill) arg).setCreatedBy(username);
                }
                // Add similar checks for other entities
            }
        }
    }

    private String extractTokenFromRequest() {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
