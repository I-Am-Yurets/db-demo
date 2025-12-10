package dev.yurets.db_demo.controller;

import dev.yurets.db_demo.model.AidRequest;
import dev.yurets.db_demo.service.AidRequestService;
import dev.yurets.db_demo.service.CountryService;
import dev.yurets.db_demo.service.PeriodService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import lombok.extern.slf4j.Slf4j;
/**
 * Контролер для роботи із запитами на допомогу
 */
@Slf4j
@Controller
public class AidRequestController {

    private final AidRequestService requestService;
    private final CountryService countryService;
    private final PeriodService periodService;

    public AidRequestController(AidRequestService requestService,
                                CountryService countryService,
                                PeriodService periodService) {
        this.requestService = requestService;
        this.countryService = countryService;
        this.periodService = periodService;
    }

    /**
     * CREATE: Додати новий запит на допомогу
     * POST /addRequest
     */
    @PostMapping("/addRequest")
    public String addRequest(@RequestParam String weaponType,
                             @RequestParam String weaponName,
                             @RequestParam Integer requestedQuantity,
                             @RequestParam String priority,
                             @RequestParam(required = false) String requestReason,
                             @RequestParam Long requestingCountryId,
                             @RequestParam(required = false) Long donorCountryId,
                             @RequestParam Long periodId,
                             RedirectAttributes redirectAttributes) {
        try {
            requestService.createRequest(weaponType, weaponName, requestedQuantity,
                    priority, requestReason, requestingCountryId, donorCountryId, periodId);
            redirectAttributes.addFlashAttribute("message", "Запит на допомогу успішно створено!");
        } catch (IllegalArgumentException e) {
            log.error("Помилка валідації при створенні запиту: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка валідації: " + e.getMessage());
        } catch (IllegalStateException e) {
            log.error("Помилка бізнес-логіки при створенні запиту: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка: " + e.getMessage());
        } catch (DataIntegrityViolationException e) {
            log.error("Помилка БД при створенні запиту: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка бази даних");
        } catch (Exception e) {
            log.error("Несподівана помилка при створенні запиту", e);
            redirectAttributes.addFlashAttribute("error", "Виникла несподівана помилка");
        }
        return "redirect:/";
    }

    /**
     * READ: Переглянути запит для редагування
     * GET /editRequest/{id}
     */
    @GetMapping("/editRequest/{id}")
    public String editRequestForm(@PathVariable Long id, Model model) {
        AidRequest request = requestService.getRequestById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Запит з ID " + id + " не знайдено!"));

        model.addAttribute("request", request);
        model.addAttribute("countries", countryService.getAllCountries());
        model.addAttribute("periods", periodService.getAllPeriods());
        return "edit-request";
    }

    /**
     * UPDATE: Оновити існуючий запит
     * POST /updateRequest
     */
    @PostMapping("/updateRequest")
    public String updateRequest(@RequestParam Long id,
                                @RequestParam String weaponType,
                                @RequestParam String weaponName,
                                @RequestParam Integer requestedQuantity,
                                @RequestParam String priority,
                                @RequestParam(required = false) String requestReason,
                                @RequestParam Long requestingCountryId,
                                @RequestParam(required = false) Long donorCountryId,
                                @RequestParam Long periodId,
                                RedirectAttributes redirectAttributes) {
        try {
            requestService.updateRequest(id, weaponType, weaponName, requestedQuantity,
                    priority, requestReason, requestingCountryId, donorCountryId, periodId);
            redirectAttributes.addFlashAttribute("message", "Запит успішно оновлено!");
            return "redirect:/";
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Помилка при оновленні запиту: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка: " + e.getMessage());
            return "redirect:/editRequest/" + id;
        } catch (Exception e) {
            log.error("Несподівана помилка при оновленні запиту", e);
            redirectAttributes.addFlashAttribute("error", "Виникла несподівана помилка");
            return "redirect:/editRequest/" + id;
        }
    }

    /**
     * APPROVE: Схвалити запит
     * GET /approveRequest/{id}
     */
    @GetMapping("/approveRequest/{id}")
    public String approveRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            requestService.approveRequest(id);
            redirectAttributes.addFlashAttribute("message", "✅ Запит схвалено!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Помилка при схваленні запиту: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка: " + e.getMessage());
        } catch (Exception e) {
            log.error("Несподівана помилка при схваленні запиту", e);
            redirectAttributes.addFlashAttribute("error", "Виникла помилка при схваленні запиту");
        }
        return "redirect:/";
    }

    /**
     * REJECT: Відхилити запит
     * POST /rejectRequest/{id}
     */
    @PostMapping("/rejectRequest/{id}")
    public String rejectRequest(@PathVariable Long id,
                                @RequestParam String rejectionReason,
                                RedirectAttributes redirectAttributes) {
        try {
            requestService.rejectRequest(id, rejectionReason);
            redirectAttributes.addFlashAttribute("message", "❌ Запит відхилено");
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Помилка при відхиленні запиту: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка: " + e.getMessage());
        } catch (Exception e) {
            log.error("Несподівана помилка при відхиленні запиту", e);
            redirectAttributes.addFlashAttribute("error", "Виникла помилка при відхиленні запиту");
        }
        return "redirect:/";
    }

    /**
     * DELETE: Видалити запит за ID
     * GET /deleteRequest/{id}
     */
    @GetMapping("/deleteRequest/{id}")
    public String deleteRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            requestService.deleteRequest(id);
            redirectAttributes.addFlashAttribute("message", "Запит успішно видалено!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Помилка при видаленні запиту: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка: " + e.getMessage());
        } catch (Exception e) {
            log.error("Несподівана помилка при видаленні запиту", e);
            redirectAttributes.addFlashAttribute("error", "Виникла помилка при видаленні запиту");
        }
        return "redirect:/";
    }
}