package dev.yurets.db_demo.controller;

import dev.yurets.db_demo.model.WeaponDelivery;
import dev.yurets.db_demo.service.DonorService;
import dev.yurets.db_demo.service.WeaponDeliveryService;
import dev.yurets.db_demo.service.WeaponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

/**
 * Контролер для роботи з поставками зброї
 */
@Slf4j
@Controller
public class WeaponDeliveryController {


    private final WeaponDeliveryService deliveryService;
    private final WeaponService weaponService;
    private final DonorService donorService;

    public WeaponDeliveryController(WeaponDeliveryService deliveryService,
                                    WeaponService weaponService,
                                    DonorService donorService) {
        this.deliveryService = deliveryService;
        this.weaponService = weaponService;
        this.donorService = donorService;
    }

    /**
     * CREATE: Додати нову поставку
     * POST /addDelivery
     */
    @PostMapping("/addDelivery")
    public String addDelivery(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deliveryDate,
                              @RequestParam Integer quantityDelivered,
                              @RequestParam String deliveryStatus,
                              @RequestParam(required = false) String trackingNumber,
                              @RequestParam Long weaponId,
                              @RequestParam Long donorId,
                              RedirectAttributes redirectAttributes) {
        try {
            deliveryService.createDelivery(deliveryDate, quantityDelivered,
                    deliveryStatus, trackingNumber, weaponId, donorId);
            redirectAttributes.addFlashAttribute("message", "Поставку успішно додано!");
        } catch (IllegalArgumentException e) {
            log.error("Помилка валідації при додаванні поставки: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка валідації: " + e.getMessage());
        } catch (DataIntegrityViolationException e) {
            log.error("Помилка БД при додаванні поставки: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка бази даних");
        } catch (Exception e) {
            log.error("Несподівана помилка при додаванні поставки", e);
            redirectAttributes.addFlashAttribute("error", "Виникла несподівана помилка");
        }
        return "redirect:/";
    }

    /**
     * READ: Переглянути поставку для редагування
     * GET /editDelivery/{id}
     */
    @GetMapping("/editDelivery/{id}")
    public String editDeliveryForm(@PathVariable Long id, Model model) {
        WeaponDelivery delivery = deliveryService.getDeliveryById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Поставку з ID " + id + " не знайдено!"));

        model.addAttribute("delivery", delivery);
        model.addAttribute("weapons", weaponService.getAllWeapons());
        model.addAttribute("donors", donorService.getAllDonors());
        model.addAttribute("editType", "delivery");
        return "edit-delivery";
    }

    /**
     * UPDATE: Оновити існуючу поставку
     * POST /updateDelivery
     */
    @PostMapping("/updateDelivery")
    public String updateDelivery(@RequestParam Long id,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deliveryDate,
                                 @RequestParam Integer quantityDelivered,
                                 @RequestParam String deliveryStatus,
                                 @RequestParam(required = false) String trackingNumber,
                                 @RequestParam Long weaponId,
                                 @RequestParam Long donorId,
                                 RedirectAttributes redirectAttributes) {
        try {
            deliveryService.updateDelivery(id, deliveryDate, quantityDelivered,
                    deliveryStatus, trackingNumber, weaponId, donorId);
            redirectAttributes.addFlashAttribute("message", "Поставку успішно оновлено!");
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            log.error("Помилка валідації при оновленні поставки: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка валідації: " + e.getMessage());
            return "redirect:/editDelivery/" + id;
        } catch (DataIntegrityViolationException e) {
            log.error("Помилка БД при оновленні поставки: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка бази даних");
            return "redirect:/editDelivery/" + id;
        } catch (Exception e) {
            log.error("Несподівана помилка при оновленні поставки", e);
            redirectAttributes.addFlashAttribute("error", "Виникла несподівана помилка");
            return "redirect:/editDelivery/" + id;
        }
    }

    /**
     * DELETE: Видалити поставку за ID
     * GET /deleteDelivery/{id}
     */
    @GetMapping("/deleteDelivery/{id}")
    public String deleteDelivery(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            deliveryService.deleteDelivery(id);
            redirectAttributes.addFlashAttribute("message", "Поставку успішно видалено!");
        } catch (IllegalArgumentException e) {
            log.error("Помилка при видаленні поставки: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Помилка: " + e.getMessage());
        } catch (Exception e) {
            log.error("Несподівана помилка при видаленні поставки", e);
            redirectAttributes.addFlashAttribute("error", "Виникла помилка при видаленні поставки");
        }
        return "redirect:/";
    }
}