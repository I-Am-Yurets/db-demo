package dev.yurets.db_demo.controller;

import dev.yurets.db_demo.model.Period;
import dev.yurets.db_demo.repository.CountryRepository;
import dev.yurets.db_demo.service.PeriodService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Контролер для роботи з періодами допомоги
 * Обробляє всі CRUD операції: CREATE, READ, UPDATE, DELETE
 */
@Controller
public class PeriodController {

    private final PeriodService periodService;
    private final CountryRepository countryRepository;

    // Dependency Injection через конструктор
    public PeriodController(PeriodService periodService,
                            CountryRepository countryRepository) {
        this.periodService = periodService;
        this.countryRepository = countryRepository;
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
                            @RequestParam Long countryId) {
        periodService.createPeriod(periodName, startDate, endDate, aidAmountUsd, countryId);
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
        model.addAttribute("countries", countryRepository.findAllByOrderByIdAsc());
        return "edit-period"; // templates/edit-period.html
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
                               @RequestParam Long countryId) {
        periodService.updatePeriod(id, periodName, startDate, endDate, aidAmountUsd, countryId);
        return "redirect:/";
    }

    /**
     * DELETE: Видалити період за ID
     * GET /deletePeriod/{id}
     */
    @GetMapping("/deletePeriod/{id}")
    public String deletePeriod(@PathVariable Long id) {
        periodService.deletePeriod(id);
        return "redirect:/";
    }
}