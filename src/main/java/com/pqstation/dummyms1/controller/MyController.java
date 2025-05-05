package com.pqstation.dummyms1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/ms1")
public class MyController {

    @GetMapping("/hello")
    public String sayHello() {
        System.out.println("Hello from MS1");
        return "Hello from MS1";
    }
}
