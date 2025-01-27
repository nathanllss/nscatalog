package com.nathanlucas.nscatalog.services;

import com.nathanlucas.nscatalog.entities.Category;
import com.nathanlucas.nscatalog.entities.Product;
import com.nathanlucas.nscatalog.projections.ProductProjection;
import com.nathanlucas.nscatalog.repositories.ProductRepository;
import com.nathanlucas.nscatalog.services.exception.DatabaseException;
import com.nathanlucas.nscatalog.services.exception.ResourceNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
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
    public Page<Product> getAllProductsPaged(String name, Pageable pageable, String categoryId) {
        List<Long> categoryIds = List.of();
        if (!categoryId.equals("0")) {
            categoryIds = Arrays.stream(categoryId.split(","))
                .map(Long::parseLong).toList();
        }
        Page<ProductProjection> page = productRepository.searchProducts(name, pageable,categoryIds);
        List<Long> productIds = page.map(ProductProjection::getId).toList();
        List<Product> entities = productRepository.searchProductWithCategories(productIds);
        return new PageImpl<>(entities, page.getPageable(), page.getTotalElements());
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
    public Product update(Long id, Product product) {
        try {
            Product entity = productRepository.getReferenceById(id);
            updateEntity(product, entity);
            return productRepository.save(entity);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Id not found: " + id);
        }
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

    private void updateEntity(Product source, Product target) {
        target.getCategories().clear();
        BeanUtils.copyProperties(source, target, "id");
        target.getCategories().addAll(source.getCategories());
    }
}
