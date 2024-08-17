package com.won.myongjiCamp.controller.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootApiController {

    @GetMapping("/health")
    public String healthCheck() {
        return "success";
    }
}
