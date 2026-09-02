package com.practica.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import com.practica.Services.ProviderService;

@RestController
@Controller(value = "/characters")
public class Characters {

    public Characters(ProviderService providerService) {
    }
    
}
