package dev.yurets.db_demo.repository;

import dev.yurets.db_demo.model.WeaponDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Репозиторій для роботи з поставками зброї
 */
public interface WeaponDeliveryRepository extends JpaRepository<WeaponDelivery, Long> {

    List<WeaponDelivery> findAllByOrderByDeliveryDateDesc();

    // Знайти всі поставки конкретної зброї
    List<WeaponDelivery> findByWeaponId(Long weaponId);

    // Знайти всі поставки конкретного донора
    List<WeaponDelivery> findByDonorId(Long donorId);

    // Знайти поставки за статусом
    List<WeaponDelivery> findByDeliveryStatus(String status);
}