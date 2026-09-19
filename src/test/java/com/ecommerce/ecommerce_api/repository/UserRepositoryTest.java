package com.ecommerce.ecommerce_api.repository;

import com.ecommerce.ecommerce_api.models.User;
import com.ecommerce.ecommerce_api.models.enums.UserRoles;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Test
    @DisplayName("Save should persist user when successful")
    void saveShouldPersistUserWhenSuccessful () {
        User user = generateUser();
        Assertions.assertThat(user.getId()).isNotNull();
        Assertions.assertThat(user).isNotNull();
    }

    @Test
    @DisplayName("FindByUsername should return right user when successful")
    void findByUsername() {
        User user = generateUser();
        String username = "banana01";

        User userByUsername = repository.findByUsername(username).orElseThrow();
        Assertions.assertThat(userByUsername.getId()).isNotNull();
        Assertions.assertThat(userByUsername)
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(user);
    }

    @Test
    @DisplayName("FindByUsername should not return user when it not exists")
    void findByUsernameShouldNotReturnUserWhenItNotExists() {
        User user = generateUser();
        String username = "banana02";

        Optional<User> userByUsername = repository.findByUsername(username);
        Assertions.assertThat(userByUsername).isEmpty();
    }

    @Test
    @DisplayName("FindById should return right user when Successful")
    void findByIdShouldReturnRightUserWhenSuccessful(){
        User saved = generateUser();

        Optional<User> optionalUser = repository.findById(saved.getId());
        Assertions.assertThat(optionalUser).isPresent();
        Assertions.assertThat(optionalUser.get().getId()).isNotNull();
        Assertions.assertThat(optionalUser.get())
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(saved);
    }

    @Test
    @DisplayName("FindById should not return user when it not exists")
    void findByIdShouldNotReturnUserWhenItNotExists(){
        User saved = generateUser();
        long unexistedId = 90921;

        Optional<User> optionalUser = repository.findById(unexistedId);
        Assertions.assertThat(optionalUser).isEmpty();
    }

    @Test
    @DisplayName("Delete should remove user when successful")
    void deleteShouldRemoveUserWhenSuccessful() {
        User user = generateUser();

        repository.delete(user);

        Optional<User> optionalUser = repository.findById(user.getId());
        Assertions.assertThat(optionalUser).isEmpty();
    }

    @Test
    @DisplayName("Delete should not remove user when it not exists")
    void deleteShouldNotRemoveUserWhenItNotExists() {
        User user = generateUser();

        repository.delete(user);

        Optional<User> optionalUser = repository.findById(user.getId());
        Assertions.assertThat(optionalUser).isEmpty();
    }

    private User generateUser() {
        User user = User.builder()
                .username("banana01")
                .password("banana123")
                .roles(UserRoles.ADMIN)
                .createdAt(LocalDateTime.now())
                .build();
        return repository.save(user);
    }
}