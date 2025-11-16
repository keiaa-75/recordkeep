package com.shing.gatekeep.controller;

import com.shing.gatekeep.model.AttendanceRecord;
import com.shing.gatekeep.model.Student;
import com.shing.gatekeep.repository.AttendanceRepository;
import com.shing.gatekeep.repository.StudentRepository;
import com.shing.gatekeep.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AttendanceController {

    @Autowired
    private AttendanceRepository attendanceRepository; // Attendance Log Repo

    @Autowired
    private StudentRepository studentRepository; // Student Info Repo
    
    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/scan")
    public String viewScanPage() {
        return "scan"; // Maps to scan.html
    }

    @PostMapping("/scan") 
    public String recordAttendance(@RequestParam String lrn, RedirectAttributes redirectAttributes) {
        String result = attendanceService.recordAttendance(lrn);
        
        if (result.startsWith("Error") || result.startsWith("Warning")) {
            redirectAttributes.addFlashAttribute("errorMessage", result);
        } else {
            redirectAttributes.addFlashAttribute("successMessage", result);
        }
        return "redirect:/scan";
    }

    @GetMapping("/report") // This is the "Attendance Today" page
    public String viewAttendanceReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate filterDate,
            @RequestParam(required = false) String searchStrand,
            @RequestParam(required = false) Integer searchGrade,
            @RequestParam(required = false) String searchSection,
            Model model) {
        
        LocalDate date = (filterDate == null) ? LocalDate.now() : filterDate;
        
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        
        List<AttendanceRecord> records = attendanceRepository.findByAttendanceTimeBetween(startOfDay, endOfDay);
        
        List<Student> allStudents = studentRepository.findAll();
        Map<String, Student> studentMap = allStudents.stream()
                .collect(Collectors.toMap(Student::getLrn, student -> student));

        List<AttendanceRecord> filteredRecords = records.stream()
            .map(record -> {
                Student student = studentMap.get(record.getLrn());
                if (student != null) {
                    record.setSurname(student.getSurname());
                    record.setFirstName(student.getFirstName());
                    record.setStrand(student.getStrand());
                    record.setGradeLevel(student.getGradeLevel());
                    record.setSection(student.getSection());
                }
                return record;
            })
            // Apply Search Filters
            .filter(record -> searchStrand == null || searchStrand.isEmpty() || (record.getStrand() != null && record.getStrand().equalsIgnoreCase(searchStrand)))
            .filter(record -> searchGrade == null || (record.getGradeLevel() != null && record.getGradeLevel().equals(searchGrade)))
            .filter(record -> searchSection == null || searchSection.isEmpty() || (record.getSection() != null && record.getSection().equalsIgnoreCase(searchSection)))
            .collect(Collectors.toList());

        model.addAttribute("strandOptions", List.of("STEM", "TVL", "HUMSS", "ABM", "GAS"));
        model.addAttribute("gradeOptions", List.of(11, 12));
        model.addAttribute("sectionOptions", allStudents.stream().map(Student::getSection).distinct().sorted().collect(Collectors.toList()));

        model.addAttribute("attendanceRecords", filteredRecords);
        model.addAttribute("selectedDate", date);
        model.addAttribute("searchStrand", searchStrand);
        model.addAttribute("searchGrade", searchGrade);
        model.addAttribute("searchSection", searchSection);

        return "attendance"; // Maps to attendance.html
    }
}