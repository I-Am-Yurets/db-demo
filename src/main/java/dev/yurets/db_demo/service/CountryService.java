package dev.yurets.db_demo.service;

import dev.yurets.db_demo.model.Country;
import dev.yurets.db_demo.repository.CountryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Сервіс для роботи з країнами-донорами
 * Прошарок між контролерами та репозиторієм
 */
@Slf4j
@Service
@Transactional
public class CountryService {

    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    /**
     * Отримати всі країни відсортовані за ID
     */
    public List<Country> getAllCountries() {
        return countryRepository.findAllByOrderByIdAsc();
    }

    /**
     * Знайти країну за ID
     */
    public Optional<Country> getCountryById(Long id) {
        return countryRepository.findById(id);
    }

    /**
     * Створити нову країну з валідацією
     */
    public void createCountry(String name, BigDecimal totalAidUsd, Boolean isOpen) {  // ⬅️ ДОДАЛИ isOpen
        // Валідація назви
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Назва країни не може бути порожньою");
        }
        if (name.trim().length() < 2) {
            throw new IllegalArgumentException("Назва країни має містити мінімум 2 символи");
        }
        if (name.trim().length() > 100) {
            throw new IllegalArgumentException("Назва країни занадто довга (максимум 100 символів)");
        }

        // Валідація суми
        if (totalAidUsd == null) {
            throw new IllegalArgumentException("Сума допомоги обов'язкова");
        }
        if (totalAidUsd.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сума допомоги має бути додатною");
        }
        if (totalAidUsd.compareTo(new BigDecimal("999999999999999")) > 0) {
            throw new IllegalArgumentException("Сума допомоги занадто велика");
        }

        // Створення країни
        Country country = new Country(name.trim(), totalAidUsd);
        country.setOpen(isOpen != null ? isOpen : true);  // ⬅️ ВСТАНОВЛЮЄМО СТАТУС
        Country saved = countryRepository.save(country);

        log.info("Створено країну: {} (ID: {}, Статус: {})",
                saved.getName(), saved.getId(), saved.isOpen() ? "відкрита" : "зачинена");
    }

    /**
     * Оновити існуючу країну з валідацією
     */
    public void updateCountry(Long id, String name, BigDecimal totalAidUsd, Boolean isOpen) {  // ⬅️ ДОДАЛИ isOpen
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Країну з ID " + id + " не знайдено!"));

        // Валідація назви
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Назва країни не може бути порожньою");
        }
        if (name.trim().length() < 2) {
            throw new IllegalArgumentException("Назва країни має містити мінімум 2 символи");
        }
        if (name.trim().length() > 100) {
            throw new IllegalArgumentException("Назва країни занадто довга (максимум 100 символів)");
        }

        // Валідація суми
        if (totalAidUsd == null) {
            throw new IllegalArgumentException("Сума допомоги обов'язкова");
        }
        if (totalAidUsd.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сума допомоги має бути додатною");
        }
        if (totalAidUsd.compareTo(new BigDecimal("999999999999999")) > 0) {
            throw new IllegalArgumentException("Сума допомоги занадто велика");
        }

        // Оновлення полів
        country.setName(name.trim());
        country.setTotalAidUsd(totalAidUsd);
        country.setOpen(isOpen != null ? isOpen : true);  // ⬅️ ОНОВЛЮЄМО СТАТУС

        Country updated = countryRepository.save(country);

        log.info("Оновлено країну: {} (ID: {}, Статус: {})",
                updated.getName(), updated.getId(), updated.isOpen() ? "відкрита" : "зачинена");
    }

    /**
     * Видалити країну за ID
     * Каскадно видалить всі пов'язані періоди та зброю
     */
    public void deleteCountry(Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Країну з ID " + id + " не знайдено!"));

        log.info("Видалення країни: {} (ID: {})", country.getName(), country.getId());

        countryRepository.deleteById(id);
    }

    /**
     * Перевірити чи існує країна з такою назвою
     */
    public boolean existsByName(String name) {
        return countryRepository.findByName(name) != null;
    }
}