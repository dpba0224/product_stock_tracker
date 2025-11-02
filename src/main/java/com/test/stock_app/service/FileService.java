package com.test.stock_app.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.test.stock_app.entity.File;
import com.test.stock_app.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {

    private final String FILE_PATH = "src\\";

    @Autowired
    private FileRepository fileRepository;

    public void saveFile(MultipartFile file) throws IOException, CsvException {
        CSVReader reader = new CSVReader(new FileReader(FILE_PATH + file.getOriginalFilename()));
        List<String[]> rows = reader.readAll();
        List<File> fileList = new ArrayList<>();

        // Skip header and validate
        for(int i = 1; i < rows.size(); i++){
            String[] row = rows.get(i);

            // Validate row has enough columns
            if(row.length < 3) {
                System.out.println("Skipping invalid row: " + i);
                continue;
            }

            try {
                System.out.println(row[0] + "," + row[1] + "," + row[2]);

                File files = new File();
                files.setSku(row[0]);
                files.setName(row[1]);
                files.setStockQuantity(Integer.parseInt(row[2].trim())); // trim() removes spaces

                fileList.add(files);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number at row " + i + ": " + row[2]);
                // You can choose to skip this row or throw the exception
            }
        }

        fileRepository.saveAll(fileList);
    }
}
