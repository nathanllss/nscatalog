package com.nathanlucas.nscatalog.services;

import com.nathanlucas.nscatalog.entities.Category;
import com.nathanlucas.nscatalog.repositories.CategoryRepository;
import com.nathanlucas.nscatalog.services.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category findById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) throw new ResourceNotFoundException("Entity not found");
        return category.get();
    }

    @Transactional
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(Long id, Category category) {
        try {
            Category entity = categoryRepository.getReferenceById(id);
            updateEntity(category, entity);
            return categoryRepository.save(entity);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Id not found: " + id);
        }
    }

    private void updateEntity(Category source, Category target) {
        BeanUtils.copyProperties(source, target, "id");
    }
}
