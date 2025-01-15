package com.nathanlucas.nscatalog.mappers;

import com.nathanlucas.nscatalog.dtos.ProductDTO;
import com.nathanlucas.nscatalog.entities.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDTO entityToDTO(Product product);
    Product dtoToEntity(ProductDTO productDTO);

}
