package com.mourathi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public Map<String, Object> root() {
        return Map.of(
                "service", "changeshield-api",
                "status", "running",
                "message", "This is an API-only service. There is no web interface here.",
                "docs", "/swagger-ui.html",
                "health", "/actuator/health"
        );
    }
}