package com.shing.recordkeep.service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shing.recordkeep.model.Section;
import com.shing.recordkeep.model.Student;
import com.shing.recordkeep.repository.SectionRepository;
import com.shing.recordkeep.repository.StudentRepository;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private SectionRepository sectionRepository;

    public List<Student> getAllStudentsSorted() {
        return studentRepository.findAll(
            Sort.by(Sort.Order.asc("surname"), Sort.Order.asc("firstName"))
        );
    }

    public List<Student> getFilteredStudents(String globalFilter, Integer gradeLevel, String section) {
        List<Student> students = getAllStudentsSorted();

        if (gradeLevel != null) {
            students = students.stream()
                .filter(student -> student.getSection().getGradeLevel().equals(gradeLevel))
                .collect(Collectors.toList());
        }

        if (section != null && !section.isEmpty()) {
            students = students.stream()
                .filter(student -> student.getSection().getSectionName().equalsIgnoreCase(section))
                .collect(Collectors.toList());
        }

        String lowerCaseGlobalFilter = (globalFilter != null && !globalFilter.trim().isEmpty()) 
            ? globalFilter.trim().toLowerCase(Locale.ROOT) : null;
        
        if (lowerCaseGlobalFilter != null) {
            students = students.stream()
                .filter(student -> 
                    student.getSurname().toLowerCase(Locale.ROOT).contains(lowerCaseGlobalFilter) ||
                    student.getFirstName().toLowerCase(Locale.ROOT).contains(lowerCaseGlobalFilter) ||
                    student.getLrn().toLowerCase(Locale.ROOT).contains(lowerCaseGlobalFilter)
                )
                .collect(Collectors.toList());
        }

        return students;
    }

    public List<Integer> getUniqueGradeLevels() {
        return studentRepository.findAll().stream()
            .map(student -> student.getSection().getGradeLevel())
            .distinct()
            .sorted(Comparator.naturalOrder())
            .collect(Collectors.toList());
    }

    public List<String> getUniqueSections() {
        return studentRepository.findAll().stream()
            .map(student -> student.getSection().getSectionName())
            .distinct()
            .sorted(Comparator.naturalOrder())
            .collect(Collectors.toList());
    }

    public Optional<Student> findById(String lrn) {
        return studentRepository.findById(lrn);
    }

    public Student save(Student student) {
        return studentRepository.save(student);
    }

    public void deleteById(String lrn) {
        studentRepository.deleteById(lrn);
    }

    // Section management methods
    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }

    public Section saveSection(Section section) {
        return sectionRepository.save(section);
    }

    public List<Student> getStudentsBySection(Long sectionId) {
        return studentRepository.findBySectionId(sectionId);
    }
}
