package com.demo.controller;

import com.demo.bean.UserDetail;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class ProtectedController {

    @RequestMapping(value = "/protected/1", method = GET)
    @Secured("ROLE_USER")
    public void protectedPath(Authentication authentication) {

        final UserDetail principal = (UserDetail) authentication.getPrincipal();
        System.out.println(principal.getUsername());

        System.out.println("-- protected for ROLE_USER --");
    }

    @RequestMapping(value = "/protected/2", method = GET)
    @Secured("ROLE_ADMIN")
    public void protectedPathRoleAdmin(Authentication authentication) {

        final UserDetail principal = (UserDetail) authentication.getPrincipal();
        System.out.println(principal.getUsername());

        System.out.println("-- protected for ROLE_ADMIN --");
    }

}
