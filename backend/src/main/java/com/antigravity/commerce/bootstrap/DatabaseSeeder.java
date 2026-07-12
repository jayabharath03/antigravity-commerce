package com.antigravity.commerce.bootstrap;

import com.antigravity.commerce.entity.Brand;
import com.antigravity.commerce.entity.Category;
import com.antigravity.commerce.entity.Product;
import com.antigravity.commerce.entity.ProductStatus;
import com.antigravity.commerce.entity.Role;
import com.antigravity.commerce.entity.User;
import com.antigravity.commerce.repository.BrandRepository;
import com.antigravity.commerce.repository.CategoryRepository;
import com.antigravity.commerce.repository.ProductRepository;
import com.antigravity.commerce.repository.RoleRepository;
import com.antigravity.commerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Starting Database Seeding...");

        // 1. Seed Roles
        Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> roleRepository.save(new Role(null, "ADMIN")));
        Role customerRole = roleRepository.findByName("CUSTOMER").orElseGet(() -> roleRepository.save(new Role(null, "CUSTOMER")));

        // 2. Seed Admin User
        if (userRepository.findByEmail("admin@antigravity.com").isEmpty()) {
            User admin = User.builder()
                    .firstName("Super")
                    .lastName("Admin")
                    .email("admin@antigravity.com")
                    .password(passwordEncoder.encode("admin123"))
                    .phone("1234567890")
                    .isVerified(true)
                    .build();
            admin.getRoles().add(adminRole);
            userRepository.save(admin);
            log.info("Seeded Admin user: admin@antigravity.com / admin123");
        }

        // 3. Seed Customer User
        if (userRepository.findByEmail("customer@antigravity.com").isEmpty()) {
            User customer = User.builder()
                    .firstName("Test")
                    .lastName("Customer")
                    .email("customer@antigravity.com")
                    .password(passwordEncoder.encode("password"))
                    .phone("0987654321")
                    .isVerified(true)
                    .build();
            customer.getRoles().add(customerRole);
            userRepository.save(customer);
            log.info("Seeded Customer user: customer@antigravity.com / password");
        }

        // 4. Seed Catalog (Categories, Brands, Products) if empty
        if (categoryRepository.count() == 0) {
            log.info("Seeding Products Catalog...");
            
            Category electronics = categoryRepository.save(Category.builder().name("Electronics").slug("electronics").description("Tech Gadgets").build());
            Category clothing = categoryRepository.save(Category.builder().name("Clothing").slug("clothing").description("Apparel").build());

            Brand apple = brandRepository.save(Brand.builder().name("Apple").slug("apple").description("Think Different").build());
            Brand nike = brandRepository.save(Brand.builder().name("Nike").slug("nike").description("Just Do It").build());

            Product p1 = Product.builder()
                    .name("iPhone 16 Pro")
                    .slug("iphone-16-pro")
                    .shortDescription("The ultimate titanium iPhone.")
                    .category(electronics)
                    .brand(apple)
                    .status(ProductStatus.ACTIVE)
                    .build();
            productRepository.save(p1);

            Product p2 = Product.builder()
                    .name("Nike Air Max")
                    .slug("nike-air-max")
                    .shortDescription("Classic comfort running shoes.")
                    .category(clothing)
                    .brand(nike)
                    .status(ProductStatus.ACTIVE)
                    .build();
            productRepository.save(p2);
            
            log.info("Catalog Seeded Successfully!");
        }
    }
}
