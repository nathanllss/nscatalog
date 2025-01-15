package com.nathanlucas.nscatalog.services;

import com.nathanlucas.nscatalog.entities.Category;
import com.nathanlucas.nscatalog.entities.Product;
import com.nathanlucas.nscatalog.repositories.ProductRepository;
import com.nathanlucas.nscatalog.services.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(String name, Pageable pageable) {
        return productRepository.searchAll(name,pageable);
    }

    @Transactional(readOnly = true)
    public Product findProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) throw new ResourceNotFoundException("Entity not found");
        return product.get();
    }
}
