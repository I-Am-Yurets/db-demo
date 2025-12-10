package dev.yurets.db_demo.controller.rest;

import dev.yurets.db_demo.model.Country;
import dev.yurets.db_demo.service.CountryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * REST API контролер для роботи з країнами
 *
 * Endpoints:
 * GET    /api/countries       - Отримати всі країни
 * GET    /api/countries/{id}  - Отримати країну за ID
 * POST   /api/countries       - Створити нову країну (тільки ADMIN)
 * PUT    /api/countries/{id}  - Оновити країну (тільки ADMIN)
 * DELETE /api/countries/{id}  - Видалити країну (тільки ADMIN)
 */
@Slf4j
@RestController
@RequestMapping("/api/countries")
public class CountryRestController {

    private final CountryService countryService;

    public CountryRestController(CountryService countryService) {
        this.countryService = countryService;
    }

    /**
     * GET /api/countries
     * Отримати список всіх країн
     * Доступ: USER, ADMIN
     */
    @GetMapping
    public ResponseEntity<List<Country>> getAllCountries() {
        log.info("[REST API] GET /api/countries - Запит всіх країн");
        List<Country> countries = countryService.getAllCountries();
        return ResponseEntity.ok(countries);
    }

    /**
     * GET /api/countries/{id}
     * Отримати країну за ID
     * Доступ: USER, ADMIN
     */
    @GetMapping("/{id}")
    public ResponseEntity<Country> getCountryById(@PathVariable Long id) {
        log.info("[REST API] GET /api/countries/{} - Запит країни", id);

        return countryService.getCountryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/countries
     * Створити нову країну
     * Доступ: тільки ADMIN
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Country> createCountry(@RequestBody Country country) {
        log.info("[REST API] POST /api/countries - Створення країни: {}", country.getName());

        try {
            countryService.createCountry(country.getName(), country.getTotalAidUsd());

            // Повертаємо створену країну
            Country created = countryService.getAllCountries().stream()
                    .filter(c -> c.getName().equals(country.getName()))
                    .findFirst()
                    .orElseThrow();

            return ResponseEntity
                    .created(URI.create("/api/countries/" + created.getId()))
                    .body(created);

        } catch (IllegalArgumentException e) {
            log.error("[REST API] Помилка валідації: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /api/countries/{id}
     * Оновити існуючу країну
     * Доступ: тільки ADMIN
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Country> updateCountry(
            @PathVariable Long id,
            @RequestBody Country country) {

        log.info("[REST API] PUT /api/countries/{} - Оновлення країни", id);

        try {
            countryService.updateCountry(id, country.getName(), country.getTotalAidUsd());

            return countryService.getCountryById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());

        } catch (IllegalArgumentException e) {
            log.error("[REST API] Помилка: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DELETE /api/countries/{id}
     * Видалити країну
     * Доступ: тільки ADMIN
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteCountry(@PathVariable Long id) {
        log.info("[REST API] DELETE /api/countries/{} - Видалення країни", id);

        try {
            countryService.deleteCountry(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("[REST API] Помилка: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}