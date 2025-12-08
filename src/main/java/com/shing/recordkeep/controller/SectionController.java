package com.shing.recordkeep.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    @GetMapping("/sections/edit/{id}")
    public String showEditSectionModal(@PathVariable Long id, Model model) {
        studentService.findSectionById(id).ifPresent(section -> {
            model.addAttribute("section", section);
        });
        return "fragments/modals/edit-section-modal :: edit-section-modal";
    }

    @PostMapping("/sections/update")
    public String updateSection(@ModelAttribute Section section, RedirectAttributes redirectAttributes) {
        try {
            studentService.saveSection(section);
            redirectAttributes.addFlashAttribute("successMessage", "Section updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating section: " + e.getMessage());
        }
        return "redirect:/sections";
    }

    @PostMapping("/sections/delete/{id}")
    public String deleteSection(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // Check if the section has students first
        if (!studentService.getStudentsBySection(id).isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete section: It still contains students. Please remove them first.");
            return "redirect:/sections";
        }

        try {
            studentService.deleteSection(id);
            redirectAttributes.addFlashAttribute("successMessage", "Section deleted successfully!");
        } catch (Exception e) {
            // This catch block is now more for unexpected errors, as the main expected error is handled above.
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred while deleting the section.");
        }
        return "redirect:/sections";
    }

    @GetMapping("/sections/{sectionId}/students")
    public String manageStudents(@PathVariable Long sectionId,
                                 @RequestParam(required = false) String date,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            LocalDate selectedDate = (date != null && !date.isEmpty())
                ? LocalDate.parse(date)
                : LocalDate.now();
            
            Section section = studentService.findSectionById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found"));

            model.addAttribute("students", studentService.getStudentsWithAttendanceBySection(sectionId, selectedDate));
            model.addAttribute("sectionId", sectionId);
            model.addAttribute("selectedDate", selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)); // YYYY-MM-DD
            model.addAttribute("newStudent", new Student());
            model.addAttribute("section", section);
            return "students";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Section not found.");
            return "redirect:/sections";
        }
    }

    @PostMapping("/sections/{sectionId}/students")
    public String createStudent(@ModelAttribute("newStudent") Student student,
                               @PathVariable Long sectionId,
                               RedirectAttributes redirectAttributes) {

        // Check for existing LRN
        if (studentService.findById(student.getLrn()).isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating student: A student with LRN '" + student.getLrn() + "' already exists.");
            return "redirect:/sections/" + sectionId + "/students";
        }

        try {
            Section section = studentService.getAllSections().stream()
                .filter(s -> s.getId().equals(sectionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Section not found"));

            student.setSection(section);
            
            studentService.save(student);
            redirectAttributes.addFlashAttribute("successMessage", "Student created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating student: " + e.getMessage());
        }
        
        return "redirect:/sections/" + sectionId + "/students";
    }

    @PostMapping("/sections/{sectionId}/students/import")
    public String importStudents(@RequestParam("file") MultipartFile file,
                                @PathVariable Long sectionId,
                                RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a CSV file to upload.");
            return "redirect:/sections/" + sectionId + "/students";
        }

        try {
            int importedCount = csvService.importStudentsFromCsv(file, sectionId);
            redirectAttributes.addFlashAttribute("successMessage", "Successfully imported " + importedCount + " students.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error importing file: " + e.getMessage());
        }
        
        return "redirect:/sections/" + sectionId + "/students";
    }
}
