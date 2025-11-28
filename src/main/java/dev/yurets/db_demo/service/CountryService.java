package dev.yurets.db_demo.service;

import dev.yurets.db_demo.model.Country;
import dev.yurets.db_demo.repository.CountryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Сервіс для роботи з країнами-донорами
 * Прошарок між контролерами та репозиторієм
 */
@Service
@Transactional
public class CountryService {

    private final CountryRepository countryRepository;

    // Dependency Injection через конструктор
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
     * Створити нову країну
     */
    public Country createCountry(String name, BigDecimal totalAidUsd) {
        Country country = new Country(name, totalAidUsd);
        Country saved = countryRepository.save(country);

        System.out.println("[SERVICE] Створено країну: " + saved.getName() +
                " (ID: " + saved.getId() + ")");

        return saved;
    }

    /**
     * Оновити існуючу країну
     */
    public Country updateCountry(Long id, String name, BigDecimal totalAidUsd) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Країну з ID " + id + " не знайдено!"));

        country.setName(name);
        country.setTotalAidUsd(totalAidUsd);

        Country updated = countryRepository.save(country);

        System.out.println("[SERVICE] Оновлено країну: " + updated.getName() +
                " (ID: " + updated.getId() + ")");

        return updated;
    }

    /**
     * Видалити країну за ID
     * Каскадно видалить всі пов'язані періоди та зброю
     */
    public void deleteCountry(Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Країну з ID " + id + " не знайдено!"));

        System.out.println("[SERVICE] Видалення країни: " + country.getName() +
                " (ID: " + country.getId() + ")");

        countryRepository.deleteById(id);
    }

    /**
     * Перевірити чи існує країна з такою назвою
     */
    public boolean existsByName(String name) {
        return countryRepository.findByName(name) != null;
    }
}