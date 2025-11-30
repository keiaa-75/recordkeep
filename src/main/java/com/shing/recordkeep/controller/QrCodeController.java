package com.shing.recordkeep.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.shing.recordkeep.service.QrCodeService;

@Controller
public class QrCodeController {

    @Autowired
    private QrCodeService qrCodeService;

    @GetMapping("/qr-code")
    public String showQrGenerator(Model model,
                                  @RequestParam(required = false) String searchName,
                                  @RequestParam(required = false) String searchStrand,
                                  @RequestParam(required = false) Integer searchGrade,
                                  @RequestParam(required = false) String searchSection) {
        
        model.addAttribute("students", qrCodeService.getFilteredStudentsForQr(searchName, searchStrand, searchGrade, searchSection));
        model.addAttribute("strandOptions", qrCodeService.getStrandOptions());
        model.addAttribute("gradeOptions", qrCodeService.getGradeOptions());
        model.addAttribute("sectionOptions", qrCodeService.getSectionOptions());
        
        model.addAttribute("searchName", searchName);
        model.addAttribute("searchStrand", searchStrand);
        model.addAttribute("searchGrade", searchGrade);
        model.addAttribute("searchSection", searchSection);

        return "qr-generator";
    }
    
    @GetMapping(value = "/generate-qr-image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQrImage(@RequestParam String lrn) {
        try {
            byte[] qrCodeBytes = qrCodeService.generateQrCodeImage(lrn, 200, 200);
            return ResponseEntity.ok().body(qrCodeBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new byte[0]);
        }
    }

    @PostMapping("/download-qrs")
    public ResponseEntity<byte[]> downloadQrCodes(@RequestBody List<String> lrns) {
        try {
            byte[] zipBytes = qrCodeService.generateQrCodesAsZip(lrns);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=qr-codes.zip");

            return new ResponseEntity<>(zipBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
