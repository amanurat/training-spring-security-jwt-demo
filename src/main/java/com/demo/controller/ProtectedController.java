package com.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class ProtectedController {

    @RequestMapping(value = "/protected/1", method = GET)
    public void protectedPath() {
        System.out.println("-- protected --");
    }
}
