package com.nathanlucas.nscatalog.services;

import com.nathanlucas.nscatalog.entities.Role;
import com.nathanlucas.nscatalog.entities.User;
import com.nathanlucas.nscatalog.repositories.RoleRepository;
import com.nathanlucas.nscatalog.repositories.UserRepository;
import com.nathanlucas.nscatalog.services.exception.DatabaseException;
import com.nathanlucas.nscatalog.services.exception.ResourceNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public User findUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) throw new ResourceNotFoundException("Entity not found");
        return user.get();
    }

    @Transactional
    public User save(User user) {
        setUserRolesForResponse(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public User update(Long id, User category) {
        try {
            User entity = userRepository.getReferenceById(id);
            updateEntity(category, entity);
            return userRepository.save(entity);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Id not found: " + id);
        }
    }

    private void updateEntity(User source, User target) {
        target.getRoles().clear();
        BeanUtils.copyProperties(source, target, "id");
        target.getRoles().addAll(source.getRoles());
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Id not found: " + id);
        }
        try {
            userRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }
    }

    private void setUserRolesForResponse(User user) {
        for (Role role : user.getRoles()) {
            role.setAuthority(roleRepository.findById(role.getId()).get().getAuthority());
        }
    }

}
