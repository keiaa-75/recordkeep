package com.shing.gatekeep.controller;

import com.shing.gatekeep.model.Student;
import com.shing.gatekeep.repository.StudentRepository; // Student Info Repo
import com.shing.gatekeep.service.QrCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class QrCodeController {

    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private StudentRepository studentRepository; // Student Info Repo

    @GetMapping("/qr-code")
    public String showQrGenerator(Model model,
                                  @RequestParam(required = false) String searchName,
                                  @RequestParam(required = false) String searchStrand,
                                  @RequestParam(required = false) Integer searchGrade,
                                  @RequestParam(required = false) String searchSection) { // Added
        
        List<Student> students = studentRepository.findAll();
        List<Student> allStudents = students; // Keep a copy

        // Apply filters
        if (searchName != null && !searchName.isEmpty()) {
            String lowerName = searchName.toLowerCase();
            students = students.stream()
                .filter(s -> s.getSurname().toLowerCase().contains(lowerName) || s.getFirstName().toLowerCase().contains(lowerName))
                .collect(Collectors.toList());
        }
        if (searchStrand != null && !searchStrand.isEmpty()) {
            students = students.stream()
                .filter(s -> s.getStrand().equalsIgnoreCase(searchStrand))
                .collect(Collectors.toList());
        }
        if (searchGrade != null) {
            students = students.stream()
                .filter(s -> s.getGradeLevel().equals(searchGrade))
                .collect(Collectors.toList());
        }
        if (searchSection != null && !searchSection.isEmpty()) {
            students = students.stream()
                .filter(s -> s.getSection().equalsIgnoreCase(searchSection))
                .collect(Collectors.toList());
        }

        model.addAttribute("students", students);
        model.addAttribute("strandOptions", List.of("STEM", "TVL", "HUMSS", "ABM", "GAS"));
        model.addAttribute("gradeOptions", List.of(11, 12));
        model.addAttribute("sectionOptions", allStudents.stream().map(Student::getSection).distinct().sorted().collect(Collectors.toList()));
        
        model.addAttribute("searchName", searchName);
        model.addAttribute("searchStrand", searchStrand);
        model.addAttribute("searchGrade", searchGrade);
        model.addAttribute("searchSection", searchSection);

        return "qr-generator"; // Maps to qr-generator.html
    }
    
    // Endpoint that generates the raw image data
    @GetMapping(value = "/generate-qr-image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQrImage(@RequestParam String lrn) {
        try {
            byte[] qrCodeBytes = qrCodeService.generateQrCode(lrn, 200, 200); 
            return ResponseEntity.ok().body(qrCodeBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new byte[0]);
        }
    }
}