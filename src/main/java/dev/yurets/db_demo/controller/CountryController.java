package dev.yurets.db_demo.controller;

import dev.yurets.db_demo.model.Country;
import dev.yurets.db_demo.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * Контролер для роботи з країнами-донорами
 * Обробляє операції CREATE та DELETE для таблиці countries
 */
@Controller
public class CountryController {

    @Autowired
    private CountryRepository countryRepository;

    /**
     * Додати нову країну
     * POST /addCountry
     */
    @PostMapping("/addCountry")
    public String addCountry(@RequestParam String name,
                             @RequestParam BigDecimal totalAidUsd) {
        Country country = new Country(name, totalAidUsd);
        countryRepository.save(country);

        System.out.println("[INFO] Додано країну: " + name +
                " з допомогою $" + totalAidUsd);

        return "redirect:/";
    }

    /**
     * Видалити країну за ID
     * GET /deleteCountry/{id}
     *
     * Каскадне видалення: видалення країни автоматично видалить
     * всі пов'язані періоди та зброю завдяки JPA cascade
     */
    @GetMapping("/deleteCountry/{id}")
    public String deleteCountry(@PathVariable Long id) {
        countryRepository.findById(id).ifPresent(country -> {
            System.out.println("[INFO] Видалення країни: " + country.getName());
        });

        countryRepository.deleteById(id);
        return "redirect:/";
    }
}