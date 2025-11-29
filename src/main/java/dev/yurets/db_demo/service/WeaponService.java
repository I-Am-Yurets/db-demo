package dev.yurets.db_demo.service;

import dev.yurets.db_demo.model.Period;
import dev.yurets.db_demo.model.Weapon;
import dev.yurets.db_demo.repository.PeriodRepository;
import dev.yurets.db_demo.repository.WeaponRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Сервіс для роботи зі зброєю
 * Прошарок між контролерами та репозиторієм
 */
@Service
@Transactional
public class WeaponService {

    private static final Logger log = LoggerFactory.getLogger(WeaponService.class);

    private final WeaponRepository weaponRepository;
    private final PeriodRepository periodRepository;

    public WeaponService(WeaponRepository weaponRepository,
                         PeriodRepository periodRepository) {
        this.weaponRepository = weaponRepository;
        this.periodRepository = periodRepository;
    }

    /**
     * Отримати всю зброю відсортовану за ID
     */
    public List<Weapon> getAllWeapons() {
        return weaponRepository.findAllByOrderByIdAsc();
    }

    /**
     * Знайти зброю за ID
     */
    public Optional<Weapon> getWeaponById(Long id) {
        return weaponRepository.findById(id);
    }

    /**
     * Отримати всю зброю для конкретного періоду
     */
    public List<Weapon> getWeaponsByPeriodId(Long periodId) {
        return weaponRepository.findByPeriodId(periodId);
    }

