package dev.yurets.db_demo.controller;

import dev.yurets.db_demo.service.CountryService;
import dev.yurets.db_demo.service.PeriodService;
import dev.yurets.db_demo.service.WeaponService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Головний контролер
 * Відповідає за відображення головної сторінки з усіма даними
 */
@Controller
public class MainController {

    private final CountryService countryService;
    private final PeriodService periodService;
    private final WeaponService weaponService;

    // Dependency Injection через конструктор
    public MainController(CountryService countryService,
                          PeriodService periodService,
                          WeaponService weaponService) {
        this.countryService = countryService;
        this.periodService = periodService;
        this.weaponService = weaponService;
    }

    /**
     * Головна сторінка (CRUD)
     * GET /
     *
     * Завантажує всі дані з БД через сервіси і передає їх у HTML-шаблон для додавання/редагування.
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("countries", countryService.getAllCountries());
        model.addAttribute("periods", periodService.getAllPeriods());
        model.addAttribute("weapons", weaponService.getAllWeapons());

        System.out.println("[INFO] Завантажено головну сторінку (CRUD)");

        return "webpage"; // templates/webpage.html
    }

    // --- НОВИЙ МЕТОД ДЛЯ ПЕРЕГЛЯДУ ТАБЛИЦЬ (READ) ---

    /**
     * Сторінка перегляду таблиць (тільки Read)
     * GET /viewAll
     *
     * Завантажує всі дані з БД та передає їх у шаблон для відображення без форм додавання.
     */
    @GetMapping("/viewAll")
    public String viewTables(Model model) {
        model.addAttribute("countries", countryService.getAllCountries());
        model.addAttribute("periods", periodService.getAllPeriods());
        model.addAttribute("weapons", weaponService.getAllWeapons());

        System.out.println("[INFO] Завантажено сторінку перегляду таблиць");

        return "view-tables"; // templates/view-tables.html
    }
}