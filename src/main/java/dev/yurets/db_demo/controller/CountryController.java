package dev.yurets.db_demo.controller;

import dev.yurets.db_demo.model.Country;
import dev.yurets.db_demo.service.CountryService;
import lombok.extern.slf4j.Slf4j;
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
 * Контролер для роботи з країнами-донорами
 * Обробляє всі CRUD операції: CREATE, READ, UPDATE, DELETE
 */
@Slf4j
@Controller
public class CountryController {

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    /**
     * CREATE: Додати нову країну
     * POST /addCountry
     */
    @PostMapping("/addCountry")
    public String addCountry(@RequestParam String name,
                             @RequestParam BigDecimal totalAidUsd,
                             RedirectAttributes redirectAttributes) {
        try {
            countryService.createCountry(name, totalAidUsd);
            redirectAttributes.addFlashAttribute("message", "Країна '" + name + "' успішно додана!");
        } catch (IllegalArgumentException e) {
            log.error("Помилка валідації при додаванні країни: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка валідації: " + e.getMessage());
            redirectAttributes.addFlashAttribute("oldName", name);
            redirectAttributes.addFlashAttribute("oldAid", totalAidUsd);
        } catch (DataIntegrityViolationException e) {
            log.error("Помилка унікальності при додаванні країни: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Країна з назвою '" + name + "' вже існує. Введіть іншу назву.");
            redirectAttributes.addFlashAttribute("oldName", name);
            redirectAttributes.addFlashAttribute("oldAid", totalAidUsd);
        } catch (Exception e) {
            log.error("Несподівана помилка при додаванні країни", e);
            redirectAttributes.addFlashAttribute("error", "Виникла несподівана помилка при додаванні країни.");
        }
        return "redirect:/";
    }

    /**
     * READ: Переглянути країну для редагування
     * GET /editCountry/{id}
     */
    @GetMapping("/editCountry/{id}")
    public String editCountryForm(@PathVariable Long id, Model model) {
        Country country = countryService.getCountryById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Країну з ID " + id + " не знайдено!"));

        model.addAttribute("country", country);
        return "edit-country";
    }

    /**
     * UPDATE: Оновити існуючу країну
     * POST /updateCountry
     */
    @PostMapping("/updateCountry")
    public String updateCountry(@RequestParam Long id,
                                @RequestParam String name,
                                @RequestParam BigDecimal totalAidUsd,
                                RedirectAttributes redirectAttributes) {
        try {
            countryService.updateCountry(id, name, totalAidUsd);
            redirectAttributes.addFlashAttribute("message", "Країну успішно оновлено!");
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            log.error("Помилка валідації при оновленні країни: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка валідації: " + e.getMessage());
            return "redirect:/editCountry/" + id;
        } catch (DataIntegrityViolationException e) {
            log.error("Помилка унікальності при оновленні країни: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Країна з назвою '" + name + "' вже існує. Введіть іншу назву.");
            return "redirect:/editCountry/" + id;
        } catch (Exception e) {
            log.error("Несподівана помилка при оновленні країни", e);
            redirectAttributes.addFlashAttribute("error", "Виникла несподівана помилка при оновленні країни.");
            return "redirect:/editCountry/" + id;
        }
    }

    /**
     * DELETE: Видалити країну за ID
     * GET /deleteCountry/{id}
     */
    @GetMapping("/deleteCountry/{id}")
    public String deleteCountry(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            countryService.deleteCountry(id);
            redirectAttributes.addFlashAttribute("message", "Країну успішно видалено!");
        } catch (IllegalArgumentException e) {
            log.error("Помилка при видаленні країни: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка: " + e.getMessage());
        } catch (Exception e) {
            log.error("Несподівана помилка при видаленні країни", e);
            redirectAttributes.addFlashAttribute("error", "Виникла помилка при видаленні країни");
        }
        return "redirect:/";
    }
}