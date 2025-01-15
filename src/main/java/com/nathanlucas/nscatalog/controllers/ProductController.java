package com.nathanlucas.nscatalog.controllers;

import com.nathanlucas.nscatalog.dtos.CategoryDTO;
import com.nathanlucas.nscatalog.dtos.ProductDTO;
import com.nathanlucas.nscatalog.entities.Category;
import com.nathanlucas.nscatalog.entities.Product;
import com.nathanlucas.nscatalog.mappers.ProductMapper;
import com.nathanlucas.nscatalog.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductMapper productMapper;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> result = productService.getAllProducts()
                .stream().map(this::mapToDTO).toList();
        return ResponseEntity.ok(result);
    }

    private ProductDTO mapToDTO(Product product) {
        return productMapper.entityToDTO(product);
    }

    private Product mapToEntity(ProductDTO productDTO) {
        return productMapper.dtoToEntity(productDTO);
    }
}
