package com.nathanlucas.nscatalog.controllers;

import com.nathanlucas.nscatalog.dtos.RoleDTO;
import com.nathanlucas.nscatalog.dtos.UserDTO;
import com.nathanlucas.nscatalog.dtos.UserInsertDTO;
import com.nathanlucas.nscatalog.entities.User;
import com.nathanlucas.nscatalog.mappers.RoleMapper;
import com.nathanlucas.nscatalog.mappers.UserMapper;
import com.nathanlucas.nscatalog.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;

    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable) {
        Page<UserDTO> result = userService.getAllUsers(pageable).map(this::mapToDTO);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO result = mapToDTO(userService.findUserById(id));
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<UserDTO> insertUser(@Valid @RequestBody UserInsertDTO dto) {
        User user = userService.save(mapInsertToEntity(dto));
        UserDTO result = mapToDTO(user);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result.getId()).toUri();
        return ResponseEntity.created(uri).body(result);
    }


    @PutMapping(value = "/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable(name = "id") Long id, @Valid @RequestBody UserInsertDTO dto) {
        User user = mapInsertToEntity(dto);
        UserDTO result = mapToDTO(userService.update(id, user));
        return ResponseEntity.ok(result);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private UserDTO mapToDTO(User user) {
        return userMapper.entityToDTO(user);
    }

    private User mapToEntity(UserDTO userDTO) {
        return userMapper.dtoToEntity(userDTO);
    }

    private User mapInsertToEntity(UserInsertDTO dto) {
        User user = userMapper.dtoToEntity(dto);
        user.setPassword(dto.getPassword());
        return user;
    }
}
