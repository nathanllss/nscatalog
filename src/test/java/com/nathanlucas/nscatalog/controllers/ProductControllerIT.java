package com.nathanlucas.nscatalog.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nathanlucas.nscatalog.dtos.ProductDTO;
import com.nathanlucas.nscatalog.entities.Product;
import com.nathanlucas.nscatalog.factories.Factory;
import com.nathanlucas.nscatalog.services.exception.ResourceNotFoundException;
import com.nathanlucas.nscatalog.util.TokenUtil;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TokenUtil tokenUtil;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private Long countTotalProducts;
    private String username, password, bearerToken;

    @BeforeEach
    public void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 4L;
        countTotalProducts = 25L;

        username = "maria@gmail.com";
        password = "123456";

        bearerToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
    }

    @Test
    public void getAllProductsShouldReturnSortedPageWhenSortByName() throws Exception {
        mockMvc.perform(get("/products?page=0&size=12&sort=name,asc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(countTotalProducts))
                .andExpect(jsonPath("$.content[0].name").value("Macbook Pro"))
                .andExpect(jsonPath("$.content[1].name").value("PC Gamer"))
                .andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
    }

    @Test
    public void updateProductByIdShouldReturnProductDtoWhenIdExists() throws Exception {
        ProductDTO dto = Factory.createProductDTO();
        String expectedName = dto.getName();

        String jsonBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/products/{id}", existingId)
                        .header("Authorization", "Bearer " + bearerToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(expectedName));
    }

    @Test
    public void updateProductByIdShouldReturn404IdDoesNotExist() throws Exception {
        ProductDTO dto = Factory.createProductDTO();
        String jsonBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/products/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + bearerToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
