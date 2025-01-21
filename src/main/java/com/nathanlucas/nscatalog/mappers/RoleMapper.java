package com.nathanlucas.nscatalog.mappers;

import com.nathanlucas.nscatalog.dtos.RoleDTO;
import com.nathanlucas.nscatalog.entities.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDTO entityToDTO(Role role);

    Role dtoToEntity(RoleDTO roleDTO);

}
