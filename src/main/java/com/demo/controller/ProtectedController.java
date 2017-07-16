package com.demo.controller;

import com.demo.bean.UserDetail;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class ProtectedController {

    @RequestMapping(value = "/protected/1", method = GET)
    public void protectedPath(Authentication authentication) {

        final UserDetail principal = (UserDetail) authentication.getPrincipal();
        System.out.println(principal.getUsername());

        System.out.println("-- protected --");
    }

}
