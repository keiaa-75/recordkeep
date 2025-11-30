package com.shing.recordkeep.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shing.recordkeep.model.AttendanceRecord;
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
    public String recordAttendance(@RequestParam String lrn, RedirectAttributes redirectAttributes) {
        Map<String, Object> result = attendanceService.recordAttendance(lrn);
        String message = (String) result.get("message");
        AttendanceRecord record = (AttendanceRecord) result.get("attendanceRecord");

        if (message.startsWith("Error") || message.startsWith("Warning")) {
            redirectAttributes.addFlashAttribute("errorMessage", message);
            return "redirect:/scan";
        } else {
            redirectAttributes.addFlashAttribute("successMessage", message);
            if (record != null && record.getSection() != null) {
                return "redirect:/sections/" + record.getSection().getId() + "/students";
            }
            return "redirect:/scan"; // Fallback
        }
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
