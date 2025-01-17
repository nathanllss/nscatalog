package com.nathanlucas.nscatalog.services;

import com.nathanlucas.nscatalog.entities.Product;
import com.nathanlucas.nscatalog.repositories.ProductRepository;
import com.nathanlucas.nscatalog.services.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ProductServiceIT {

    @Autowired
    private ProductService service;
    @Autowired
    private ProductRepository repository;
    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private Long countTotalProducts;

    @BeforeEach
    public void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 4L;
        countTotalProducts = 25L;
    }

    @Test
    public void deleteShouldDeleteResourceWhenIdExists() {
        service.delete(existingId);

        assertEquals(countTotalProducts - 1, repository.count());
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDontExists() {

        assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));
        assertEquals(countTotalProducts, repository.count());
    }

    @Test
    public void getAllProductsShouldReturnAllProducts() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<Product> result = service.getAllProducts("", pageRequest);

        assertFalse(result.isEmpty());
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());
        assertEquals(countTotalProducts, result.getTotalElements());
    }

    @Test
    public void getAllProductsShouldReturnEmptyPageWhenPageDoesNotExist() {
        PageRequest pageRequest = PageRequest.of(30, 10);

        Page<Product> result = service.getAllProducts("", pageRequest);

        assertTrue(result.isEmpty());
    }

    @Test
    public void getAllProductsShouldReturnOrderedWhenSorted() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));

        Page<Product> result = service.getAllProducts("", pageRequest);

        assertFalse(result.isEmpty());
        assertEquals("Macbook Pro", result.getContent().get(0).getName());
        assertEquals("PC Gamer", result.getContent().get(1).getName());
        assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
        assertEquals(countTotalProducts, result.getTotalElements());
    }
}
