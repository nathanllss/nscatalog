package com.nathanlucas.nscatalog.services;

import com.nathanlucas.nscatalog.entities.Product;
import com.nathanlucas.nscatalog.repositories.ProductRepository;
import com.nathanlucas.nscatalog.services.exception.DatabaseException;
import com.nathanlucas.nscatalog.services.exception.ResourceNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(String name, Pageable pageable) {
        return productRepository.searchAll(name, pageable);
    }

    @Transactional(readOnly = true)
    public Product findProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) throw new ResourceNotFoundException("Entity not found");
        return product.get();
    }

    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, Product category) {
        try {
            Product entity = productRepository.getReferenceById(id);
            updateEntity(category, entity);
            return productRepository.save(entity);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Id not found: " + id);
        }
    }

    private void updateEntity(Product source, Product target) {
        BeanUtils.copyProperties(source, target, "id");
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Id not found: " + id);
        }
        try {
            productRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }
    }
}