    /**
     * Створити нову зброю з валідацією
     */
    public void createWeapon(String weaponType, String weaponName,
                             Integer quantity, BigDecimal unitCostUsd,
                             BigDecimal totalCostUsd, Long periodId) {
        // Валідація типу зброї
        if (weaponType == null || weaponType.trim().isEmpty()) {
            throw new IllegalArgumentException("Тип зброї не може бути порожнім");
        }
        if (weaponType.trim().length() < 2) {
            throw new IllegalArgumentException("Тип зброї має містити мінімум 2 символи");
        }
        if (weaponType.trim().length() > 100) {
            throw new IllegalArgumentException("Тип зброї занадто довгий (максимум 100 символів)");
        }

        // Валідація назви зброї
        if (weaponName == null || weaponName.trim().isEmpty()) {
            throw new IllegalArgumentException("Назва зброї не може бути порожньою");
        }
        if (weaponName.trim().length() < 2) {
            throw new IllegalArgumentException("Назва зброї має містити мінімум 2 символи");
        }
        if (weaponName.trim().length() > 200) {
            throw new IllegalArgumentException("Назва зброї занадто довга (максимум 200 символів)");
        }

        // Валідація кількості
        if (quantity == null) {
            throw new IllegalArgumentException("Кількість обов'язкова");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Кількість має бути додатною");
        }
        if (quantity > 1000000) {
            throw new IllegalArgumentException("Кількість занадто велика (максимум 1,000,000)");
        }

        // Валідація вартості одиниці
        if (unitCostUsd == null) {
            throw new IllegalArgumentException("Вартість одиниці обов'язкова");
        }
        if (unitCostUsd.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Вартість одиниці має бути додатною");
        }
        if (unitCostUsd.compareTo(new BigDecimal("999999999999")) > 0) {
            throw new IllegalArgumentException("Вартість одиниці занадто велика");
        }

        // Валідація загальної вартості
        if (totalCostUsd == null) {
            throw new IllegalArgumentException("Загальна вартість обов'язкова");
        }
        if (totalCostUsd.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Загальна вартість має бути додатною");
        }
        if (totalCostUsd.compareTo(new BigDecimal("999999999999999")) > 0) {
            throw new IllegalArgumentException("Загальна вартість занадто велика");
        }

        // Валідація періоду
        if (periodId == null) {
            throw new IllegalArgumentException("Період обов'язковий");
        }
        Period period = periodRepository.findById(periodId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Період з ID " + periodId + " не знайдено!"));

        Weapon weapon = new Weapon(weaponType.trim(), weaponName.trim(), quantity,
                unitCostUsd, totalCostUsd, period);
        Weapon saved = weaponRepository.save(weapon);

        log.info("Створено зброю: {} (тип: {}, кількість: {}, ID: {})",
                saved.getWeaponName(), saved.getWeaponType(), saved.getQuantity(), saved.getId());

    }

    /**
     * Оновити існуючу зброю з валідацією
     */
    public void updateWeapon(Long id, String weaponType, String weaponName,
                             Integer quantity, BigDecimal unitCostUsd,
                             BigDecimal totalCostUsd, Long periodId) {
        Weapon weapon = weaponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Зброю з ID " + id + " не знайдено!"));

        // Валідація типу зброї
        if (weaponType == null || weaponType.trim().isEmpty()) {
            throw new IllegalArgumentException("Тип зброї не може бути порожнім");
        }
        if (weaponType.trim().length() < 2) {
            throw new IllegalArgumentException("Тип зброї має містити мінімум 2 символи");
        }
        if (weaponType.trim().length() > 100) {
            throw new IllegalArgumentException("Тип зброї занадто довгий (максимум 100 символів)");
        }

        // Валідація назви зброї
        if (weaponName == null || weaponName.trim().isEmpty()) {
            throw new IllegalArgumentException("Назва зброї не може бути порожньою");
        }
        if (weaponName.trim().length() < 2) {
            throw new IllegalArgumentException("Назва зброї має містити мінімум 2 символи");
        }
        if (weaponName.trim().length() > 200) {
            throw new IllegalArgumentException("Назва зброї занадто довга (максимум 200 символів)");
        }

        // Валідація кількості
        if (quantity == null) {
            throw new IllegalArgumentException("Кількість обов'язкова");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Кількість має бути додатною");
        }
        if (quantity > 1000000) {
            throw new IllegalArgumentException("Кількість занадто велика (максимум 1,000,000)");
        }

        // Валідація вартості одиниці
        if (unitCostUsd == null) {
            throw new IllegalArgumentException("Вартість одиниці обов'язкова");
        }
        if (unitCostUsd.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Вартість одиниці має бути додатною");
        }
        if (unitCostUsd.compareTo(new BigDecimal("999999999999")) > 0) {
            throw new IllegalArgumentException("Вартість одиниці занадто велика");
        }

        // Валідація загальної вартості
        if (totalCostUsd == null) {
            throw new IllegalArgumentException("Загальна вартість обов'язкова");
        }
        if (totalCostUsd.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Загальна вартість має бути додатною");
        }
        if (totalCostUsd.compareTo(new BigDecimal("999999999999999")) > 0) {
            throw new IllegalArgumentException("Загальна вартість занадто велика");
        }

        // Валідація періоду
        if (periodId == null) {
            throw new IllegalArgumentException("Період обов'язковий");
        }
        Period period = periodRepository.findById(periodId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Період з ID " + periodId + " не знайдено!"));

        weapon.setWeaponType(weaponType.trim());
        weapon.setWeaponName(weaponName.trim());
        weapon.setQuantity(quantity);
        weapon.setUnitCostUsd(unitCostUsd);
        weapon.setTotalCostUsd(totalCostUsd);
        weapon.setPeriod(period);

        Weapon updated = weaponRepository.save(weapon);

        log.info("Оновлено зброю: {} (ID: {})", updated.getWeaponName(), updated.getId());

    }

    /**
     * Видалити зброю за ID
     */
    public void deleteWeapon(Long id) {
        Weapon weapon = weaponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Зброю з ID " + id + " не знайдено!"));

        log.info("Видалення зброї: {} (ID: {})", weapon.getWeaponName(), weapon.getId());

        weaponRepository.deleteById(id);
    }
}