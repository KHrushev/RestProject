package com.restproject.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Value("${api.key.darksky}")
    String key;

    @RequestMapping("/test")
    public String test() {
        return key;
    }
}
