package com.nathanlucas.nscatalog.controllers;

import com.nathanlucas.nscatalog.dtos.CategoryDTO;
import com.nathanlucas.nscatalog.dtos.ProductDTO;
import com.nathanlucas.nscatalog.entities.Category;
import com.nathanlucas.nscatalog.entities.Product;
import com.nathanlucas.nscatalog.mappers.ProductMapper;
import com.nathanlucas.nscatalog.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductMapper productMapper;

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(@RequestParam(defaultValue = "", name = "name")String name,
                                                           Pageable pageable) {
        Page<ProductDTO> result = productService.getAllProducts(name, pageable).map(this::mapToDTO);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO result = mapToDTO(productService.findProductById(id));
        return ResponseEntity.ok(result);
    }

    private ProductDTO mapToDTO(Product product) {
        return productMapper.entityToDTO(product);
    }

    private Product mapToEntity(ProductDTO productDTO) {
        return productMapper.dtoToEntity(productDTO);
    }
}
