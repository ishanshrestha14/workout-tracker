package com.workouttracker.service;

import com.workouttracker.model.User;
import com.workouttracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Creates the initial admin account from ADMIN_USERNAME / ADMIN_EMAIL / ADMIN_PASSWORD
 * when they are set. Existing accounts are never promoted, so registering the
 * configured username first cannot be used to gain admin rights.
 */
@Service
public class AdminBootstrapService implements CommandLineRunner {

    static final int MIN_PASSWORD_LENGTH = 12;

    private static final Logger logger = LoggerFactory.getLogger(AdminBootstrapService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:}")
    private String username;

    @Value("${app.admin.email:}")
    private String email;

    @Value("${app.admin.password:}")
    private String password;

    @Override
    @Transactional
    public void run(String... args) {
        if (!StringUtils.hasText(username)) {
            return;
        }

        if (!StringUtils.hasText(email) || password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalStateException("ADMIN_USERNAME is set, so ADMIN_EMAIL and an ADMIN_PASSWORD of at least "
                    + MIN_PASSWORD_LENGTH + " characters are required");
        }

        userRepository.findByUsername(username).ifPresentOrElse(
                existing -> {
                    if (existing.getRole() != User.Role.ADMIN) {
                        logger.warn("Admin bootstrap skipped: user '{}' already exists and is not an admin. "
                                + "Promote it manually if that is intended.", username);
                    }
                },
                this::createAdmin);
    }

    private void createAdmin() {
        if (userRepository.existsByEmail(email)) {
            logger.warn("Admin bootstrap skipped: email '{}' is already in use", email);
            return;
        }

        User admin = new User(username, email, passwordEncoder.encode(password));
        admin.setRole(User.Role.ADMIN);
        userRepository.save(admin);
        logger.info("Created admin user '{}'", username);
    }
}
