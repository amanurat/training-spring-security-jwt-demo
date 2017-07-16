package com.demo.controller;

import com.demo.bean.LoginRequest;
import com.demo.bean.LoginResponse;
import com.demo.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired private LoginService loginService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {

        LoginResponse loginResponse = loginService.login(loginRequest);

        return loginResponse;

    }

}
