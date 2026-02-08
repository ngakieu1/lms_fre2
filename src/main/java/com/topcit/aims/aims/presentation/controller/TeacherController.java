package com.topcit.aims.aims.presentation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    @GetMapping("/dashboard")
    public String teacher(){
        return "Teacher Dashboard";
    }
}
