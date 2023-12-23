package com.app.todo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return ("<h1>Welcome</h1>");
    }

    @GetMapping("/user")
    public String user(Authentication auth) {
        return ("<h1>Welcome User " + auth.getName() + "</h1>");
    }

    @GetMapping("/admin")
    public String admin(Authentication auth) {
        return ("<h1>Welcome Admin " + auth.getName() + " </h1>");
    }
}
