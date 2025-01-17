package com.nathanlucas.nscatalog.repositories;

import com.nathanlucas.nscatalog.entities.Product;
import com.nathanlucas.nscatalog.factories.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;
    Long existingId;
    Long nonExistingId;
    Long countTotalProducts;

    @BeforeEach
    public void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {


        repository.deleteById(existingId);

        Optional<Product> result = repository.findById(existingId);

        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
        Product product = Factory.createProduct();
        product.setId(null);

        product = repository.save(product);

        assertNotNull(product.getId());
        assertEquals(countTotalProducts+1, product.getId());
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() {
        Optional<Product> productExpected = repository.findById(existingId);

        assertThat(productExpected.isPresent()).isTrue();
    }

    @Test
    public void findByIdShouldNotReturnProductWhenIdDontExists() {
        Optional<Product> productExpected = repository.findById(nonExistingId);

        assertThat(productExpected.isEmpty()).isTrue();
    }
}
