package dev.yurets.db_demo;

import dev.yurets.db_demo.model.Country;
import dev.yurets.db_demo.model.Period;
import dev.yurets.db_demo.model.Weapon;
import dev.yurets.db_demo.repository.CountryRepository;
import dev.yurets.db_demo.repository.PeriodRepository;
import dev.yurets.db_demo.repository.WeaponRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootApplication
public class MilitaryAidApplication {

    public static void main(String[] args) {
        SpringApplication.run(MilitaryAidApplication.class, args);

        System.out.println("\n--- ВЕБ-ДОДАТОК ВІЙСЬКОВОЇ ДОПОМОГИ УСПІШНО ЗАПУЩЕНО! ---");
        System.out.println("Відкрийте браузер і перейдіть на: http://localhost:8080");
    }

    @Bean
    public CommandLineRunner loadInitialData(CountryRepository countryRepo,
                                             PeriodRepository periodRepo,
                                             WeaponRepository weaponRepo) {
        return (args) -> {
            System.out.println("\n--- [INIT] Початкове завантаження даних ---");

            // Перевіримо, чи база порожня, щоб не дублювати дані при перезапуску
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
            System.out.println("[INIT] Країни створено.");

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
            System.out.println("[INIT] Періоди створено.");

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
            System.out.println("[INIT] Зброя додана.");

            System.out.println("--- [INIT] Початкове завантаження даних ЗАВЕРШЕНО ---");
        };
    }
}