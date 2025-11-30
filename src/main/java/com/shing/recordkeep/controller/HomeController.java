package com.shing.recordkeep.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shing.recordkeep.model.AttendanceRecord;
import com.shing.recordkeep.repository.AttendanceRepository;
import com.shing.recordkeep.repository.StudentRepository;

@Controller
public class HomeController implements ErrorController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @GetMapping("/")
    public String viewHome(Model model) {
        model.addAttribute("totalStudents", studentRepository.count());
        
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(23, 59, 59);
        
        List<AttendanceRecord> attendanceRecords = attendanceRepository.findByAttendanceTimeBetween(todayStart, todayEnd);
        
        model.addAttribute("studentsPresentToday", attendanceRecords.size());
        
        return "home";
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            
            HttpStatus httpStatus = HttpStatus.resolve(statusCode);
            if (httpStatus != null) {
                model.addAttribute("status", statusCode);
                model.addAttribute("error", httpStatus.getReasonPhrase());
            } else {
                model.addAttribute("status", statusCode);
                model.addAttribute("error", "Unknown Error");
            }
        } else {
            model.addAttribute("status", "");
            model.addAttribute("error", "An unexpected error occurred");
        }
        
        return "error";
    }
}
