package dev.yurets.db_demo.controller.rest;

import dev.yurets.db_demo.model.Period;
import dev.yurets.db_demo.service.PeriodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * REST API контролер для роботи з періодами
 *
 * Endpoints:
 * GET    /api/periods       - Отримати всі періоди
 * GET    /api/periods/{id}  - Отримати період за ID
 * POST   /api/periods       - Створити новий період (тільки ADMIN)
 * PUT    /api/periods/{id}  - Оновити період (тільки ADMIN)
 * DELETE /api/periods/{id}  - Видалити період (тільки ADMIN)
 */
@Slf4j
@RestController
@RequestMapping("/api/periods")
public class PeriodRestController {

    private final PeriodService periodService;

    public PeriodRestController(PeriodService periodService) {
        this.periodService = periodService;
    }

    /**
     * GET /api/periods
     * Отримати список всіх періодів
     */
    @GetMapping
    public ResponseEntity<List<Period>> getAllPeriods() {
        log.info("[REST API] GET /api/periods - Запит всіх періодів");
        List<Period> periods = periodService.getAllPeriods();
        return ResponseEntity.ok(periods);
    }

    /**
     * GET /api/periods/{id}
     * Отримати період за ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Period> getPeriodById(@PathVariable Long id) {
        log.info("[REST API] GET /api/periods/{} - Запит періоду", id);

        return periodService.getPeriodById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/periods
     * Створити новий період
     * Доступ: тільки ADMIN
     *
     * Приклад JSON:
     * {
     *   "periodName": "2024 Q1",
     *   "startDate": "2024-01-01",
     *   "endDate": "2024-03-31",
     *   "aidAmountUsd": 5000000000,
     *   "country": { "id": 1 }
     * }
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Period> createPeriod(@RequestBody Period period) {
        log.info("[REST API] POST /api/periods - Створення періоду: {}", period.getPeriodName());

        try {
            periodService.createPeriod(
                    period.getPeriodName(),
                    period.getStartDate(),
                    period.getEndDate(),
                    period.getAidAmountUsd(),
                    period.getCountry().getId()
            );

            // Повертаємо створений період
            Period created = periodService.getAllPeriods().stream()
                    .filter(p -> p.getPeriodName().equals(period.getPeriodName()))
                    .findFirst()
                    .orElseThrow();

            return ResponseEntity
                    .created(URI.create("/api/periods/" + created.getId()))
                    .body(created);

        } catch (IllegalArgumentException e) {
            log.error("[REST API] Помилка валідації: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /api/periods/{id}
     * Оновити існуючий період
     * Доступ: тільки ADMIN
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Period> updatePeriod(
            @PathVariable Long id,
            @RequestBody Period period) {

        log.info("[REST API] PUT /api/periods/{} - Оновлення періоду", id);

        try {
            periodService.updatePeriod(
                    id,
                    period.getPeriodName(),
                    period.getStartDate(),
                    period.getEndDate(),
                    period.getAidAmountUsd(),
                    period.getCountry().getId()
            );

            return periodService.getPeriodById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());

        } catch (IllegalArgumentException e) {
            log.error("[REST API] Помилка: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DELETE /api/periods/{id}
     * Видалити період
     * Доступ: тільки ADMIN
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deletePeriod(@PathVariable Long id) {
        log.info("[REST API] DELETE /api/periods/{} - Видалення періоду", id);

        try {
            periodService.deletePeriod(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("[REST API] Помилка: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}