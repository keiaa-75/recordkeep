package com.shing.gatekeep.controller;

import com.shing.gatekeep.repository.AttendanceRepository; 
import com.shing.gatekeep.repository.StudentRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Controller
@RequestMapping("/")
public class DashboardController {

    @Autowired
    private StudentRepository studentRepository; // Student Info Repo

    @Autowired
    private AttendanceRepository attendanceRepository; // Attendance Log Repo

    @GetMapping
    public String viewDashboard(Model model) {
        
        long totalStudents = studentRepository.count();
        model.addAttribute("totalStudents", totalStudents);

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
        long todayPresent = attendanceRepository.findByAttendanceTimeBetween(
            startOfDay, endOfDay
        ).size();
        model.addAttribute("totalPresent", todayPresent);

        return "dashboard"; 
    }
}