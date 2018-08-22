package com.rabbit.samples.bootbackoff.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController("helloRestController")
public class HelloRestController {

	@GetMapping("/")
	public String home() {

		log.debug("Backoff - Hello world...");

		return "Hello World";
	}

}
