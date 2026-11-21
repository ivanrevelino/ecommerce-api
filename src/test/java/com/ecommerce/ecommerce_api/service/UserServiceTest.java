package com.ecommerce.ecommerce_api.service;

import com.ecommerce.ecommerce_api.dto.user.UserPasswordUpdateDTO;
import com.ecommerce.ecommerce_api.dto.user.UserUpdateDTO;
import com.ecommerce.ecommerce_api.exception.BadRequestException;
import com.ecommerce.ecommerce_api.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce_api.models.User;
import com.ecommerce.ecommerce_api.models.enums.UserRoles;
import com.ecommerce.ecommerce_api.repository.UserRepository;
import com.ecommerce.ecommerce_api.util.Generator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private BCryptPasswordEncoder bcrypt;

    @InjectMocks
    private UserService service;

    @Test
    void findAllShouldMapUsers() {
        when(repository.findAll()).thenReturn(List.of(Generator.user()));

        assertThat(service.findAll()).containsExactly(Generator.userResponse());
    }

    @Test
    void updateShouldChangeUsernameAndRole() {
        User user = Generator.user();
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.findByUsername("updated-user")).thenReturn(Optional.empty());
        when(repository.save(user)).thenReturn(user);

        UserUpdateDTO request = Generator.userUpdate();
        assertThat(service.update(1L, request).username()).isEqualTo("updated-user");
        assertThat(user.getRoles()).isEqualTo(UserRoles.ADMIN);
        verify(repository).save(user);
    }

    @Test
    void updateShouldRejectUsernameUsedByAnotherUser() {
        User user = Generator.user();
        User anotherUser = Generator.user();
        anotherUser.setId(2L);
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.findByUsername("updated-user")).thenReturn(Optional.of(anotherUser));

        assertThatThrownBy(() -> service.update(1L, Generator.userUpdate()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Username already exists");
    }

    @Test
    void updatePasswordShouldEncodeAndSaveNewPassword() {
        User user = Generator.user();
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(bcrypt.matches("old-password", user.getPassword())).thenReturn(true);
        when(bcrypt.encode("new-password")).thenReturn("new-encoded-password");

        service.updatePassword(1L, Generator.userPasswordUpdate());

        assertThat(user.getPassword()).isEqualTo("new-encoded-password");
        verify(repository).save(user);
    }

    @Test
    void updatePasswordShouldRejectIncorrectCurrentPassword() {
        User user = Generator.user();
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(bcrypt.matches(any(), any())).thenReturn(false);

        assertThatThrownBy(() -> service.updatePassword(1L, Generator.userPasswordUpdate()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Current password is incorrect");
    }

    @Test
    void findByIdShouldThrowWhenUserDoesNotExist() {
        when(repository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(404L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id: 404");
    }

    @Test
    void deleteShouldRemoveExistingUser() {
        User user = Generator.user();
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        service.delete(1L);

        verify(repository).delete(user);
    }
}
