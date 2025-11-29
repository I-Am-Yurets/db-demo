package dev.yurets.db_demo.controller;

import dev.yurets.db_demo.model.Country;
import dev.yurets.db_demo.model.Period;
import dev.yurets.db_demo.model.Weapon;
import dev.yurets.db_demo.service.CountryService;
import dev.yurets.db_demo.service.PeriodService;
import dev.yurets.db_demo.service.WeaponService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Головний контролер
 * Відповідає за відображення головної сторінки з усіма даними
 */
@Controller
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    private final CountryService countryService;
    private final PeriodService periodService;
    private final WeaponService weaponService;

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
    public String index(@RequestParam(required = false) String searchCountry,
                        @RequestParam(required = false) String searchPeriod,
                        @RequestParam(required = false) String searchWeapon,
                        Model model) {

        // Отримання всіх даних
        List<Country> countries = countryService.getAllCountries();
        List<Period> periods = periodService.getAllPeriods();
        List<Weapon> weapons = weaponService.getAllWeapons();

        // Пошук країн
        if (searchCountry != null && !searchCountry.trim().isEmpty()) {
            String query = searchCountry.toLowerCase().trim();
            countries = countries.stream()
                    .filter(c -> c.getName().toLowerCase().contains(query))
                    .collect(Collectors.toList());
            model.addAttribute("searchCountry", searchCountry);
        }

        // Пошук періодів
        if (searchPeriod != null && !searchPeriod.trim().isEmpty()) {
            String query = searchPeriod.toLowerCase().trim();
            periods = periods.stream()
                    .filter(p -> p.getPeriodName().toLowerCase().contains(query) ||
                            p.getCountry().getName().toLowerCase().contains(query))
                    .collect(Collectors.toList());
            model.addAttribute("searchPeriod", searchPeriod);
        }

        // Пошук зброї
        if (searchWeapon != null && !searchWeapon.trim().isEmpty()) {
            String query = searchWeapon.toLowerCase().trim();
            weapons = weapons.stream()
                    .filter(w -> w.getWeaponName().toLowerCase().contains(query) ||
                            w.getWeaponType().toLowerCase().contains(query))
                    .collect(Collectors.toList());
            model.addAttribute("searchWeapon", searchWeapon);
        }

        model.addAttribute("countries", countries);
        model.addAttribute("periods", periods);
        model.addAttribute("weapons", weapons);

        log.info("Завантажено головну сторінку (CRUD)");

        return "webpage";
    }

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

        log.info("Завантажено сторінку перегляду таблиць");

        return "view-tables";
    }
}