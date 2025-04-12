package com.won.myongjiCamp.controller.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminApiController {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/dashBoard")
    public String adminDashboard() {
        return "admin dashboard";
    }
}
