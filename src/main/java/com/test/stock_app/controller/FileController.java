package com.test.stock_app.controller;

import com.opencsv.exceptions.CsvException;
import com.test.stock_app.entity.File;
import com.test.stock_app.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/import")
    public ResponseEntity<String> uploadFileToDb(@RequestParam MultipartFile file) throws IOException, CsvException {
        try{
            fileService.saveFile(file);
            return ResponseEntity.status(HttpStatus.OK).body("Success");
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input. Please revise and re-upload the file.");
        }
    }

    @GetMapping("/products")
    public ResponseEntity<List<File>> getAllProducts() {
        List<File> products = fileService.getAllProducts();
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }
}
