package dev.yurets.db_demo.controller;

import dev.yurets.db_demo.model.Country;
import dev.yurets.db_demo.model.Donor;
import dev.yurets.db_demo.model.Period;
import dev.yurets.db_demo.model.Weapon;
import dev.yurets.db_demo.model.WeaponDelivery;
import dev.yurets.db_demo.service.CountryService;
import dev.yurets.db_demo.service.DonorService;
import dev.yurets.db_demo.service.PeriodService;
import dev.yurets.db_demo.service.WeaponDeliveryService;
import dev.yurets.db_demo.service.WeaponService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final DonorService donorService;
    private final WeaponDeliveryService deliveryService;

    public MainController(CountryService countryService,
                          PeriodService periodService,
                          WeaponService weaponService,
                          DonorService donorService,
                          WeaponDeliveryService deliveryService) {
        this.countryService = countryService;
        this.periodService = periodService;
        this.weaponService = weaponService;
        this.donorService = donorService;
        this.deliveryService = deliveryService;
    }

    /**
     * Головна сторінка (CRUD)
     * GET /
     *
     * ТІЛЬКИ ДЛЯ ADMIN!
     * Завантажує всі дані з БД через сервіси і передає їх у HTML-шаблон.
     */
    @GetMapping("/")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String index(@RequestParam(required = false) String searchCountry,
                        @RequestParam(required = false) String searchPeriod,
                        @RequestParam(required = false) String searchWeapon,
                        @RequestParam(required = false) String searchDonor,
                        @RequestParam(required = false) String searchDelivery,
                        Model model) {

        log.info("[WEB] Завантаження головної сторінки (CRUD) - доступ ADMIN");

        // Отримання всіх даних
        List<Country> countries = countryService.getAllCountries();
        List<Period> periods = periodService.getAllPeriods();
        List<Weapon> weapons = weaponService.getAllWeapons();
        List<Donor> donors = donorService.getAllDonors();
        List<WeaponDelivery> deliveries = deliveryService.getAllDeliveries();

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

        // Пошук донорів
        if (searchDonor != null && !searchDonor.trim().isEmpty()) {
            String query = searchDonor.toLowerCase().trim();
            donors = donors.stream()
                    .filter(d -> d.getOrganizationName().toLowerCase().contains(query) ||
                            d.getOrganizationType().toLowerCase().contains(query))
                    .collect(Collectors.toList());
            model.addAttribute("searchDonor", searchDonor);
        }

        // Пошук поставок
        if (searchDelivery != null && !searchDelivery.trim().isEmpty()) {
            String query = searchDelivery.toLowerCase().trim();
            deliveries = deliveries.stream()
                    .filter(d -> d.getWeapon().getWeaponName().toLowerCase().contains(query) ||
                            d.getDonor().getOrganizationName().toLowerCase().contains(query) ||
                            d.getDeliveryStatus().toLowerCase().contains(query))
                    .collect(Collectors.toList());
            model.addAttribute("searchDelivery", searchDelivery);
        }

        model.addAttribute("countries", countries);
        model.addAttribute("periods", periods);
        model.addAttribute("weapons", weapons);
        model.addAttribute("donors", donors);
        model.addAttribute("deliveries", deliveries);

        return "webpage";
    }

    /**
     * Сторінка перегляду таблиць (тільки Read)
     * GET /viewAll
     *
     * ДОСТУПНО ДЛЯ USER ТА ADMIN
     * Завантажує всі дані з БД для відображення без можливості редагування.
     */
    @GetMapping("/viewAll")
    public String viewTables(Model model) {
        log.info("[WEB] Завантаження сторінки перегляду таблиць");

        model.addAttribute("countries", countryService.getAllCountries());
        model.addAttribute("periods", periodService.getAllPeriods());
        model.addAttribute("weapons", weaponService.getAllWeapons());
        model.addAttribute("donors", donorService.getAllDonors());
        model.addAttribute("deliveries", deliveryService.getAllDeliveries());

        return "view-tables";
    }
}