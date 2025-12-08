package dev.yurets.db_demo;

import dev.yurets.db_demo.model.Country;
import dev.yurets.db_demo.model.Donor;
import dev.yurets.db_demo.model.Period;
import dev.yurets.db_demo.model.User;
import dev.yurets.db_demo.model.Weapon;
import dev.yurets.db_demo.model.WeaponDelivery;
import dev.yurets.db_demo.repository.CountryRepository;
import dev.yurets.db_demo.repository.DonorRepository;
import dev.yurets.db_demo.repository.PeriodRepository;
import dev.yurets.db_demo.repository.UserRepository;
import dev.yurets.db_demo.repository.WeaponDeliveryRepository;
import dev.yurets.db_demo.repository.WeaponRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootApplication
public class MilitaryAidApplication {

    public static void main(String[] args) {
        SpringApplication.run(MilitaryAidApplication.class, args);

        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║   🎖️  ВЕБ-ЗАСТОСУНОК ВІЙСЬКОВОЇ ДОПОМОГИ ЗАПУЩЕНО!   ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println("\n📱 Веб-інтерфейс: http://localhost:8080");
        System.out.println("🔐 Сторінка логіну: http://localhost:8080/login");
        System.out.println("\n👤 Тестові користувачі:");
        System.out.println("   ADMIN: admin / admin (повний доступ)");
        System.out.println("   USER:  user / user   (тільки перегляд)");
        System.out.println("\n🔗 REST API Endpoints:");
        System.out.println("   GET    http://localhost:8080/api/countries");
        System.out.println("   GET    http://localhost:8080/api/periods");
        System.out.println("   GET    http://localhost:8080/api/weapons");
        System.out.println("   (POST/PUT/DELETE доступні тільки для ADMIN)");
        System.out.println("\n💡 Для тестування використовуйте:");
        System.out.println("   - Браузер (для GET запитів)");
        System.out.println("   - curl або Postman (для всіх методів)");
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
    }

    @Bean
    public CommandLineRunner loadInitialData(CountryRepository countryRepo,
                                             PeriodRepository periodRepo,
                                             WeaponRepository weaponRepo,
                                             UserRepository userRepo,
                                             DonorRepository donorRepo,
                                             WeaponDeliveryRepository deliveryRepo,
                                             PasswordEncoder passwordEncoder) {
        return (args) -> {
            System.out.println("\n--- [INIT] Початкове завантаження даних ---");

            // ========== СТВОРЕННЯ КОРИСТУВАЧІВ ==========
            if (userRepo.count() == 0) {
                System.out.println("[INIT] Створення користувачів...");

                // ADMIN: admin / admin (повний доступ)
                User admin = new User(
                        "admin",
                        passwordEncoder.encode("admin"), // BCrypt хеш
                        "ADMIN"
                );
                userRepo.save(admin);

                // USER: user / user (тільки перегляд)
                User user = new User(
                        "user",
                        passwordEncoder.encode("user"), // BCrypt хеш
                        "USER"
                );
                userRepo.save(user);

                System.out.println("[INIT] ✅ Користувачі створено:");
                System.out.println("       - admin/admin (ADMIN)");
                System.out.println("       - user/user (USER)");
            } else {
                System.out.println("[INIT] Користувачі вже існують.");
            }

            // ========== СТВОРЕННЯ ДАНИХ (країни, періоди, зброя) ==========
            if (countryRepo.count() > 0) {
                System.out.println("[INIT] Дані вже існують. Пропускаємо ініціалізацію.");
                return;
            }

            // --- Створюємо Країни (3) ---
            System.out.println("[INIT] Створення країн...");
            Country usa = new Country("USA", new BigDecimal("75000000000"));
            Country germany = new Country("Germany", new BigDecimal("28000000000"));
            Country uk = new Country("United Kingdom", new BigDecimal("15000000000"));

            countryRepo.save(usa);
            countryRepo.save(germany);
            countryRepo.save(uk);
            System.out.println("[INIT] ✅ Країни створено.");

            // --- Створюємо Періоди (3) ---
            System.out.println("[INIT] Створення періодів...");
            Period usaPeriod1 = new Period(
                    "2022 Q1-Q2",
                    LocalDate.of(2022, 1, 1),
                    LocalDate.of(2022, 6, 30),
                    new BigDecimal("15000000000"),
                    usa
            );

            Period usaPeriod2 = new Period(
                    "2023 Full Year",
                    LocalDate.of(2023, 1, 1),
                    LocalDate.of(2023, 12, 31),
                    new BigDecimal("35000000000"),
                    usa
            );

            Period germanyPeriod = new Period(
                    "2022-2023",
                    LocalDate.of(2022, 2, 1),
                    LocalDate.of(2023, 12, 31),
                    new BigDecimal("28000000000"),
                    germany
            );

            periodRepo.save(usaPeriod1);
            periodRepo.save(usaPeriod2);
            periodRepo.save(germanyPeriod);
            System.out.println("[INIT] ✅ Періоди створено.");

            // --- Створюємо Зброю (3) ---
            System.out.println("[INIT] Створення записів зброї...");
            Weapon howitzer = new Weapon(
                    "Artillery",
                    "M777 Howitzer",
                    90,
                    new BigDecimal("2500000"),
                    new BigDecimal("225000000"),
                    usaPeriod1
            );

            Weapon javelin = new Weapon(
                    "Air Defense",
                    "Javelin Anti-Tank Missile",
                    5000,
                    new BigDecimal("178000"),
                    new BigDecimal("890000000"),
                    usaPeriod1
            );

            Weapon leopard = new Weapon(
                    "Air Defense",
                    "Leopard 2 Tank",
                    18,
                    new BigDecimal("12000000"),
                    new BigDecimal("216000000"),
                    germanyPeriod
            );

            weaponRepo.save(howitzer);
            weaponRepo.save(javelin);
            weaponRepo.save(leopard);
            System.out.println("[INIT] ✅ Зброя додана.");

            // --- Створюємо Донорів (3) ---
            System.out.println("[INIT] Створення донорів...");
            Donor pentagon = new Donor("Pentagon", "урядова", "defense@us.gov", usa);
            Donor usaid = new Donor("US Agency for International Development", "урядова", "contact@usaid.gov", usa);
            Donor bundeswehr = new Donor("Bundeswehr", "урядова", "info@bundeswehr.de", germany);

            donorRepo.save(pentagon);
            donorRepo.save(usaid);
            donorRepo.save(bundeswehr);
            System.out.println("[INIT] ✅ Донори створено.");

            // --- Створюємо Поставки (3) ---
            System.out.println("[INIT] Створення поставок...");
            WeaponDelivery delivery1 = new WeaponDelivery(
                    LocalDate.of(2022, 3, 15),
                    45,
                    "delivered",
                    "USA-HOW-001",
                    howitzer,
                    pentagon
            );

            WeaponDelivery delivery2 = new WeaponDelivery(
                    LocalDate.of(2022, 4, 20),
                    2500,
                    "delivered",
                    "USA-JAV-002",
                    javelin,
                    usaid
            );

            WeaponDelivery delivery3 = new WeaponDelivery(
                    LocalDate.of(2023, 1, 10),
                    18,
                    "delivered",
                    "DE-LEO-003",
                    leopard,
                    bundeswehr
            );

            deliveryRepo.save(delivery1);
            deliveryRepo.save(delivery2);
            deliveryRepo.save(delivery3);
            System.out.println("[INIT] ✅ Поставки створено.");

            System.out.println("--- [INIT] ✅ Початкове завантаження даних ЗАВЕРШЕНО ---\n");
        };
    }
}