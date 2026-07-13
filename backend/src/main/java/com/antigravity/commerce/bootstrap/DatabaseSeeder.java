package com.antigravity.commerce.bootstrap;

import com.antigravity.commerce.entity.Brand;
import com.antigravity.commerce.entity.Category;
import com.antigravity.commerce.entity.Product;
import com.antigravity.commerce.entity.ProductImage;
import com.antigravity.commerce.entity.ProductStatus;
import com.antigravity.commerce.entity.ProductVariant;
import com.antigravity.commerce.entity.Role;
import com.antigravity.commerce.entity.User;

import java.math.BigDecimal;
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

            Category electronics = categoryRepository.save(Category.builder().name("Electronics").slug("electronics").description("Phones, laptops & gadgets").build());
            Category audio = categoryRepository.save(Category.builder().name("Audio").slug("audio").description("Headphones & speakers").build());
            Category wearables = categoryRepository.save(Category.builder().name("Wearables").slug("wearables").description("Smartwatches & bands").build());
            Category footwear = categoryRepository.save(Category.builder().name("Footwear").slug("footwear").description("Sneakers & shoes").build());
            Category clothing = categoryRepository.save(Category.builder().name("Clothing").slug("clothing").description("Apparel").build());

            Brand apple = brandRepository.save(Brand.builder().name("Apple").slug("apple").description("Think Different").build());
            Brand nike = brandRepository.save(Brand.builder().name("Nike").slug("nike").description("Just Do It").build());
            Brand sony = brandRepository.save(Brand.builder().name("Sony").slug("sony").description("Make. Believe.").build());
            Brand samsung = brandRepository.save(Brand.builder().name("Samsung").slug("samsung").description("Do What You Can't").build());

            String img = "https://images.unsplash.com/";
            // Stock levels are chosen to showcase the realtime badges: some low, one out of stock.
            seedProduct("iPhone 16 Pro", "iphone-16-pro", "The ultimate titanium iPhone.", electronics, apple, "IPH16-PRO-256", "1199.00", 25, img + "photo-1511707171634-5f897ff02aa9?q=80&w=800&auto=format&fit=crop");
            seedProduct("MacBook Air M3", "macbook-air-m3", "Impossibly thin. Incredibly fast.", electronics, apple, "MBA-M3-256", "1099.00", 15, img + "photo-1496181133206-80ce9b88a853?q=80&w=800&auto=format&fit=crop");
            seedProduct("Samsung Galaxy S24 Ultra", "galaxy-s24-ultra", "The AI phone with a titanium frame.", electronics, samsung, "SAMS24U-512", "999.00", 0, img + "photo-1610945265064-0e34e5519bbf?q=80&w=800&auto=format&fit=crop");
            seedProduct("Sony PlayStation 5", "playstation-5", "Play has no limits.", electronics, sony, "SONY-PS5", "549.00", 5, img + "photo-1606813907291-d86efa9b94db?q=80&w=800&auto=format&fit=crop");
            seedProduct("Sony WH-1000XM5", "sony-wh-1000xm5", "Industry-leading noise cancellation.", audio, sony, "SONYXM5-BLK", "399.00", 12, img + "photo-1505740420928-5e560c06d30e?q=80&w=800&auto=format&fit=crop");
            seedProduct("Apple Watch Series 10", "apple-watch-series-10", "Your essential health companion.", wearables, apple, "AWS10-GPS", "499.00", 4, img + "photo-1523275335684-37898b6baf30?q=80&w=800&auto=format&fit=crop");
            seedProduct("Nike Air Max 90", "nike-air-max-90", "Classic comfort, iconic style.", footwear, nike, "NIKE-AM90-42", "129.99", 3, img + "photo-1542291026-7eec264c27ff?q=80&w=800&auto=format&fit=crop");
            seedProduct("Nike Dri-FIT Tee", "nike-dri-fit-tee", "Sweat-wicking everyday training tee.", clothing, nike, "NIKE-TEE-M", "34.99", 50, img + "photo-1521572163474-6864f9cf17ab?q=80&w=800&auto=format&fit=crop");

            log.info("Catalog Seeded Successfully!");
        }
    }

    /** Creates a product with a single variant (price + stock) and a primary image. */
    private void seedProduct(String name, String slug, String description, Category category, Brand brand,
                             String sku, String price, int stock, String imageUrl) {
        Product product = Product.builder()
                .name(name)
                .slug(slug)
                .shortDescription(description)
                .category(category)
                .brand(brand)
                .status(ProductStatus.ACTIVE)
                .build();

        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .sku(sku)
                .price(new BigDecimal(price))
                .stockQuantity(stock)
                .status(ProductStatus.ACTIVE)
                .build();
        product.getVariants().add(variant);

        ProductImage image = ProductImage.builder()
                .product(product)
                .imageUrl(imageUrl)
                .isPrimary(true)
                .displayOrder(0)
                .build();
        product.getImages().add(image);

        productRepository.save(product);
    }
}
