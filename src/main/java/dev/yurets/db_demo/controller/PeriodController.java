package dev.yurets.db_demo.controller;

import dev.yurets.db_demo.model.Country;
import dev.yurets.db_demo.model.Period;
import dev.yurets.db_demo.repository.CountryRepository;
import dev.yurets.db_demo.repository.PeriodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Контролер для роботи з періодами допомоги
 * Обробляє операції CREATE та DELETE для таблиці periods
 */
@Controller
public class PeriodController {

    @Autowired
    private PeriodRepository periodRepository;

    @Autowired
    private CountryRepository countryRepository;

    /**
     * Додати новий період допомоги
     * POST /addPeriod
     */
    @PostMapping("/addPeriod")
    public String addPeriod(@RequestParam String periodName,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, // !!! ВИПРАВЛЕНО !!!
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate, // !!! ВИПРАВЛЕНО !!!
                            @RequestParam BigDecimal aidAmountUsd,
                            @RequestParam Long countryId) {

        // Знаходимо країну, до якої належить період
        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Країну з ID " + countryId + " не знайдено!"));

        Period period = new Period(periodName, startDate, endDate, aidAmountUsd, country);
        periodRepository.save(period);

        System.out.println("[INFO] Додано період: " + periodName +
                " для країни " + country.getName());

        return "redirect:/";
    }

    /**
     * Видалити період за ID
     * GET /deletePeriod/{id}
     *
     * Каскадне видалення: видалення періоду автоматично видалить
     * всю пов'язану зброю завдяки JPA cascade
     */
    @GetMapping("/deletePeriod/{id}")
    public String deletePeriod(@PathVariable Long id) {
        periodRepository.findById(id).ifPresent(period -> {
            System.out.println("[INFO] Видалення періоду: " + period.getPeriodName());
        });

        periodRepository.deleteById(id);
        return "redirect:/";
    }
}