package com.nathanlucas.nscatalog.services;

import com.nathanlucas.nscatalog.entities.Product;
import com.nathanlucas.nscatalog.factories.Factory;
import com.nathanlucas.nscatalog.repositories.ProductRepository;
import com.nathanlucas.nscatalog.services.exception.DatabaseException;
import com.nathanlucas.nscatalog.services.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductService service;
    @Mock
    private ProductRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private PageImpl<Product> page;
    private Product product;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 4L;
        product = Factory.createProduct();
        page = new PageImpl<>(List.of(product));

    }

    @Test
    public void findAllShouldReturnPage() {
        when(repository.searchAll(anyString(), any(Pageable.class))).thenReturn(page);

        Pageable pageable = PageRequest.of(0, 20);

        Page<Product> result = service.getAllProducts("", pageable);

        assertNotNull(result);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getContent().size()).isEqualTo(1);
        verify(repository, times(1)).searchAll(anyString(), any(Pageable.class));

    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() {
        when(repository.findById(existingId)).thenReturn(Optional.of(product));

        assertNotNull(service.findProductById(existingId));
        assertThat(service.findProductById(existingId)).isEqualTo(product);
    }

    @Test
    public void findByIdShouldThrowExceptionWhenIdDontExists() {
        when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findProductById(nonExistingId));

        verify(repository, times(1)).findById(nonExistingId);
    }

    @Test
    public void updateShouldUpdateProductWhenIdExists() {
        when(repository.getReferenceById(existingId)).thenReturn(product);

        Product updatedProduct = Factory.createProduct();
        updatedProduct.setName("Iphone");

        when(repository.save(any(Product.class))).thenReturn(updatedProduct);

        updatedProduct = service.update(existingId, updatedProduct);

        assertNotNull(updatedProduct);
        assertThat(updatedProduct.getDescription()).isEqualTo("Good Phone");
    }

    @Test
    public void updateShouldThrowExceptionWhenIdDontExists() {

    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenIdIsDependent() {
        when(repository.existsById(dependentId)).thenReturn(true);
        doThrow(DatabaseException.class).when(repository).deleteById(dependentId);

        assertThrows(DatabaseException.class, () -> {
            service.delete(dependentId);
        });

        verify(repository, times(1)).deleteById(dependentId);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        when(repository.existsById(existingId)).thenReturn(true);
        doNothing().when(repository).deleteById(existingId);

        assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

        verify(repository, times(1)).deleteById(existingId);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        when(repository.existsById(nonExistingId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });

        verify(repository, times(0)).deleteById(nonExistingId);
    }
}
