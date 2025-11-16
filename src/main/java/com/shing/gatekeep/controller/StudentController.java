package com.shing.gatekeep.controller;

import com.shing.gatekeep.model.Student;
import com.shing.gatekeep.repository.StudentRepository; // Student Info Repo
import com.shing.gatekeep.service.CsvService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/list") // URL for Student List
public class StudentController {

    @Autowired
    private StudentRepository studentRepository; // Student Info Repo
    
    @Autowired
    private CsvService csvService; 

    @GetMapping
    public String listStudents(
            @RequestParam(required = false) String globalFilter,
            @RequestParam(required = false) Integer gradeLevel, 
            @RequestParam(required = false) String section,
            Model model) {
        
        List<Student> students = studentRepository.findAll(
            Sort.by(Sort.Order.asc("surname"), Sort.Order.asc("firstName"))
        );

        // --- Filter Logic ---
        if (gradeLevel != null) {
            students = students.stream()
                .filter(student -> student.getGradeLevel().equals(gradeLevel))
                .collect(Collectors.toList());
        }
        if (section != null && !section.isEmpty()) {
            students = students.stream()
                .filter(student -> student.getSection().equalsIgnoreCase(section))
                .collect(Collectors.toList());
        }
        String lowerCaseGlobalFilter = (globalFilter != null && !globalFilter.trim().isEmpty()) ? globalFilter.trim().toLowerCase(Locale.ROOT) : null;
        if (lowerCaseGlobalFilter != null) {
            students = students.stream()
                .filter(student -> 
                    student.getSurname().toLowerCase(Locale.ROOT).contains(lowerCaseGlobalFilter) ||
                    student.getFirstName().toLowerCase(Locale.ROOT).contains(lowerCaseGlobalFilter) ||
                    student.getLrn().toLowerCase(Locale.ROOT).contains(lowerCaseGlobalFilter)
                )
                .collect(Collectors.toList());
        }
        
        List<Integer> uniqueGradeLevels = studentRepository.findAll().stream()
                .map(Student::getGradeLevel).distinct().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        List<String> uniqueSections = studentRepository.findAll().stream()
                .map(Student::getSection).distinct().sorted(Comparator.naturalOrder()).collect(Collectors.toList());

        model.addAttribute("students", students);
        model.addAttribute("currentGlobalFilter", globalFilter);
        model.addAttribute("currentGradeLevel", gradeLevel);
        model.addAttribute("currentSection", section);
        model.addAttribute("gradeLevels", uniqueGradeLevels);
        model.addAttribute("sections", uniqueSections);

        return "list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("pageTitle", "Add New Student");
        return "add-edit-record"; // <-- FIX: Changed "student-form" to "add-edit-record"
    }
    
    @GetMapping("/edit/{lrn}")
    public String showEditForm(@PathVariable("lrn") String lrn, Model model, RedirectAttributes redirectAttributes) {
        Optional<Student> studentOpt = studentRepository.findById(lrn);
        if (studentOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Student not found.");
            return "redirect:/list";
        }
        model.addAttribute("student", studentOpt.get());
        model.addAttribute("pageTitle", "Edit Student");
        return "add-edit-record"; // <-- FIX: Changed "student-form" to "add-edit-record"
    }

    @PostMapping("/save")
    public String saveStudent(@Valid Student student, BindingResult bindingResult, 
                              RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", (student.getLrn() == null || student.getLrn().isEmpty()) ? "Add New Student" : "Edit Student");
            return "add-edit-record"; // <-- FIX: Changed "student-form" to "add-edit-record"
        }
        studentRepository.save(student);
        redirectAttributes.addFlashAttribute("successMessage", "Student saved successfully!");
        return "redirect:/list";
    }

    @GetMapping("/delete/{lrn}")
    public String deleteStudent(@PathVariable("lrn") String lrn, RedirectAttributes redirectAttributes) {
        studentRepository.deleteById(lrn);
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