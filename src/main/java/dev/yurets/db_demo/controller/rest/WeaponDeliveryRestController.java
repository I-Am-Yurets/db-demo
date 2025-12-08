package dev.yurets.db_demo.controller.rest;

import dev.yurets.db_demo.model.WeaponDelivery;
import dev.yurets.db_demo.service.WeaponDeliveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * REST API контролер для роботи з поставками зброї
 */
@RestController
@RequestMapping("/api/deliveries")
public class WeaponDeliveryRestController {

    private static final Logger log = LoggerFactory.getLogger(WeaponDeliveryRestController.class);

    private final WeaponDeliveryService deliveryService;

    public WeaponDeliveryRestController(WeaponDeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping
    public ResponseEntity<List<WeaponDelivery>> getAllDeliveries() {
        log.info("[REST API] GET /api/deliveries");
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WeaponDelivery> getDeliveryById(@PathVariable Long id) {
        log.info("[REST API] GET /api/deliveries/{}", id);
        return deliveryService.getDeliveryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/weapon/{weaponId}")
    public ResponseEntity<List<WeaponDelivery>> getDeliveriesByWeapon(@PathVariable Long weaponId) {
        log.info("[REST API] GET /api/deliveries/weapon/{}", weaponId);
        return ResponseEntity.ok(deliveryService.getDeliveriesByWeaponId(weaponId));
    }

    @GetMapping("/donor/{donorId}")
    public ResponseEntity<List<WeaponDelivery>> getDeliveriesByDonor(@PathVariable Long donorId) {
        log.info("[REST API] GET /api/deliveries/donor/{}", donorId);
        return ResponseEntity.ok(deliveryService.getDeliveriesByDonorId(donorId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<WeaponDelivery>> getDeliveriesByStatus(@PathVariable String status) {
        log.info("[REST API] GET /api/deliveries/status/{}", status);
        return ResponseEntity.ok(deliveryService.getDeliveriesByStatus(status));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<WeaponDelivery> createDelivery(@RequestBody WeaponDelivery delivery) {
        log.info("[REST API] POST /api/deliveries");

        try {
            deliveryService.createDelivery(
                    delivery.getDeliveryDate(),
                    delivery.getQuantityDelivered(),
                    delivery.getDeliveryStatus(),
                    delivery.getTrackingNumber(),
                    delivery.getWeapon().getId(),
                    delivery.getDonor().getId()
            );

            WeaponDelivery created = deliveryService.getAllDeliveries().stream()
                    .findFirst()
                    .orElseThrow();

            return ResponseEntity
                    .created(URI.create("/api/deliveries/" + created.getId()))
                    .body(created);
        } catch (IllegalArgumentException e) {
            log.error("[REST API] Помилка: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<WeaponDelivery> updateDelivery(
            @PathVariable Long id, @RequestBody WeaponDelivery delivery) {
        log.info("[REST API] PUT /api/deliveries/{}", id);

        try {
            deliveryService.updateDelivery(
                    id,
                    delivery.getDeliveryDate(),
                    delivery.getQuantityDelivered(),
                    delivery.getDeliveryStatus(),
                    delivery.getTrackingNumber(),
                    delivery.getWeapon().getId(),
                    delivery.getDonor().getId()
            );

            return deliveryService.getDeliveryById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            log.error("[REST API] Помилка: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteDelivery(@PathVariable Long id) {
        log.info("[REST API] DELETE /api/deliveries/{}", id);

        try {
            deliveryService.deleteDelivery(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("[REST API] Помилка: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}