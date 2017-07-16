package com.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
class ResourcesController {

	@RequestMapping(value = "/resources/1", method = GET)
	public void resources() {
		System.out.println("-- resources --");
	}

}
