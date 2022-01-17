package com.davidxie.Online.Facial.Recognition;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String getHello() {
        return "Hello World RESTful with Spring Boot";
    }

    @RequestMapping(value = "/hello", method = RequestMethod.POST)
    public String postHello(@RequestBody String input) {
        return input;
    }
}