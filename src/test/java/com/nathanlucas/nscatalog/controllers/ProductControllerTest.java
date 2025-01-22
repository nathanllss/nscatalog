package com.nathanlucas.nscatalog.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nathanlucas.nscatalog.NscatalogApplication;
import com.nathanlucas.nscatalog.dtos.ProductDTO;
import com.nathanlucas.nscatalog.entities.Product;
import com.nathanlucas.nscatalog.factories.Factory;
import com.nathanlucas.nscatalog.mappers.ProductMapper;
import com.nathanlucas.nscatalog.services.ProductService;
import com.nathanlucas.nscatalog.services.exception.DatabaseException;
import com.nathanlucas.nscatalog.services.exception.ResourceNotFoundException;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = ProductController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ProductService service;
    private Product entity;
    private ProductDTO dto;
    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;

    private PageImpl<Product> page;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        dependentId = 4L;
        nonExistingId = 1000L;
        entity = Factory.createProduct();
        dto = Factory.createProductDTO();
        page = new PageImpl<>(List.of(entity));

    }

    @Test
    public void getAllProductsShouldReturnPageOfDTOs() throws Exception {
        when(service.getAllProducts(anyString(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/products")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void findProductByIdShouldReturnProductDtoWhenIdExists() throws Exception {
        when(service.findProductById(existingId)).thenReturn(entity);

        mockMvc.perform(get("/products/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void findProductByIdShouldReturn404IdDoesNotExist() throws Exception {
        when(service.findProductById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateProductByIdShouldReturnProductDtoWhenIdExists() throws Exception {
        when(service.update(eq(existingId), any(Product.class))).thenReturn(entity);

        String jsonBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/products/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void updateProductByIdShouldReturn404IdDoesNotExist() throws Exception {
        when(service.update(eq(nonExistingId), any(Product.class))).thenThrow(ResourceNotFoundException.class);

        String jsonBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void insertProductShouldSaveProductandReturnCreated() throws Exception {
        when(service.save(any(Product.class))).thenReturn(entity);

        String jsonBody = objectMapper.writeValueAsString(entity);

        mockMvc.perform(post("/products")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isCreated());
    }

    @Test
    public void deleteShouldDeleteProductByIdWhenIdExists() throws Exception {
        doNothing().when(service).delete(existingId);

        mockMvc.perform(delete("/products/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldReturn404IdDoesNotExist() throws Exception {
        doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);

        mockMvc.perform(delete("/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
