package com.ecommerce.ecommerce_api.controller;

import com.ecommerce.ecommerce_api.dto.user.UserPasswordUpdateDTO;
import com.ecommerce.ecommerce_api.dto.user.UserResponseDTO;
import com.ecommerce.ecommerce_api.dto.user.UserUpdateDTO;
import com.ecommerce.ecommerce_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping("filter/{username}")
    public ResponseEntity<UserResponseDTO> findByUsername(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id,
                                                  @RequestBody @Valid UserUpdateDTO request) {
        return userService.update(id, request);
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id,
                                               @RequestBody @Valid UserPasswordUpdateDTO request) {
        return userService.updatePassword(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return userService.delete(id);
    }
}
