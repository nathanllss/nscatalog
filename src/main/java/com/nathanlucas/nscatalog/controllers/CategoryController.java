package com.nathanlucas.nscatalog.controllers;

import com.nathanlucas.nscatalog.dtos.CategoryDTO;
import com.nathanlucas.nscatalog.mappers.CategoryMapper;
import com.nathanlucas.nscatalog.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
