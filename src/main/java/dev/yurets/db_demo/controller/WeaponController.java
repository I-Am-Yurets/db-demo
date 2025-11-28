package dev.yurets.db_demo.controller;

import dev.yurets.db_demo.model.Weapon;
import dev.yurets.db_demo.repository.PeriodRepository;
import dev.yurets.db_demo.service.WeaponService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * Контролер для роботи зі зброєю
 * Обробляє всі CRUD операції: CREATE, READ, UPDATE, DELETE
 */
@Controller
public class WeaponController {

    private final WeaponService weaponService;
    private final PeriodRepository periodRepository;

    // Dependency Injection через конструктор
    public WeaponController(WeaponService weaponService,
                            PeriodRepository periodRepository) {
        this.weaponService = weaponService;
        this.periodRepository = periodRepository;
    }

    /**
     * CREATE: Додати нову зброю
     * POST /addWeapon
     */
    @PostMapping("/addWeapon")
    public String addWeapon(@RequestParam String weaponType,
                            @RequestParam String weaponName,
                            @RequestParam Integer quantity,
                            @RequestParam BigDecimal unitCostUsd,
                            @RequestParam BigDecimal totalCostUsd,
                            @RequestParam Long periodId) {
        weaponService.createWeapon(weaponType, weaponName, quantity,
                unitCostUsd, totalCostUsd, periodId);
        return "redirect:/";
    }

    /**
     * READ: Переглянути зброю для редагування
     * GET /editWeapon/{id}
     */
    @GetMapping("/editWeapon/{id}")
    public String editWeaponForm(@PathVariable Long id, Model model) {
        Weapon weapon = weaponService.getWeaponById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Зброю з ID " + id + " не знайдено!"));

        model.addAttribute("weapon", weapon);
        model.addAttribute("periods", periodRepository.findAllByOrderByIdAsc());
        return "edit-weapon"; // templates/edit-weapon.html
    }

    /**
     * UPDATE: Оновити існуючу зброю
     * POST /updateWeapon
     */
    @PostMapping("/updateWeapon")
    public String updateWeapon(@RequestParam Long id,
                               @RequestParam String weaponType,
                               @RequestParam String weaponName,
                               @RequestParam Integer quantity,
                               @RequestParam BigDecimal unitCostUsd,
                               @RequestParam BigDecimal totalCostUsd,
                               @RequestParam Long periodId) {
        weaponService.updateWeapon(id, weaponType, weaponName, quantity,
                unitCostUsd, totalCostUsd, periodId);
        return "redirect:/";
    }

    /**
     * DELETE: Видалити зброю за ID
     * GET /deleteWeapon/{id}
     */
    @GetMapping("/deleteWeapon/{id}")
    public String deleteWeapon(@PathVariable Long id) {
        weaponService.deleteWeapon(id);
        return "redirect:/";
    }
}