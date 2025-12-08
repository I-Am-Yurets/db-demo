package dev.yurets.db_demo.controller.rest;

import dev.yurets.db_demo.model.Weapon;
import dev.yurets.db_demo.service.WeaponService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * REST API контролер для роботи зі зброєю
 *
 * Endpoints:
 * GET    /api/weapons       - Отримати всю зброю
 * GET    /api/weapons/{id}  - Отримати зброю за ID
 * POST   /api/weapons       - Створити новий запис зброї (тільки ADMIN)
 * PUT    /api/weapons/{id}  - Оновити зброю (тільки ADMIN)
 * DELETE /api/weapons/{id}  - Видалити зброю (тільки ADMIN)
 */
@RestController
@RequestMapping("/api/weapons")
public class WeaponRestController {

    private static final Logger log = LoggerFactory.getLogger(WeaponRestController.class);

    private final WeaponService weaponService;

    public WeaponRestController(WeaponService weaponService) {
        this.weaponService = weaponService;
    }

    /**
     * GET /api/weapons
     * Отримати список всієї зброї
     */
    @GetMapping
    public ResponseEntity<List<Weapon>> getAllWeapons() {
        log.info("[REST API] GET /api/weapons - Запит всієї зброї");
        List<Weapon> weapons = weaponService.getAllWeapons();
        return ResponseEntity.ok(weapons);
    }

    /**
     * GET /api/weapons/{id}
     * Отримати зброю за ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Weapon> getWeaponById(@PathVariable Long id) {
        log.info("[REST API] GET /api/weapons/{} - Запит зброї", id);

        return weaponService.getWeaponById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/weapons
     * Створити новий запис зброї
     * Доступ: тільки ADMIN
     *
     * Приклад JSON:
     * {
     *   "weaponType": "Artillery",
     *   "weaponName": "M777 Howitzer",
     *   "quantity": 90,
     *   "unitCostUsd": 2500000,
     *   "totalCostUsd": 225000000,
     *   "period": { "id": 1 }
     * }
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Weapon> createWeapon(@RequestBody Weapon weapon) {
        log.info("[REST API] POST /api/weapons - Створення зброї: {}", weapon.getWeaponName());

        try {
            weaponService.createWeapon(
                    weapon.getWeaponType(),
                    weapon.getWeaponName(),
                    weapon.getQuantity(),
                    weapon.getUnitCostUsd(),
                    weapon.getTotalCostUsd(),
                    weapon.getPeriod().getId()
            );

            // Повертаємо створену зброю
            Weapon created = weaponService.getAllWeapons().stream()
                    .filter(w -> w.getWeaponName().equals(weapon.getWeaponName()))
                    .findFirst()
                    .orElseThrow();

            return ResponseEntity
                    .created(URI.create("/api/weapons/" + created.getId()))
                    .body(created);

        } catch (IllegalArgumentException e) {
            log.error("[REST API] Помилка валідації: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /api/weapons/{id}
     * Оновити існуючу зброю
     * Доступ: тільки ADMIN
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Weapon> updateWeapon(
            @PathVariable Long id,
            @RequestBody Weapon weapon) {

        log.info("[REST API] PUT /api/weapons/{} - Оновлення зброї", id);

        try {
            weaponService.updateWeapon(
                    id,
                    weapon.getWeaponType(),
                    weapon.getWeaponName(),
                    weapon.getQuantity(),
                    weapon.getUnitCostUsd(),
                    weapon.getTotalCostUsd(),
                    weapon.getPeriod().getId()
            );

            return weaponService.getWeaponById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());

        } catch (IllegalArgumentException e) {
            log.error("[REST API] Помилка: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DELETE /api/weapons/{id}
     * Видалити зброю
     * Доступ: тільки ADMIN
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteWeapon(@PathVariable Long id) {
        log.info("[REST API] DELETE /api/weapons/{} - Видалення зброї", id);

        try {
            weaponService.deleteWeapon(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("[REST API] Помилка: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}