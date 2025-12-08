package dev.yurets.db_demo.controller.rest;

import dev.yurets.db_demo.model.Donor;
import dev.yurets.db_demo.service.DonorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * REST API контролер для роботи з донорами
 */
@RestController
@RequestMapping("/api/donors")
public class DonorRestController {

    private static final Logger log = LoggerFactory.getLogger(DonorRestController.class);

    private final DonorService donorService;

    public DonorRestController(DonorService donorService) {
        this.donorService = donorService;
    }

    @GetMapping
    public ResponseEntity<List<Donor>> getAllDonors() {
        log.info("[REST API] GET /api/donors");
        return ResponseEntity.ok(donorService.getAllDonors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Donor> getDonorById(@PathVariable Long id) {
        log.info("[REST API] GET /api/donors/{}", id);
        return donorService.getDonorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Donor> createDonor(@RequestBody Donor donor) {
        log.info("[REST API] POST /api/donors - {}", donor.getOrganizationName());

        try {
            donorService.createDonor(
                    donor.getOrganizationName(),
                    donor.getOrganizationType(),
                    donor.getContactInfo(),
                    donor.getCountry().getId()
            );

            Donor created = donorService.getAllDonors().stream()
                    .filter(d -> d.getOrganizationName().equals(donor.getOrganizationName()))
                    .findFirst()
                    .orElseThrow();

            return ResponseEntity
                    .created(URI.create("/api/donors/" + created.getId()))
                    .body(created);
        } catch (IllegalArgumentException e) {
            log.error("[REST API] Помилка: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Donor> updateDonor(@PathVariable Long id, @RequestBody Donor donor) {
        log.info("[REST API] PUT /api/donors/{}", id);

        try {
            donorService.updateDonor(
                    id,
                    donor.getOrganizationName(),
                    donor.getOrganizationType(),
                    donor.getContactInfo(),
                    donor.getCountry().getId()
            );

            return donorService.getDonorById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            log.error("[REST API] Помилка: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteDonor(@PathVariable Long id) {
        log.info("[REST API] DELETE /api/donors/{}", id);

        try {
            donorService.deleteDonor(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("[REST API] Помилка: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}