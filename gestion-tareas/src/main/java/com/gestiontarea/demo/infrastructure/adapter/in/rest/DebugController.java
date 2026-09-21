package com.gestiontarea.demo.infrastructure.adapter.in.rest;

import org.springframework.web.bind.annotation.*;

@RestController
public class DebugController {
     @PostMapping("/debug/raw")
    public String raw(@RequestBody String body) {
        System.out.println("DEBUG RAW >>> [" + body + "]");
        return body;
    }
}
