package com.nathanlucas.nscatalog.services;

import com.nathanlucas.nscatalog.entities.Role;
import com.nathanlucas.nscatalog.entities.User;
import com.nathanlucas.nscatalog.projections.UserDetailsProjection;
import com.nathanlucas.nscatalog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> result = userRepository.searchUserAndRolesByEmail(username);
        if (result.isEmpty()) {
            throw new UsernameNotFoundException("Email not found");
        }
        User user = customLoadUser(username, result);
        return user;
    }

    private User customLoadUser(String username, List<UserDetailsProjection> projections) {
        User user = new User();
        user.setEmail(username);
        user.setPassword(projections.get(0).getPassword());
        for (UserDetailsProjection projection : projections) {
            user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
        }
        return user;
    }

}
