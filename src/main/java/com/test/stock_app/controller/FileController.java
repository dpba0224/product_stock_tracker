package com.test.stock_app.controller;

import com.opencsv.exceptions.CsvException;
import com.test.stock_app.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/products")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/import")
    public ResponseEntity<String> uploadFileToDb(@RequestParam MultipartFile file) throws IOException, CsvException {
        fileService.saveFile(file);

        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }
}
