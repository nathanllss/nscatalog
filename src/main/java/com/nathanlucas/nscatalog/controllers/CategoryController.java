package com.nathanlucas.nscatalog.controllers;

import com.nathanlucas.nscatalog.dtos.CategoryDTO;
import com.nathanlucas.nscatalog.entities.Category;
import com.nathanlucas.nscatalog.mappers.CategoryMapper;
import com.nathanlucas.nscatalog.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/categories")
public class CategoryController {


    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> findAll() {
        List<CategoryDTO> result = categoryService.findAll()
                .stream().map(categoryMapper::entityToDTO).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CategoryDTO> findById(@PathVariable(name = "id") Long id) {
        Category category = categoryService.findById(id);
        CategoryDTO result = categoryMapper.entityToDTO(category);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> insertCategory(@RequestBody CategoryDTO dto) {
        Category cat = categoryService.save(categoryMapper.DTOtoEntity(dto));
        CategoryDTO result = categoryMapper.entityToDTO(cat);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result.getId()).toUri();
        return ResponseEntity.created(uri).body(result);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable(name = "id") Long id, @RequestBody CategoryDTO dto) {
        Category cat = categoryMapper.DTOtoEntity(dto);
        CategoryDTO result = categoryMapper.entityToDTO(categoryService.update(id,cat));
        return ResponseEntity.ok(result);
    }
}
