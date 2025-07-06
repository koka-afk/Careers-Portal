package com.career.portal.Config;

import com.career.portal.models.User;
import com.career.portal.models.UserRole;
import com.career.portal.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminUserInitializer.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByRole(UserRole.ADMIN).isEmpty()) {
            log.info("No ADMIN user found, creating a default admin user.");

            User adminUser = new User();
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");

            String adminEmail = System.getenv("ADMIN_EMAIL");
            String adminPassword = System.getenv("ADMIN_PASSWORD");

            if (adminEmail == null || adminPassword == null) {
                log.warn("ADMIN_EMAIL or ADMIN_PASSWORD environment variables not set. Using default credentials.");
                adminEmail = "admin@noon.com";
                adminPassword = "adminpassword";
            }

            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setRole(UserRole.ADMIN);

            userRepository.save(adminUser);
            log.info("Default ADMIN user created with email: {}", adminEmail);
        } else {
            log.info("ADMIN user already exists. Skipping creation.");
        }
    }
}
