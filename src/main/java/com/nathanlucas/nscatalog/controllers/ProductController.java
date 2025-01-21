package com.nathanlucas.nscatalog.controllers;

import com.nathanlucas.nscatalog.dtos.ProductDTO;
import com.nathanlucas.nscatalog.entities.Product;
import com.nathanlucas.nscatalog.mappers.ProductMapper;
import com.nathanlucas.nscatalog.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductMapper productMapper;

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(@RequestParam(defaultValue = "", name = "name") String name,
                                                           Pageable pageable) {
        Page<ProductDTO> result = productService.getAllProducts(name, pageable).map(this::mapToDTO);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO result = mapToDTO(productService.findProductById(id));
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> insertProduct(@Valid @RequestBody ProductDTO dto) {
        Product product = productService.save(mapToEntity(dto));
        ProductDTO result = mapToDTO(product);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result.getId()).toUri();
        return ResponseEntity.created(uri).body(result);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable(name = "id") Long id,@Valid @RequestBody ProductDTO dto) {
        Product product = mapToEntity(dto);
        ProductDTO result = mapToDTO(productService.update(id, product));
        return ResponseEntity.ok(result);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable(name = "id") Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ProductDTO mapToDTO(Product product) {
        return productMapper.entityToDTO(product);
    }

    private Product mapToEntity(ProductDTO productDTO) {
        return productMapper.dtoToEntity(productDTO);
    }
}
