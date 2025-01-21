package com.nathanlucas.nscatalog.mappers;

import com.nathanlucas.nscatalog.dtos.UserDTO;
import com.nathanlucas.nscatalog.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO entityToDTO(User user);
    User dtoToEntity(UserDTO userDTO);

}
