package com.nathanlucas.nscatalog.services;

import com.nathanlucas.nscatalog.entities.Category;
import com.nathanlucas.nscatalog.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> findAll() {
        List<Category> list = new ArrayList<>();
        list.add(new Category(1L, "Books"));
        list.add(new Category(1L, "Eletronics"));
        return list;
    }
}
