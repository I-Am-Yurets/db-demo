package dev.yurets.db_demo.controller;

import dev.yurets.db_demo.repository.CountryRepository;
import dev.yurets.db_demo.repository.PeriodRepository;
import dev.yurets.db_demo.repository.WeaponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Головний контролер
 * Відповідає за відображення головної сторінки з усіма даними
 */
@Controller
public class MainController {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private PeriodRepository periodRepository;

    @Autowired
    private WeaponRepository weaponRepository;

    /**
     * Головна сторінка
     * GET /
     *
     * Завантажує всі дані з БД і передає їх у HTML-шаблон
     */
    @GetMapping("/")
    public String index(Model model) {
        // Додаємо до моделі списки для відображення в Thymeleaf
        model.addAttribute("countries", countryRepository.findAllByOrderByIdAsc());
        model.addAttribute("periods", periodRepository.findAllByOrderByIdAsc());
        model.addAttribute("weapons", weaponRepository.findAllByOrderByIdAsc());

        System.out.println("[INFO] Завантажено головну сторінку");

        return "webpage"; // templates/webpage.html
    }
}