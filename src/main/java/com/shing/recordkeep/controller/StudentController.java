package com.shing.recordkeep.controller;

import com.shing.recordkeep.model.Student;
import com.shing.recordkeep.service.CsvService;
import com.shing.recordkeep.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/list")
public class StudentController {

    @Autowired
    private StudentService studentService;
    
    @Autowired
    private CsvService csvService; 

    @GetMapping
    public String listStudents(
            @RequestParam(required = false) String globalFilter,
            @RequestParam(required = false) Integer gradeLevel, 
            @RequestParam(required = false) String section,
            Model model) {
        
        model.addAttribute("students", studentService.getFilteredStudents(globalFilter, gradeLevel, section));
        model.addAttribute("currentGlobalFilter", globalFilter);
        model.addAttribute("currentGradeLevel", gradeLevel);
        model.addAttribute("currentSection", section);
        model.addAttribute("gradeLevels", studentService.getUniqueGradeLevels());
        model.addAttribute("sections", studentService.getUniqueSections());

        return "list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("pageTitle", "Add New Student");
        return "add-edit-record";
    }
    
    @GetMapping("/edit/{lrn}")
    public String showEditForm(@PathVariable("lrn") String lrn, Model model, RedirectAttributes redirectAttributes) {
        Optional<Student> studentOpt = studentService.findById(lrn);
        if (studentOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Student not found.");
            return "redirect:/list";
        }
        model.addAttribute("student", studentOpt.get());
        model.addAttribute("pageTitle", "Edit Student");
        return "add-edit-record";
    }

    @PostMapping("/save")
    public String saveStudent(@Valid Student student, BindingResult bindingResult, 
                              RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", (student.getLrn() == null || student.getLrn().isEmpty()) ? "Add New Student" : "Edit Student");
            return "add-edit-record";
        }
        studentService.save(student);
        redirectAttributes.addFlashAttribute("successMessage", "Student saved successfully!");
        return "redirect:/list";
    }

    @GetMapping("/delete/{lrn}")
    public String deleteStudent(@PathVariable("lrn") String lrn, RedirectAttributes redirectAttributes) {
        studentService.deleteById(lrn);
        redirectAttributes.addFlashAttribute("successMessage", "Student deleted successfully.");
        return "redirect:/list";
    }
    
    @PostMapping("/import")
    public String importCsv(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a CSV file to upload.");
            return "redirect:/list";
        }

        try {
            int importedCount = csvService.importStudentsFromCsv(file);
            redirectAttributes.addFlashAttribute("successMessage", "Successfully imported " + importedCount + " students.");
        
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error importing file: " + e.getMessage());
        }
        
        return "redirect:/list";
    }
}
