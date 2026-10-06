package com.workouttracker.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void newUsersAreRegularUsers() {
        User user = new User("alice", "alice@example.com", "hash");

        assertThat(user.getRole()).isEqualTo(User.Role.USER);
        assertThat(user.getAuthorities()).extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER");
    }

    @Test
    void adminsAlsoHaveUserRole() {
        User admin = new User("admin", "admin@example.com", "hash");
        admin.setRole(User.Role.ADMIN);

        assertThat(admin.getAuthorities()).extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }
}
