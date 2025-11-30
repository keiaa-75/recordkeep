package com.shing.recordkeep.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shing.recordkeep.model.Section;
import com.shing.recordkeep.model.Student;
import com.shing.recordkeep.service.CsvService;
import com.shing.recordkeep.service.StudentService;

@Controller
public class SectionController {

    @Autowired
    private StudentService studentService;
    
    @Autowired
    private CsvService csvService;

    @GetMapping("/sections")
    public String listSections(Model model) {
        model.addAttribute("sections", studentService.getAllSections());
        return "sections";
    }

    @PostMapping("/sections")
    public String createSection(@RequestParam String gradeLevel, 
                               @RequestParam String strand, 
                               @RequestParam String sectionName, 
                               RedirectAttributes redirectAttributes) {
        try {
            Section section = new Section();
            section.setGradeLevel(Integer.parseInt(gradeLevel.replace("Grade ", "")));
            section.setStrand(strand);
            section.setSectionName(sectionName);
            
            studentService.saveSection(section);
            redirectAttributes.addFlashAttribute("successMessage", "Section created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating section: " + e.getMessage());
        }
        
        return "redirect:/sections";
    }

    @GetMapping("/students/{sectionId}")
    public String manageStudents(@PathVariable Long sectionId, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("students", studentService.getStudentsBySection(sectionId));
            model.addAttribute("sectionId", sectionId);
            return "students";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Section not found.");
            return "redirect:/sections";
        }
    }

    @PostMapping("/students")
    public String createStudent(@RequestParam String lrn,
                               @RequestParam String firstName,
                               @RequestParam String surname,
                               @RequestParam String sex,
                               @RequestParam Long sectionId,
                               RedirectAttributes redirectAttributes) {
        try {
            Section section = studentService.getAllSections().stream()
                .filter(s -> s.getId().equals(sectionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Section not found"));

            Student student = new Student();
            student.setLrn(lrn);
            student.setFirstName(firstName);
            student.setSurname(surname);
            student.setSex(sex);
            student.setSection(section);
            
            studentService.save(student);
            redirectAttributes.addFlashAttribute("successMessage", "Student created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating student: " + e.getMessage());
        }
        
        return "redirect:/students/" + sectionId;
    }

    @PostMapping("/students/import")
    public String importStudents(@RequestParam("file") MultipartFile file,
                                @RequestParam Long sectionId,
                                RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a CSV file to upload.");
            return "redirect:/students/" + sectionId;
        }

        try {
            int importedCount = csvService.importStudentsFromCsv(file);
            redirectAttributes.addFlashAttribute("successMessage", "Successfully imported " + importedCount + " students.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error importing file: " + e.getMessage());
        }
        
        return "redirect:/students/" + sectionId;
    }
}
