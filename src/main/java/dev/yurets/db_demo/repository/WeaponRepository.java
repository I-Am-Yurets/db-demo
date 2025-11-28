package dev.yurets.db_demo.repository;

import dev.yurets.db_demo.model.Weapon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Репозиторій для роботи зі зброєю
 */
public interface WeaponRepository extends JpaRepository<Weapon, Long> {

    List<Weapon> findAllByOrderByIdAsc();

    // Знайти всю зброю для конкретного періоду
    List<Weapon> findByPeriodId(Long periodId);
}