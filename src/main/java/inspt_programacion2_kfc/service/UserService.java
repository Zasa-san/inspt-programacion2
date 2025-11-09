package inspt_programacion2_kfc.service;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inspt_programacion2_kfc.model.Role;
import inspt_programacion2_kfc.model.User;
import inspt_programacion2_kfc.repository.UserRepository;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User create(String username, String rawPassword, Role role, boolean resetIfExists) {
        Optional<User> dbUser = userRepository.findByUsername(username);
        if (dbUser.isEmpty()) {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(passwordEncoder.encode(rawPassword));
            newUser.setRole(role);
            newUser.setEnabled(true);
            try {
                return userRepository.save(newUser);
            } catch (DataIntegrityViolationException ex) {
                throw new UserCreationFailedException("Could not create user " + username, ex);
            }
        } else if (resetIfExists) {
            User existing = dbUser.get();
            existing.setPassword(passwordEncoder.encode(rawPassword));
            try {
                return userRepository.save(existing);
            } catch (DataIntegrityViolationException ex) {

                throw new UserPasswordResetFailedException("Could not reset password for user " + username, ex);
            }
        }
        return dbUser.get();
    }
}
