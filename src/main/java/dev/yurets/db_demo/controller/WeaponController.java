package dev.yurets.db_demo.controller;

import dev.yurets.db_demo.model.Period;
import dev.yurets.db_demo.model.Weapon;
import dev.yurets.db_demo.repository.PeriodRepository;
import dev.yurets.db_demo.repository.WeaponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * Контролер для роботи зі зброєю
 * Обробляє операції CREATE та DELETE для таблиці weapons
 */
@Controller
public class WeaponController {

    @Autowired
    private WeaponRepository weaponRepository;

    @Autowired
    private PeriodRepository periodRepository;

    /**
     * Додати нову зброю
     * POST /addWeapon
     */
    @PostMapping("/addWeapon")
    public String addWeapon(@RequestParam String weaponType,
                            @RequestParam String weaponName,
                            @RequestParam Integer quantity,
                            @RequestParam BigDecimal unitCostUsd,
                            @RequestParam BigDecimal totalCostUsd,
                            @RequestParam Long periodId) {

        // Знаходимо період, до якого належить зброя
        Period period = periodRepository.findById(periodId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Період з ID " + periodId + " не знайдено!"));

        Weapon weapon = new Weapon(weaponType, weaponName, quantity,
                unitCostUsd, totalCostUsd, period);
        weaponRepository.save(weapon);

        System.out.println("[INFO] Додано зброю: " + weaponName +
                " (тип: " + weaponType + ", кількість: " + quantity + ")");

        return "redirect:/";
    }

    /**
     * Видалити зброю за ID
     * GET /deleteWeapon/{id}
     */
    @GetMapping("/deleteWeapon/{id}")
    public String deleteWeapon(@PathVariable Long id) {
        weaponRepository.findById(id).ifPresent(weapon -> {
            System.out.println("[INFO] Видалення зброї: " + weapon.getWeaponName());
        });

        weaponRepository.deleteById(id);
        return "redirect:/";
    }
}