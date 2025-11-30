package com.shing.recordkeep.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.shing.recordkeep.service.AttendanceService;

@Controller
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/scan")
    public String viewScanPage() {
        return "scan";
    }

    @PostMapping("/scan")
    public ResponseEntity<Map<String, Object>> recordAttendance(@RequestBody Map<String, String> payload) {
        String lrn = payload.get("lrn");
        Map<String, Object> result = attendanceService.recordAttendance(lrn);
        Map<String, Object> response = new HashMap<>();
        
        String message = (String) result.get("message");
        response.put("message", message);
        
        if (result.get("student") != null) {
            com.shing.recordkeep.model.Student student = (com.shing.recordkeep.model.Student) result.get("student");
            response.put("studentName", student.getSurname() + ", " + student.getFirstName());
        }
        
        boolean success = !message.startsWith("Error") && !message.startsWith("Warning");
        response.put("success", success);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/report")
    public String viewAttendanceReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate filterDate,
            @RequestParam(required = false) String searchStrand,
            @RequestParam(required = false) Integer searchGrade,
            @RequestParam(required = false) String searchSection,
            @RequestParam(required = false) String searchSex,
            Model model) {
        
        LocalDate date = (filterDate == null) ? LocalDate.now() : filterDate;
        
        model.addAttribute("attendanceRecords", attendanceService.getFilteredAttendanceRecords(date, searchStrand, searchGrade, searchSection, searchSex));
        model.addAttribute("strandOptions", attendanceService.getStrandOptions());
        model.addAttribute("gradeOptions", attendanceService.getGradeOptions());
        model.addAttribute("sectionOptions", attendanceService.getSectionOptions());
        model.addAttribute("selectedDate", date);
        model.addAttribute("searchStrand", searchStrand);
        model.addAttribute("searchGrade", searchGrade);
        model.addAttribute("searchSection", searchSection);
        model.addAttribute("searchSex", searchSex);

        return "attendance";
    }
}
