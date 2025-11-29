package dev.yurets.db_demo.controller;

import dev.yurets.db_demo.model.Weapon;
import dev.yurets.db_demo.service.PeriodService;
import dev.yurets.db_demo.service.WeaponService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

/**
 * Контролер для роботи зі зброєю
 * Обробляє всі CRUD операції: CREATE, READ, UPDATE, DELETE
 */
@Controller
public class WeaponController {

    private static final Logger log = LoggerFactory.getLogger(WeaponController.class);

    private final WeaponService weaponService;
    private final PeriodService periodService;

    public WeaponController(WeaponService weaponService,
                            PeriodService periodService) {
        this.weaponService = weaponService;
        this.periodService = periodService;
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
                            @RequestParam Long periodId,
                            RedirectAttributes redirectAttributes) {
        try {
            weaponService.createWeapon(weaponType, weaponName, quantity,
                    unitCostUsd, totalCostUsd, periodId);
            redirectAttributes.addFlashAttribute("message", "Зброю '" + weaponName + "' успішно додано!");
        } catch (IllegalArgumentException e) {
            log.error("Помилка валідації при додаванні зброї: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка валідації: " + e.getMessage());
        } catch (DataIntegrityViolationException e) {
            log.error("Помилка БД при додаванні зброї: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка бази даних: можливо така зброя вже існує");
        } catch (Exception e) {
            log.error("Несподівана помилка при додаванні зброї", e);
            redirectAttributes.addFlashAttribute("error", "Виникла несподівана помилка при додаванні зброї");
        }
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
        model.addAttribute("periods", periodService.getAllPeriods());
        return "edit-weapon";
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
                               @RequestParam Long periodId,
                               RedirectAttributes redirectAttributes) {
        try {
            weaponService.updateWeapon(id, weaponType, weaponName, quantity,
                    unitCostUsd, totalCostUsd, periodId);
            redirectAttributes.addFlashAttribute("message", "Зброю успішно оновлено!");
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            log.error("Помилка валідації при оновленні зброї: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка валідації: " + e.getMessage());
            return "redirect:/editWeapon/" + id;
        } catch (DataIntegrityViolationException e) {
            log.error("Помилка БД при оновленні зброї: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка бази даних: можливо така зброя вже існує");
            return "redirect:/editWeapon/" + id;
        } catch (Exception e) {
            log.error("Несподівана помилка при оновленні зброї", e);
            redirectAttributes.addFlashAttribute("error", "Виникла несподівана помилка при оновленні зброї");
            return "redirect:/editWeapon/" + id;
        }
    }

    /**
     * DELETE: Видалити зброю за ID
     * GET /deleteWeapon/{id}
     */
    @GetMapping("/deleteWeapon/{id}")
    public String deleteWeapon(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            weaponService.deleteWeapon(id);
            redirectAttributes.addFlashAttribute("message", "Зброю успішно видалено!");
        } catch (IllegalArgumentException e) {
            log.error("Помилка при видаленні зброї: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка: " + e.getMessage());
        } catch (Exception e) {
            log.error("Несподівана помилка при видаленні зброї", e);
            redirectAttributes.addFlashAttribute("error", "Виникла помилка при видаленні зброї");
        }
        return "redirect:/";
    }
}