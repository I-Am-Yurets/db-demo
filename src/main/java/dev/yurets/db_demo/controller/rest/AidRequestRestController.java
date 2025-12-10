package dev.yurets.db_demo.controller.rest;

import dev.yurets.db_demo.model.AidRequest;
import dev.yurets.db_demo.service.AidRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * REST API контролер для роботи із запитами на допомогу
 *
 * Endpoints:
 * GET    /api/requests              - Отримати всі запити
 * GET    /api/requests/{id}         - Отримати запит за ID
 * GET    /api/requests/status/{s}   - Отримати запити за статусом
 * GET    /api/requests/priority/{p} - Отримати запити за пріоритетом
 * POST   /api/requests              - Створити новий запит (тільки ADMIN)
 * PUT    /api/requests/{id}         - Оновити запит (тільки ADMIN)
 * PUT    /api/requests/{id}/approve - Схвалити запит (тільки ADMIN)
 * PUT    /api/requests/{id}/reject  - Відхилити запит (тільки ADMIN)
 * DELETE /api/requests/{id}         - Видалити запит (тільки ADMIN)
 */
@Slf4j
@RestController
@RequestMapping("/api/requests")
public class AidRequestRestController {

    private final AidRequestService requestService;

    public AidRequestRestController(AidRequestService requestService) {
        this.requestService = requestService;
    }

    /**
     * GET /api/requests
     * Отримати список всіх запитів
     * Доступ: USER, ADMIN
     */
    @GetMapping
    public ResponseEntity<List<AidRequest>> getAllRequests() {
        log.info("[REST API] GET /api/requests - Запит всіх запитів на допомогу");
        List<AidRequest> requests = requestService.getAllRequests();
        return ResponseEntity.ok(requests);
    }

    /**
     * GET /api/requests/{id}
     * Отримати запит за ID
     * Доступ: USER, ADMIN
     */
    @GetMapping("/{id}")
    public ResponseEntity<AidRequest> getRequestById(@PathVariable Long id) {
        log.info("[REST API] GET /api/requests/{} - Запит на допомогу", id);

        return requestService.getRequestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/requests/status/{status}
     * Отримати запити за статусом (PENDING, APPROVED, REJECTED, DELIVERED)
     * Доступ: USER, ADMIN
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AidRequest>> getRequestsByStatus(@PathVariable String status) {
        log.info("[REST API] GET /api/requests/status/{}", status);
        List<AidRequest> requests = requestService.getRequestsByStatus(status);
        return ResponseEntity.ok(requests);
    }

    /**
     * GET /api/requests/priority/{priority}
     * Отримати запити за пріоритетом (URGENT, HIGH, MEDIUM, LOW)
     * Доступ: USER, ADMIN
     */
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<AidRequest>> getRequestsByPriority(@PathVariable String priority) {
        log.info("[REST API] GET /api/requests/priority/{}", priority);
        List<AidRequest> requests = requestService.getRequestsByPriority(priority);
        return ResponseEntity.ok(requests);
    }

    /**
     * POST /api/requests
     * Створити новий запит на допомогу
     * Доступ: тільки ADMIN
     *
     * Приклад JSON:
     * {
     *   "weaponType": "Artillery",
     *   "weaponName": "M777 Howitzer",
     *   "requestedQuantity": 100,
     *   "priority": "URGENT",
     *   "requestReason": "Defense needs",
     *   "requestingCountry": { "id": 1 },
     *   "donorCountry": { "id": 2 },
     *   "period": { "id": 1 }
     * }
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AidRequest> createRequest(@RequestBody AidRequest request) {
        log.info("[REST API] POST /api/requests - Створення запиту: {}", request.getWeaponName());

        try {
            requestService.createRequest(
                    request.getWeaponType(),
                    request.getWeaponName(),
                    request.getRequestedQuantity(),
                    request.getPriority(),
                    request.getRequestReason(),
                    request.getRequestingCountry().getId(),
                    request.getDonorCountry() != null ? request.getDonorCountry().getId() : null,
                    request.getPeriod().getId()
            );

            // Повертаємо створений запит
            AidRequest created = requestService.getAllRequests().stream()
                    .filter(r -> r.getWeaponName().equals(request.getWeaponName()))
                    .findFirst()
                    .orElseThrow();

            return ResponseEntity
                    .created(URI.create("/api/requests/" + created.getId()))
                    .body(created);

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("[REST API] Помилка валідації: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /api/requests/{id}
     * Оновити існуючий запит
     * Доступ: тільки ADMIN
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AidRequest> updateRequest(
            @PathVariable Long id,
            @RequestBody AidRequest request) {

        log.info("[REST API] PUT /api/requests/{} - Оновлення запиту", id);

        try {
            requestService.updateRequest(
                    id,
                    request.getWeaponType(),
                    request.getWeaponName(),
                    request.getRequestedQuantity(),
                    request.getPriority(),
                    request.getRequestReason(),
                    request.getRequestingCountry().getId(),
                    request.getDonorCountry() != null ? request.getDonorCountry().getId() : null,
                    request.getPeriod().getId()
            );

            return requestService.getRequestById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("[REST API] Помилка: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /api/requests/{id}/approve
     * Схвалити запит
     * Доступ: тільки ADMIN
     */
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AidRequest> approveRequest(@PathVariable Long id) {
        log.info("[REST API] PUT /api/requests/{}/approve - Схвалення запиту", id);

        try {
            requestService.approveRequest(id);

            return requestService.getRequestById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("[REST API] Помилка: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /api/requests/{id}/reject
     * Відхилити запит
     * Доступ: тільки ADMIN
     *
     * Приклад JSON:
     * {
     *   "rejectionReason": "Insufficient resources"
     * }
     */
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AidRequest> rejectRequest(
            @PathVariable Long id,
            @RequestBody RejectRequestDto dto) {

        log.info("[REST API] PUT /api/requests/{}/reject - Відхилення запиту", id);

        try {
            requestService.rejectRequest(id, dto.getRejectionReason());

            return requestService.getRequestById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("[REST API] Помилка: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DELETE /api/requests/{id}
     * Видалити запит
     * Доступ: тільки ADMIN
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        log.info("[REST API] DELETE /api/requests/{} - Видалення запиту", id);

        try {
            requestService.deleteRequest(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("[REST API] Помилка: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DTO для відхилення запиту
     */
    public static class RejectRequestDto {
        private String rejectionReason;

        public String getRejectionReason() {
            return rejectionReason;
        }

        public void setRejectionReason(String rejectionReason) {
            this.rejectionReason = rejectionReason;
        }
    }
}