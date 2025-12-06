package com.shing.recordkeep.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shing.recordkeep.service.StudentService;

@Controller
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/sections/{sectionId}/students/{lrn}/update")
    @ResponseBody
    public ResponseEntity<Void> updateStudent(@PathVariable String lrn,
                                              @PathVariable Long sectionId,
                                              @RequestParam String firstName,
                                              @RequestParam String surname,
                                              @RequestParam String sex) {
        try {
            studentService.updateStudent(lrn, firstName, surname, sex);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/sections/{sectionId}/students/{lrn}/delete")
    @ResponseBody
    public ResponseEntity<Void> deleteStudent(@PathVariable String lrn,
                                              @PathVariable Long sectionId) {
        try {
            studentService.deleteStudent(lrn);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
