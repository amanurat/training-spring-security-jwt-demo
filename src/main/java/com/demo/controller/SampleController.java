package com.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class SampleController {

    @RequestMapping(value = "/sample", method = GET)
    public void sample() {
        System.out.println("sample");
    }


}
