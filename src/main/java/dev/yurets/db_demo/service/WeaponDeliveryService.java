package dev.yurets.db_demo.service;

import dev.yurets.db_demo.model.Donor;
import dev.yurets.db_demo.model.Weapon;
import dev.yurets.db_demo.model.WeaponDelivery;
import dev.yurets.db_demo.repository.DonorRepository;
import dev.yurets.db_demo.repository.WeaponDeliveryRepository;
import dev.yurets.db_demo.repository.WeaponRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Сервіс для роботи з поставками зброї
 */
@Service
@Transactional
public class WeaponDeliveryService {

    private static final Logger log = LoggerFactory.getLogger(WeaponDeliveryService.class);

    private final WeaponDeliveryRepository deliveryRepository;
    private final WeaponRepository weaponRepository;
    private final DonorRepository donorRepository;

    public WeaponDeliveryService(WeaponDeliveryRepository deliveryRepository,
                                 WeaponRepository weaponRepository,
                                 DonorRepository donorRepository) {
        this.deliveryRepository = deliveryRepository;
        this.weaponRepository = weaponRepository;
        this.donorRepository = donorRepository;
    }

    public List<WeaponDelivery> getAllDeliveries() {
        return deliveryRepository.findAllByOrderByDeliveryDateDesc();
    }

    public Optional<WeaponDelivery> getDeliveryById(Long id) {
        return deliveryRepository.findById(id);
    }

    public List<WeaponDelivery> getDeliveriesByWeaponId(Long weaponId) {
        return deliveryRepository.findByWeaponId(weaponId);
    }

    public List<WeaponDelivery> getDeliveriesByDonorId(Long donorId) {
        return deliveryRepository.findByDonorId(donorId);
    }

    public List<WeaponDelivery> getDeliveriesByStatus(String status) {
        return deliveryRepository.findByDeliveryStatus(status);
    }

    public void createDelivery(LocalDate deliveryDate, Integer quantityDelivered,
                               String deliveryStatus, String trackingNumber,
                               Long weaponId, Long donorId) {
        // Валідація
        if (deliveryDate == null) {
            throw new IllegalArgumentException("Дата поставки обов'язкова");
        }
        if (quantityDelivered == null || quantityDelivered <= 0) {
            throw new IllegalArgumentException("Кількість має бути додатною");
        }
        if (deliveryStatus == null || deliveryStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("Статус поставки обов'язковий");
        }
        if (!isValidStatus(deliveryStatus)) {
            throw new IllegalArgumentException(
                    "Невірний статус. Дозволені: planned, in_transit, delivered");
        }
        if (weaponId == null) {
            throw new IllegalArgumentException("Зброя обов'язкова");
        }
        if (donorId == null) {
            throw new IllegalArgumentException("Донор обов'язковий");
        }

        Weapon weapon = weaponRepository.findById(weaponId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Зброю з ID " + weaponId + " не знайдено!"));

        Donor donor = donorRepository.findById(donorId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Донора з ID " + donorId + " не знайдено!"));

        WeaponDelivery delivery = new WeaponDelivery(
                deliveryDate, quantityDelivered, deliveryStatus.trim(),
                trackingNumber, weapon, donor
        );

        WeaponDelivery saved = deliveryRepository.save(delivery);
        log.info("Створено поставку: {} од. {} (статус: {}, ID: {})",
                saved.getQuantityDelivered(), weapon.getWeaponName(),
                saved.getDeliveryStatus(), saved.getId());
    }

    public void updateDelivery(Long id, LocalDate deliveryDate, Integer quantityDelivered,
                               String deliveryStatus, String trackingNumber,
                               Long weaponId, Long donorId) {
        WeaponDelivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Поставку з ID " + id + " не знайдено!"));

        // Валідація (аналогічно create)
        if (deliveryDate == null) {
            throw new IllegalArgumentException("Дата поставки обов'язкова");
        }
        if (quantityDelivered == null || quantityDelivered <= 0) {
            throw new IllegalArgumentException("Кількість має бути додатною");
        }
        if (deliveryStatus == null || deliveryStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("Статус поставки обов'язковий");
        }
        if (!isValidStatus(deliveryStatus)) {
            throw new IllegalArgumentException(
                    "Невірний статус. Дозволені: planned, in_transit, delivered");
        }

        Weapon weapon = weaponRepository.findById(weaponId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Зброю з ID " + weaponId + " не знайдено!"));

        Donor donor = donorRepository.findById(donorId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Донора з ID " + donorId + " не знайдено!"));

        delivery.setDeliveryDate(deliveryDate);
        delivery.setQuantityDelivered(quantityDelivered);
        delivery.setDeliveryStatus(deliveryStatus.trim());
        delivery.setTrackingNumber(trackingNumber);
        delivery.setWeapon(weapon);
        delivery.setDonor(donor);

        WeaponDelivery updated = deliveryRepository.save(delivery);
        log.info("Оновлено поставку (ID: {})", updated.getId());
    }

    public void deleteDelivery(Long id) {
        WeaponDelivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Поставку з ID " + id + " не знайдено!"));

        log.info("Видалення поставки (ID: {})", delivery.getId());
        deliveryRepository.deleteById(id);
    }

    private boolean isValidStatus(String status) {
        return status.equals("planned") ||
                status.equals("in_transit") ||
                status.equals("delivered");
    }
}