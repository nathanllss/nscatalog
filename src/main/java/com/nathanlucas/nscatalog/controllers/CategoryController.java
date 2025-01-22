package com.nathanlucas.nscatalog.controllers;

import com.nathanlucas.nscatalog.dtos.CategoryDTO;
import com.nathanlucas.nscatalog.entities.Category;
import com.nathanlucas.nscatalog.mappers.CategoryMapper;
import com.nathanlucas.nscatalog.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/categories")
public class CategoryController {


    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping
    public ResponseEntity<Page<CategoryDTO>> findAll(@RequestParam(defaultValue = "", name = "name")String name,
                                                     Pageable pageable) {
        Page<CategoryDTO> result = categoryService.findAll(name,pageable).map(this::mapToDTO);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CategoryDTO> findById(@PathVariable(name = "id") Long id) {
        Category category = categoryService.findById(id);
        CategoryDTO result = mapToDTO(category);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<CategoryDTO> insertCategory(@RequestBody CategoryDTO dto) {
        Category cat = categoryService.save(mapToEntity(dto));
        CategoryDTO result = mapToDTO(cat);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result.getId()).toUri();
        return ResponseEntity.created(uri).body(result);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable(name = "id") Long id, @RequestBody CategoryDTO dto) {
        Category cat = mapToEntity(dto);
        CategoryDTO result = mapToDTO(categoryService.update(id,cat));
        return ResponseEntity.ok(result);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<Void> deleteCategory(@PathVariable(name = "id") Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private CategoryDTO mapToDTO(Category entity) {
        return categoryMapper.entityToDTO(entity);
    }

    private Category mapToEntity(CategoryDTO dto) {
        return categoryMapper.DTOtoEntity(dto);
    }
}
