package com.nathanlucas.nscatalog.services;

import com.nathanlucas.nscatalog.dtos.EmailDTO;
import com.nathanlucas.nscatalog.entities.PasswordRecover;
import com.nathanlucas.nscatalog.entities.Role;
import com.nathanlucas.nscatalog.entities.User;
import com.nathanlucas.nscatalog.projections.UserDetailsProjection;
import com.nathanlucas.nscatalog.repositories.PasswordRecoverRepository;
import com.nathanlucas.nscatalog.repositories.UserRepository;
import com.nathanlucas.nscatalog.services.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService implements UserDetailsService {

    @Value("${email.password-recover.token.minutes}")
    private Long tokenMinutes;

    @Value("${email.password-recover.uri}")
    private String recoverUri;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordRecoverRepository passwordRecoverRepository;

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

    public void createRecoverToken(EmailDTO body) {
        Optional<User> user = userRepository.findByEmail(body.getEmail());
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("Email not found");
        }
        PasswordRecover entity = new PasswordRecover();
        String token = UUID.randomUUID().toString();
        entity.setEmail(body.getEmail());
        entity.setToken(token);
        entity.setExpiration(Instant.now().plusSeconds(tokenMinutes * 60L));
        passwordRecoverRepository.save(entity);

        String text = "Acesse o link para recuperar a senha: " + recoverUri + token
                + "\n\nO link expira em " + tokenMinutes + " minutos";

        emailService.sendEmail(body.getEmail(), "Recuperação de senha", text);
    }
}
