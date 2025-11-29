package dev.yurets.db_demo.controller;

import dev.yurets.db_demo.model.Period;
import dev.yurets.db_demo.service.CountryService;
import dev.yurets.db_demo.service.PeriodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Контролер для роботи з періодами допомоги
 * Обробляє всі CRUD операції: CREATE, READ, UPDATE, DELETE
 */
@Controller
public class PeriodController {

    private static final Logger log = LoggerFactory.getLogger(PeriodController.class);

    private final PeriodService periodService;
    private final CountryService countryService;

    public PeriodController(PeriodService periodService,
                            CountryService countryService) {
        this.periodService = periodService;
        this.countryService = countryService;
    }

    /**
     * CREATE: Додати новий період
     * POST /addPeriod
     */
    @PostMapping("/addPeriod")
    public String addPeriod(@RequestParam String periodName,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                            @RequestParam BigDecimal aidAmountUsd,
                            @RequestParam Long countryId,
                            RedirectAttributes redirectAttributes) {
        try {
            periodService.createPeriod(periodName, startDate, endDate, aidAmountUsd, countryId);
            redirectAttributes.addFlashAttribute("message", "Період '" + periodName + "' успішно додано!");
        } catch (IllegalArgumentException e) {
            log.error("Помилка валідації при додаванні періоду: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка валідації: " + e.getMessage());
        } catch (DataIntegrityViolationException e) {
            log.error("Помилка БД при додаванні періоду: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка бази даних: можливо період з такими даними вже існує");
        } catch (Exception e) {
            log.error("Несподівана помилка при додаванні періоду", e);
            redirectAttributes.addFlashAttribute("error", "Виникла несподівана помилка при додаванні періоду");
        }
        return "redirect:/";
    }

    /**
     * READ: Переглянути період для редагування
     * GET /editPeriod/{id}
     */
    @GetMapping("/editPeriod/{id}")
    public String editPeriodForm(@PathVariable Long id, Model model) {
        Period period = periodService.getPeriodById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Період з ID " + id + " не знайдено!"));

        model.addAttribute("period", period);
        model.addAttribute("countries", countryService.getAllCountries());
        return "edit-period";
    }

    /**
     * UPDATE: Оновити існуючий період
     * POST /updatePeriod
     */
    @PostMapping("/updatePeriod")
    public String updatePeriod(@RequestParam Long id,
                               @RequestParam String periodName,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                               @RequestParam BigDecimal aidAmountUsd,
                               @RequestParam Long countryId,
                               RedirectAttributes redirectAttributes) {
        try {
            periodService.updatePeriod(id, periodName, startDate, endDate, aidAmountUsd, countryId);
            redirectAttributes.addFlashAttribute("message", "Період успішно оновлено!");
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            log.error("Помилка валідації при оновленні періоду: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка валідації: " + e.getMessage());
            return "redirect:/editPeriod/" + id;
        } catch (DataIntegrityViolationException e) {
            log.error("Помилка БД при оновленні періоду: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка бази даних: можливо період з такими даними вже існує");
            return "redirect:/editPeriod/" + id;
        } catch (Exception e) {
            log.error("Несподівана помилка при оновленні періоду", e);
            redirectAttributes.addFlashAttribute("error", "Виникла несподівана помилка при оновленні періоду");
            return "redirect:/editPeriod/" + id;
        }
    }

    /**
     * DELETE: Видалити період за ID
     * GET /deletePeriod/{id}
     */
    @GetMapping("/deletePeriod/{id}")
    public String deletePeriod(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            periodService.deletePeriod(id);
            redirectAttributes.addFlashAttribute("message", "Період успішно видалено!");
        } catch (IllegalArgumentException e) {
            log.error("Помилка при видаленні періоду: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка: " + e.getMessage());
        } catch (Exception e) {
            log.error("Несподівана помилка при видаленні періоду", e);
            redirectAttributes.addFlashAttribute("error", "Виникла помилка при видаленні періоду");
        }
        return "redirect:/";
    }
}