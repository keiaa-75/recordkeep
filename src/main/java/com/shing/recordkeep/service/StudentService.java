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
        return getStudentsWithAttendanceBySection(sectionId, LocalDate.now());
    }

    public List<StudentWithAttendance> getStudentsWithAttendanceBySection(Long sectionId, LocalDate date) {
        List<Student> students = studentRepository.findBySectionId(sectionId);
        List<String> attendedLrns = attendanceRepository.findByAttendanceTimeBetween(
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay()
            ).stream()
            .map(ar -> ar.getLrn())
            .collect(Collectors.toList());

        return students.stream()
            .sorted((s1, s2) -> {
                int surnameCompare = s1.getSurname().compareToIgnoreCase(s2.getSurname());
                return surnameCompare != 0 ? surnameCompare : s1.getFirstName().compareToIgnoreCase(s2.getFirstName());
            })
            .map(student -> new StudentWithAttendance(student, attendedLrns.contains(student.getLrn())))
            .collect(Collectors.toList());
    }

    public Optional<Student> findById(String lrn) {
        return studentRepository.findById(lrn);
    }

    public Student save(Student student) {
        return studentRepository.save(student);
    }

    public Student updateStudent(String lrn, String firstName, String surname, Character middleInitial, String sex) {
        Student student = studentRepository.findById(lrn)
            .orElseThrow(() -> new RuntimeException("Student not found with LRN: " + lrn));
        student.setFirstName(firstName);
        student.setSurname(surname);
        student.setMiddleInitial(middleInitial);
        student.setSex(sex);
        return studentRepository.save(student);
    }

    public void deleteStudent(String lrn) {
        studentRepository.deleteById(lrn);
    }

    public void deleteSection(Long id) {
        sectionRepository.deleteById(id);
    }

    // Section management methods
    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }

    public Optional<Section> findSectionById(Long id) {
        return sectionRepository.findById(id);
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
