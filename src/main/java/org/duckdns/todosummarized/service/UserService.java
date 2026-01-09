package org.duckdns.todosummarized.service;

import lombok.RequiredArgsConstructor;
import org.duckdns.todosummarized.domains.entity.User;
import org.duckdns.todosummarized.domains.enums.Role;
import org.duckdns.todosummarized.dto.UserRegistrationDTO;
import org.duckdns.todosummarized.dto.UserResponseDTO;
import org.duckdns.todosummarized.exception.UserAlreadyExistsException;
import org.duckdns.todosummarized.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * Service for user account management operations.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new user.
     *
     * @param userRegistrationDTO the registration details
     * @return the created user response
     * @throws UserAlreadyExistsException if email is already registered
     */
    @Transactional
    public UserResponseDTO registerUser(UserRegistrationDTO userRegistrationDTO) {
        String email = normalizeEmail(userRegistrationDTO.getEmail());

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email);
        }

        User user = toNewUser(email, userRegistrationDTO.getPassword());
        return UserResponseDTO.fromEntity(userRepository.save(user));
    }

    /**
     * Get the current authenticated user's profile.
     *
     * @param email the user's email
     * @return the user response DTO
     */
    @Transactional(readOnly = true)
    public UserResponseDTO getUserProfile(String email) {
        User user = userRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return UserResponseDTO.fromEntity(user);
    }

    private User toNewUser(String email, String rawPassword) {
        return User.builder()
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.ROLE_USER)
                .build();
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}

