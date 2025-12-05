package com.shing.recordkeep.service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shing.recordkeep.model.Student;

@Service
public class QrCodeService {

    @Autowired
    private QrCodeGenService qrCodeGenService;

    @Autowired
    private StudentService studentService;

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
