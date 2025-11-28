package dev.yurets.db_demo.service;

import dev.yurets.db_demo.model.Period;
import dev.yurets.db_demo.model.Weapon;
import dev.yurets.db_demo.repository.PeriodRepository;
import dev.yurets.db_demo.repository.WeaponRepository;
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

    private final WeaponRepository weaponRepository;
    private final PeriodRepository periodRepository;

    // Dependency Injection через конструктор
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
     * Створити нову зброю
     */
    public Weapon createWeapon(String weaponType, String weaponName,
                               Integer quantity, BigDecimal unitCostUsd,
                               BigDecimal totalCostUsd, Long periodId) {
        Period period = periodRepository.findById(periodId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Період з ID " + periodId + " не знайдено!"));

        Weapon weapon = new Weapon(weaponType, weaponName, quantity,
                unitCostUsd, totalCostUsd, period);
        Weapon saved = weaponRepository.save(weapon);

        System.out.println("[SERVICE] Створено зброю: " + saved.getWeaponName() +
                " (тип: " + saved.getWeaponType() +
                ", кількість: " + saved.getQuantity() +
                ", ID: " + saved.getId() + ")");

        return saved;
    }

    /**
     * Оновити існуючу зброю
     */
    public Weapon updateWeapon(Long id, String weaponType, String weaponName,
                               Integer quantity, BigDecimal unitCostUsd,
                               BigDecimal totalCostUsd, Long periodId) {
        Weapon weapon = weaponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Зброю з ID " + id + " не знайдено!"));

        Period period = periodRepository.findById(periodId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Період з ID " + periodId + " не знайдено!"));

        weapon.setWeaponType(weaponType);
        weapon.setWeaponName(weaponName);
        weapon.setQuantity(quantity);
        weapon.setUnitCostUsd(unitCostUsd);
        weapon.setTotalCostUsd(totalCostUsd);
        weapon.setPeriod(period);

        Weapon updated = weaponRepository.save(weapon);

        System.out.println("[SERVICE] Оновлено зброю: " + updated.getWeaponName() +
                " (ID: " + updated.getId() + ")");

        return updated;
    }

    /**
     * Видалити зброю за ID
     */
    public void deleteWeapon(Long id) {
        Weapon weapon = weaponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Зброю з ID " + id + " не знайдено!"));

        System.out.println("[SERVICE] Видалення зброї: " + weapon.getWeaponName() +
                " (ID: " + weapon.getId() + ")");

        weaponRepository.deleteById(id);
    }
}