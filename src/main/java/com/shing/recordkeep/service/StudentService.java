package com.shing.recordkeep.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shing.recordkeep.model.Section;
import com.shing.recordkeep.model.Student;
import com.shing.recordkeep.model.dto.StudentWithAttendance;
import com.shing.recordkeep.repository.AttendanceRepository;
import com.shing.recordkeep.repository.SectionRepository;
import com.shing.recordkeep.repository.StudentRepository;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    public List<Student> getAllStudentsSorted() {
        return studentRepository.findAll(
            Sort.by(Sort.Order.asc("surname"), Sort.Order.asc("firstName"))
        );
    }

    public List<StudentWithAttendance> getStudentsWithAttendanceBySection(Long sectionId) {
        List<Student> students = studentRepository.findBySectionId(sectionId);
        List<String> attendedLrns = attendanceRepository.findByAttendanceTimeBetween(
                LocalDate.now().atStartOfDay(),
                LocalDate.now().plusDays(1).atStartOfDay()
            ).stream()
            .map(ar -> ar.getLrn())
            .collect(Collectors.toList());

        return students.stream()
            .map(student -> new StudentWithAttendance(student, attendedLrns.contains(student.getLrn())))
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

    public List<String> getUniqueSectionNames() {
        return sectionRepository.findAll().stream()
            .map(Section::getSectionName)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
}
