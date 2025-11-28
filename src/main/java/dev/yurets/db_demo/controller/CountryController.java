package dev.yurets.db_demo.controller;

import dev.yurets.db_demo.model.Country;
import dev.yurets.db_demo.service.CountryService;
import org.springframework.dao.DataIntegrityViolationException; // Імпорт для обробки помилок
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Імпорт для передачі атрибутів

import java.math.BigDecimal;

/**
 * Контролер для роботи з країнами-донорами
 * Обробляє всі CRUD операції: CREATE, READ, UPDATE, DELETE
 */
@Controller
public class CountryController {

    private final CountryService countryService;

    // Dependency Injection через конструктор
    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    /**
     * CREATE: Додати нову країну
     * POST /addCountry
     * Обробляє DataIntegrityViolationException (помилка унікальності)
     */
    @PostMapping("/addCountry")
    public String addCountry(@RequestParam String name,
                             @RequestParam BigDecimal totalAidUsd,
                             RedirectAttributes redirectAttributes) { // Додано RedirectAttributes

        try {
            countryService.createCountry(name, totalAidUsd);
            // Додавання повідомлення про успіх (опціонально)
            redirectAttributes.addFlashAttribute("message", "Країна '" + name + "' успішно додана!");

        } catch (DataIntegrityViolationException e) {
            // КЛЮЧОВИЙ ЗМІН: Перехоплюємо помилку, коли назва країни вже існує
            System.err.println("[ERROR] Помилка унікальності при додаванні країни: " + e.getMessage());

            // Додаємо Flash-атрибут з повідомленням про помилку для відображення на '/'
            redirectAttributes.addFlashAttribute("error",
                    "**Помилка:** Країна з назвою '" + name + "' вже існує. Введіть іншу назву.");

            // Зберігаємо введені дані, щоб користувачу не доводилося вводити їх знову (опціонально, але зручно)
            redirectAttributes.addFlashAttribute("oldName", name);
            redirectAttributes.addFlashAttribute("oldAid", totalAidUsd);

        } catch (Exception e) {
            // Обробка інших непередбачених помилок
            redirectAttributes.addFlashAttribute("error", "Виникла несподівана помилка при додаванні країни.");
        }

        return "redirect:/"; // Завжди повертаємо на головну сторінку
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
        return "edit-country"; // templates/edit-country.html
    }

    /**
     * UPDATE: Оновити існуючу країну
     * POST /updateCountry
     */
    @PostMapping("/updateCountry")
    public String updateCountry(@RequestParam Long id,
                                @RequestParam String name,
                                @RequestParam BigDecimal totalAidUsd) {
        // У цьому методі також варто додати обробку DataIntegrityViolationException,
        // якщо користувач намагається оновити країну, змінивши її назву на вже існуючу.
        countryService.updateCountry(id, name, totalAidUsd);
        return "redirect:/";
    }

    /**
     * DELETE: Видалити країну за ID
     * GET /deleteCountry/{id}
     */
    @GetMapping("/deleteCountry/{id}")
    public String deleteCountry(@PathVariable Long id) {
        countryService.deleteCountry(id);
        return "redirect:/";
    }
}