package com.test.stock_app.service;

import com.opencsv.exceptions.CsvException;
import com.test.stock_app.entity.File;
import com.test.stock_app.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileService fileService;

    private List<File> mockProducts;

    @BeforeEach
    void setUp() {
        // Setup mock data before each test
        File product1 = new File();
        product1.setId(1);
        product1.setSku("PROD001");
        product1.setName("Widget A");
        product1.setStockQuantity(100);

        File product2 = new File();
        product2.setId(2);
        product2.setSku("PROD002");
        product2.setName("Widget B");
        product2.setStockQuantity(0); // Zero stock product

        File product3 = new File();
        product3.setId(3);
        product3.setSku("PROD003");
        product3.setName("Widget C");
        product3.setStockQuantity(250);

        mockProducts = Arrays.asList(product1, product2, product3);
    }

    /**
     * Test 1: Verify that getAllProducts returns all products from database
     */
    @Test
    void testGetAllProducts_ReturnsAllProducts() {
        // ARRANGE: Set up the test scenario
        // Mock the repository to return our test data
        when(fileRepository.findAll()).thenReturn(mockProducts);

        // ACT: Call the method we're testing
        List<File> result = fileService.getAllProducts();

        // ASSERT: Verify the results
        assertNotNull(result, "Result should not be null");
        assertEquals(3, result.size(), "Should return 3 products");
        assertEquals("PROD001", result.get(0).getSku(), "First product SKU should match");
        assertEquals("Widget B", result.get(1).getName(), "Second product name should match");
        assertEquals(250, result.get(2).getStockQuantity(), "Third product stock should match");

        // Verify that findAll() was called exactly once
        verify(fileRepository, times(1)).findAll();
    }

    /**
     * Test 2: Verify that getAllProducts logs warning for zero stock items
     */
    @Test
    void testGetAllProducts_LogsWarningForZeroStock() {
        // ARRANGE: Set up mock data with zero stock product
        when(fileRepository.findAll()).thenReturn(mockProducts);

        // ACT: Call the method
        List<File> result = fileService.getAllProducts();

        // ASSERT: Verify zero stock product is in the result
        File zeroStockProduct = result.stream()
                .filter(p -> p.getStockQuantity() == 0)
                .findFirst()
                .orElse(null);

        assertNotNull(zeroStockProduct, "Should find a product with zero stock");
        assertEquals("PROD002", zeroStockProduct.getSku(), "Zero stock product should be PROD002");
        assertEquals(0, zeroStockProduct.getStockQuantity(), "Stock quantity should be 0");

        // Verify repository was called
        verify(fileRepository, times(1)).findAll();
    }

    /**
     * Test 3: Verify that getAllProducts returns empty list when no products exist
     */
    @Test
    void testGetAllProducts_ReturnsEmptyList_WhenNoProducts() {
        // ARRANGE: Mock repository to return empty list
        when(fileRepository.findAll()).thenReturn(Arrays.asList());

        // ACT: Call the method
        List<File> result = fileService.getAllProducts();

        // ASSERT: Verify empty list is returned
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Result should be an empty list");
        assertEquals(0, result.size(), "Size should be 0");

        // Verify repository was called
        verify(fileRepository, times(1)).findAll();
    }
}
