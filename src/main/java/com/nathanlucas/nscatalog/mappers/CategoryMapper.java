package com.nathanlucas.nscatalog.mappers;

import com.nathanlucas.nscatalog.dtos.CategoryDTO;
import com.nathanlucas.nscatalog.entities.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDTO entityToDTO(Category category);

    Category DTOtoEntity(CategoryDTO categoryDTO);
}
