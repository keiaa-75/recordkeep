package com.shing.gatekeep.service;

import com.shing.gatekeep.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QrCodeService {

    @Autowired
    private StudentService studentService;

    @Autowired
    private QrCodeGenService qrCodeGenService;

    public List<Student> getFilteredStudentsForQr(String searchName, String searchStrand, Integer searchGrade, String searchSection) {
        List<Student> students = studentService.getAllStudentsSorted();

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

        return students;
    }

    public List<String> getStrandOptions() {
        return List.of("STEM", "TVL", "HUMSS", "ABM", "GAS");
    }

    public List<Integer> getGradeOptions() {
        return List.of(11, 12);
    }

    public List<String> getSectionOptions() {
        return studentService.getUniqueSections();
    }

    public byte[] generateQrCodeImage(String lrn, int width, int height) throws Exception {
        return qrCodeGenService.generateQrCode(lrn, width, height);
    }
}
