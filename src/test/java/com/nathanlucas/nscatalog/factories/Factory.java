package com.nathanlucas.nscatalog.factories;


import com.nathanlucas.nscatalog.dtos.ProductDTO;
import com.nathanlucas.nscatalog.entities.Category;
import com.nathanlucas.nscatalog.entities.Product;
import com.nathanlucas.nscatalog.mappers.ProductMapper;
import com.nathanlucas.nscatalog.mappers.ProductMapperImpl;

import java.time.Instant;

public class Factory {

    private static ProductMapper productMapper = new ProductMapperImpl();

    public static Product createProduct() {
        Product product = new Product(1L, "Phone", "Good Phone", 800.0, "https://img.com/img.png");
        product.setCreatedAt(Instant.parse("2020-10-20T03:00:00Z"));
        product.getCategories().add(new Category(1L, "Electronics"));
        return product;
    }

    public static ProductDTO createProductDTO() {
        Product product = createProduct();
        return productMapper.entityToDTO(product);
    }
}
