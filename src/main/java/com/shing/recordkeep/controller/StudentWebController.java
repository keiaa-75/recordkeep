package com.shing.recordkeep.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shing.recordkeep.model.Student;
import com.shing.recordkeep.service.StudentService;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@RequestMapping("/web/students")
public class StudentWebController {

    private final StudentService studentService;

    @GetMapping("/edit/{lrn}")
    public String getEditStudentModal(@PathVariable String lrn, Model model) {
        Student student = studentService.findById(lrn)
            .orElseThrow(() -> new RuntimeException("Student not found with LRN: " + lrn));
        model.addAttribute("student", student);
        return "fragments/edit-student-modal";
    }
}
