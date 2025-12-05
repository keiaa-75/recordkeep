package com.shing.recordkeep.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.shing.recordkeep.service.QrCodeService;

@Controller
public class QrCodeController {

    @Autowired
    private QrCodeService qrCodeService;

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
