package com.ecommerce.ecommerce_api.service;

import com.ecommerce.ecommerce_api.dto.user.UserPasswordUpdateDTO;
import com.ecommerce.ecommerce_api.dto.user.UserRequestDTO;
import com.ecommerce.ecommerce_api.dto.user.UserResponseDTO;
import com.ecommerce.ecommerce_api.dto.user.UserUpdateDTO;
import com.ecommerce.ecommerce_api.exception.BadRequestException;
import com.ecommerce.ecommerce_api.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce_api.models.User;
import com.ecommerce.ecommerce_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bcrypt;

    public User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public ResponseEntity<List<UserResponseDTO>> findAll() {
        List<UserResponseDTO> users = userRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();

        return ResponseEntity.ok(users);
    }

    public ResponseEntity<UserResponseDTO> findById(Long id) {
        User user = findUserById(id);
        return ResponseEntity.ok(toResponseDTO(user));
    }

    public ResponseEntity<UserResponseDTO> findByUsername(String username) {
        User user = findUserByUsername(username);
        return ResponseEntity.ok(toResponseDTO(user));
    }

    public ResponseEntity<UserResponseDTO> update(Long id, UserUpdateDTO request) {
        User user = findUserById(id);
        validateUsernameIsAvailable(request.username(), id);

        user.setUsername(request.username());
        user.setRoles(request.role());

        User updated = userRepository.save(user);

        log.info("User(id: {}, username: {}, role: {}) updated successfully", updated.getId(), updated.getUsername(), updated.getRoles());
        return ResponseEntity.ok(toResponseDTO(updated));
    }

    public ResponseEntity<Void> updatePassword(Long id, UserPasswordUpdateDTO request) {
        User user = findUserById(id);

        if (!bcrypt.matches(request.currentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPassword(bcrypt.encode(request.newPassword()));
        userRepository.save(user);

        log.info("User(id: {}, username: {}) updated password successfully", user.getId(), user.getUsername());
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Void> delete(Long id) {
        User user = findUserById(id);
        userRepository.delete(user);

        log.info("User(id: {}, username: {}) deleted successfully", user.getId(), user.getUsername());
        return ResponseEntity.noContent().build();
    }

    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    private void validateUsernameIsAvailable(String username, Long userIdToIgnore) {
        userRepository.findByUsername(username)
                .filter(user -> !user.getId().equals(userIdToIgnore))
                .ifPresent(user -> {
                    throw new BadRequestException("Username already exists");
                });
    }

    private UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getRoles(),
                user.getCreatedAt()
        );
    }
}
