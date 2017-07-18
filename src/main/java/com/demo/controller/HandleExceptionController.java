package com.demo.controller;

import com.demo.exception.JwtAuthenticationException;
import com.demo.exception.LoginFailException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.security.SignatureException;

@ControllerAdvice
public class HandleExceptionController {

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> globalException(Exception e) {
        System.out.println("-- this is global exception --");
        return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {LoginFailException.class, BadCredentialsException.class})
    public ResponseEntity<Object> loginFailException(LoginFailException e) {
        System.out.println("-- this is login fail exception --");
        return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(SignatureException.class)
    public void failedToVerify() {
        System.out.println("");
    }

    @ExceptionHandler(value = {JwtAuthenticationException.class})
    public ResponseEntity<Object> jwtAuthenticationException(JwtAuthenticationException e) {
        System.out.println("-- verify token invalid exception --");
        return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest request) {
        return new ResponseEntity<>("Access denied ", new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

}
