package com.example.wmsnew.wms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthPageController {
    @GetMapping("/login")
    public String login() {
        // 直接转发到静态资源 login.html
        return "forward:/login.html";
    }
}

