package com.test.stock_app.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.test.stock_app.entity.File;
import com.test.stock_app.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
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
        // Validation 1: Check if file is null or empty
        if (file == null || file.isEmpty()) {
            System.err.println("No file was uploaded or file is empty");
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        // Validation 2: Check if file is CSV
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            System.err.println("Invalid file format. Only CSV files are allowed. Received: " + filename);
            throw new IllegalArgumentException("Only CSV files are allowed");
        }

        String filePath = FILE_PATH + filename;
        CSVReader reader = null;

        try {
            // Validation 3: Check if file exists and can be read
            reader = new CSVReader(new FileReader(filePath));
            System.out.println("✓ File found and opened successfully: " + filename);

        } catch (FileNotFoundException e) {
            // File not found - log error message
            System.err.println("ERROR: File not found at path: " + filePath);
            System.err.println("Please ensure the file is uploaded to the correct directory");
            throw new FileNotFoundException("File not found: " + filename + ". Please upload the file first.");
        }

        List<String[]> rows;
        try {
            rows = reader.readAll();
            System.out.println("✓ CSV file read successfully. Total rows: " + rows.size());
        } catch (CsvException e) {
            // Invalid CSV format - log error
            System.err.println("ERROR: Invalid CSV format in file: " + filename);
            System.err.println("Details: " + e.getMessage());
            throw new CsvException("Invalid CSV format. Please check your file structure.");
        } finally {
            // Always close the reader
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println("Failed to close CSV reader");
                }
            }
        }

        // Validation 4: Check if CSV has data (more than just header)
        if (rows.size() <= 1) {
            System.err.println("CSV file is empty or contains only headers");
            throw new IllegalArgumentException("CSV file must contain at least one data row");
        }

        List<File> fileList = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;

        // Skip header and validate each row
        for(int i = 1; i < rows.size(); i++){
            String[] row = rows.get(i);

            try {
                // Validation 5: Check if row has enough columns
                if(row.length < 3) {
                    System.out.println("WARNING: Skipping row " + i + " - Insufficient columns (expected 3, got " + row.length + ")");
                    errorCount++;
                    continue;
                }

                // Validation 6: Check for empty or null values
                if(row[0] == null || row[0].trim().isEmpty()) {
                    System.out.println("WARNING: Skipping row " + i + " - SKU is empty");
                    errorCount++;
                    continue;
                }

                if(row[1] == null || row[1].trim().isEmpty()) {
                    System.out.println("WARNING: Skipping row " + i + " - Product name is empty");
                    errorCount++;
                    continue;
                }

                if(row[2] == null || row[2].trim().isEmpty()) {
                    System.out.println("WARNING: Skipping row " + i + " - Stock quantity is empty");
                    errorCount++;
                    continue;
                }

                // Validation 7: Check for valid number format
                int stockQuantity;
                try {
                    stockQuantity = Integer.parseInt(row[2].trim());
                } catch (NumberFormatException e) {
                    System.out.println("WARNING: Skipping row " + i + " - Invalid number format: '" + row[2] + "' (Expected a number)");
                    errorCount++;
                    continue;
                }

                // Validation 8: Check for negative stock quantity
                if(stockQuantity < 0) {
                    System.out.println("WARNING: Row " + i + " - Negative stock quantity detected: " + stockQuantity + ". Setting to 0.");
                    stockQuantity = 0;
                }

                // Create and populate File entity
                File files = new File();
                files.setSku(row[0].trim());
                files.setName(row[1].trim());
                files.setStockQuantity(stockQuantity);

                fileList.add(files);
                successCount++;

                // Log zero stock warning
                if(stockQuantity == 0) {
                    System.out.println("WARNING: Product '" + row[1].trim() + "' (SKU: " + row[0].trim() + ") is OUT OF STOCK!");
                }

                System.out.println("✓ Row " + i + " processed: " + row[0] + ", " + row[1] + ", " + row[2]);

            } catch (Exception e) {
                // Catch any unexpected errors for this specific row
                System.err.println("Unexpected error processing row " + i + ": " + e.getMessage());
                errorCount++;
            }
        }

        // Validation 9: Check if any valid rows were processed
        if(fileList.isEmpty()) {
            System.err.println("No valid data rows found in CSV file");
            throw new IllegalArgumentException("No valid data to import. Please check your CSV file format.");
        }

        // Save to database
        try {
            fileRepository.saveAll(fileList);
            System.out.println("Import completed!");
            System.out.println("Total rows processed: " + (rows.size() - 1));
            System.out.println("Successfully imported: " + successCount);
            System.out.println("Skipped/Errors: " + errorCount);
        } catch (Exception e) {
            System.err.println("Failed to save data to database: " + e.getMessage());
            throw new RuntimeException("Database error: Unable to save products");
        }
    }

    public List<File> getAllProducts() {
        try {
            List<File> products = fileRepository.findAll();
            System.out.println("✓ Retrieved " + products.size() + " products from database");

            // Log products with zero stock when retrieving all products
            int zeroStockCount = 0;
            for(File product : products) {
                if(product.getStockQuantity() == 0) {
                    System.out.println("Product '" + product.getName() +
                            "' (SKU: " + product.getSku() + ") is out of stock.");
                    zeroStockCount++;
                }
            }

            if(zeroStockCount > 0) {
                System.out.println("Total products out of stock: " + zeroStockCount);
            }

            return products;
        } catch (Exception e) {
            System.err.println("Failed to retrieve products from database: " + e.getMessage());
            throw new RuntimeException("Database error: Unable to retrieve products");
        }
    }
}
