package dev.yurets.db_demo.controller;

import dev.yurets.db_demo.model.Donor;
import dev.yurets.db_demo.service.CountryService;
import dev.yurets.db_demo.service.DonorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Контролер для роботи з донорами (організаціями)
 */
@Slf4j
@Controller
public class DonorController {

    private final DonorService donorService;
    private final CountryService countryService;

    public DonorController(DonorService donorService, CountryService countryService) {
        this.donorService = donorService;
        this.countryService = countryService;
    }

    /**
     * CREATE: Додати нового донора
     * POST /addDonor
     */
    @PostMapping("/addDonor")
    public String addDonor(@RequestParam String organizationName,
                           @RequestParam String organizationType,
                           @RequestParam(required = false) String contactInfo,
                           @RequestParam Long countryId,
                           RedirectAttributes redirectAttributes) {
        try {
            donorService.createDonor(organizationName, organizationType, contactInfo, countryId);
            redirectAttributes.addFlashAttribute("message", "Донора '" + organizationName + "' успішно додано!");
        } catch (IllegalArgumentException e) {
            log.error("Помилка валідації при додаванні донора: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка валідації: " + e.getMessage());
        } catch (DataIntegrityViolationException e) {
            log.error("Помилка БД при додаванні донора: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка бази даних");
        } catch (Exception e) {
            log.error("Несподівана помилка при додаванні донора", e);
            redirectAttributes.addFlashAttribute("error", "Виникла несподівана помилка");
        }
        return "redirect:/";
    }

    /**
     * READ: Переглянути донора для редагування
     * GET /editDonor/{id}
     */
    @GetMapping("/editDonor/{id}")
    public String editDonorForm(@PathVariable Long id, Model model) {
        Donor donor = donorService.getDonorById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Донора з ID " + id + " не знайдено!"));

        model.addAttribute("donor", donor);
        model.addAttribute("countries", countryService.getAllCountries());
        model.addAttribute("editType", "donor");
        return "edit-donor";
    }

    /**
     * UPDATE: Оновити існуючого донора
     * POST /updateDonor
     */
    @PostMapping("/updateDonor")
    public String updateDonor(@RequestParam Long id,
                              @RequestParam String organizationName,
                              @RequestParam String organizationType,
                              @RequestParam(required = false) String contactInfo,
                              @RequestParam Long countryId,
                              RedirectAttributes redirectAttributes) {
        try {
            donorService.updateDonor(id, organizationName, organizationType, contactInfo, countryId);
            redirectAttributes.addFlashAttribute("message", "Донора успішно оновлено!");
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            log.error("Помилка валідації при оновленні донора: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка валідації: " + e.getMessage());
            return "redirect:/editDonor/" + id;
        } catch (DataIntegrityViolationException e) {
            log.error("Помилка БД при оновленні донора: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка бази даних");
            return "redirect:/editDonor/" + id;
        } catch (Exception e) {
            log.error("Несподівана помилка при оновленні донора", e);
            redirectAttributes.addFlashAttribute("error", "Виникла несподівана помилка");
            return "redirect:/editDonor/" + id;
        }
    }

    /**
     * DELETE: Видалити донора за ID
     * GET /deleteDonor/{id}
     */
    @GetMapping("/deleteDonor/{id}")
    public String deleteDonor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            donorService.deleteDonor(id);
            redirectAttributes.addFlashAttribute("message", "Донора успішно видалено!");
        } catch (IllegalArgumentException e) {
            log.error("Помилка при видаленні донора: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка: " + e.getMessage());
        } catch (Exception e) {
            log.error("Несподівана помилка при видаленні донора", e);
            redirectAttributes.addFlashAttribute("error", "Виникла помилка при видаленні донора");
        }
        return "redirect:/";
    }
}