package com.shing.recordkeep.service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shing.recordkeep.model.Student;

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
                .filter(s -> s.getSection().getStrand().equalsIgnoreCase(searchStrand))
                .collect(Collectors.toList());
        }

        if (searchGrade != null) {
            students = students.stream()
                .filter(s -> s.getSection().getGradeLevel().equals(searchGrade))
                .collect(Collectors.toList());
        }

        if (searchSection != null && !searchSection.isEmpty()) {
            students = students.stream()
                .filter(s -> s.getSection().getSectionName().equalsIgnoreCase(searchSection))
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

    public byte[] generateQrCodesAsZip(List<String> lrns) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (String lrn : lrns) {
                Student student = studentService.findById(lrn)
                    .orElseThrow(() -> new Exception("Student not found with LRN: " + lrn));
                
                byte[] qrCodeBytes = generateQrCodeImage(lrn, 200, 200);

                String fileName = String.format("QR_%s_%s_%s.png", 
                    student.getFirstName(), student.getSurname(), lrn);

                ZipEntry zipEntry = new ZipEntry(fileName);
                zos.putNextEntry(zipEntry);
                zos.write(qrCodeBytes);
                zos.closeEntry();
            }
            zos.finish();
            return baos.toByteArray();
        }
    }
}
