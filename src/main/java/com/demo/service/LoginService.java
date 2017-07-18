package com.demo.service;

import com.demo.bean.LoginRequest;
import com.demo.bean.LoginResponse;
import com.demo.domain.User;
import com.demo.exception.LoginFailException;
import com.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginService {

    @Autowired private JwtService jwtService;
    @Autowired private UserRepository userRepository;

    public LoginResponse login(LoginRequest loginRequest) {
        //mock username = 'admin' and password = 'admin'
//        if (!"admin".equals(loginRequest.getUsername()) || !"admin".equals(loginRequest.getPassword())) {
//            throw new LoginFailException("username or password incorrect!");
//        }

        User user = this.userRepository.findByUsernameAndPassword(loginRequest.getUsername(), loginRequest.getPassword());
        if (user == null) {
            throw new LoginFailException("username or password incorrect ");
        }

        //generate token and return token

//        String tokenResponse = jwtService.tokenFor(loginRequest);
        String tokenResponse = jwtService.tokenFor(user);

        System.out.println(tokenResponse);

        return new LoginResponse(tokenResponse);

    }
}
