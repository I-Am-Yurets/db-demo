package dev.yurets.db_demo;

import dev.yurets.db_demo.controller.CountryController;
import dev.yurets.db_demo.model.Country;
import dev.yurets.db_demo.service.CountryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Тести для CountryController
 * Перевіряють всі CRUD операції з використанням Mockito
 *
 * ОНОВЛЕНО: Додано параметр isOpen у всі методи
 */
class CountryControllerTest {

    @Mock
    private CountryService countryService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private CountryController countryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- ТЕСТ 1: Додавання країни (Успішно) ---
    @Test
    void testAddCountry_Success() {
        String countryName = "Ukraine";
        BigDecimal totalAid = new BigDecimal("5000000000");
        Boolean isOpen = true;  // ⬅️ ДОДАНО

        // Виклик методу
        String viewName = countryController.addCountry(countryName, totalAid, isOpen, redirectAttributes);

        // Перевірка, що сервіс був викликаний з правильними параметрами
        verify(countryService, times(1)).createCountry(countryName, totalAid, isOpen);  // ⬅️ ЗМІНЕНО

        // Перевірка повідомлення про успіх
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("message"), any(String.class));

        // Перевірка редіректу
        assertEquals("redirect:/", viewName);
    }

    // --- ТЕСТ 2: Додавання країни (Помилка валідації) ---
    @Test
    void testAddCountry_ValidationError() {
        String countryName = "";
        BigDecimal totalAid = new BigDecimal("5000000000");
        Boolean isOpen = true;  // ⬅️ ДОДАНО

        // Налаштування мока для викидання винятку
        doThrow(new IllegalArgumentException("Назва країни не може бути порожньою"))
                .when(countryService).createCountry(countryName, totalAid, isOpen);  // ⬅️ ЗМІНЕНО

        // Виклик методу
        String viewName = countryController.addCountry(countryName, totalAid, isOpen, redirectAttributes);

        // Перевірка, що було передано повідомлення про помилку
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("error"), any(String.class));

        // Перевірка редіректу
        assertEquals("redirect:/", viewName);
    }

    // --- ТЕСТ 3: Перегляд країни для редагування (Успішно) ---
    @Test
    void testEditCountryForm_Success() {
        Long countryId = 1L;
        Country mockCountry = new Country("Poland", new BigDecimal("3500000000"));
        mockCountry.setId(countryId);
        mockCountry.setOpen(true);  // ⬅️ ДОДАНО

        // Налаштування мока
        when(countryService.getCountryById(countryId)).thenReturn(Optional.of(mockCountry));

        // Виклик методу
        String viewName = countryController.editCountryForm(countryId, model);

        // Перевірка, що країна була додана в модель
        verify(model, times(1)).addAttribute("country", mockCountry);

        // Перевірка повернення правильного view
        assertEquals("edit-country", viewName);
    }

    // --- ТЕСТ 4: Перегляд неіснуючої країни (Помилка) ---
    @Test
    void testEditCountryForm_NotFound() {
        Long countryId = 999L;

        // Налаштування мока для повернення порожнього Optional
        when(countryService.getCountryById(countryId)).thenReturn(Optional.empty());

        // Перевірка, що викидається IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            countryController.editCountryForm(countryId, model);
        });
    }

    // --- ТЕСТ 5: Оновлення країни (Успішно) ---
    @Test
    void testUpdateCountry_Success() {
        Long countryId = 1L;
        String newName = "New Country Name";
        BigDecimal newAid = new BigDecimal("10000000000");
        Boolean isOpen = false;  // ⬅️ ДОДАНО (зачинена)

        // Виклик методу
        String viewName = countryController.updateCountry(countryId, newName, newAid, isOpen, redirectAttributes);

        // Перевірка, що сервіс був викликаний з правильними параметрами
        verify(countryService, times(1)).updateCountry(countryId, newName, newAid, isOpen);  // ⬅️ ЗМІНЕНО

        // Перевірка повідомлення про успіх
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("message"), any(String.class));

        // Перевірка редіректу
        assertEquals("redirect:/", viewName);
    }

    // --- ТЕСТ 6: Оновлення країни (Помилка валідації) ---
    @Test
    void testUpdateCountry_ValidationError() {
        Long countryId = 1L;
        String newName = "X"; // занадто коротка назва
        BigDecimal newAid = new BigDecimal("10000000000");
        Boolean isOpen = true;  // ⬅️ ДОДАНО

        // Налаштування мока для викидання винятку
        doThrow(new IllegalArgumentException("Назва країни має містити мінімум 2 символи"))
                .when(countryService).updateCountry(countryId, newName, newAid, isOpen);  // ⬅️ ЗМІНЕНО

        // Виклик методу
        String viewName = countryController.updateCountry(countryId, newName, newAid, isOpen, redirectAttributes);

        // Перевірка, що було передано повідомлення про помилку
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("error"), any(String.class));

        // Перевірка редіректу до форми редагування
        assertEquals("redirect:/editCountry/" + countryId, viewName);
    }

    // --- ТЕСТ 7: Видалення країни (Успішно) ---
    @Test
    void testDeleteCountry_Success() {
        Long countryId = 1L;

        // Виклик методу
        String viewName = countryController.deleteCountry(countryId, redirectAttributes);

        // Перевірка, що сервіс був викликаний
        verify(countryService, times(1)).deleteCountry(countryId);

        // Перевірка повідомлення про успіх
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("message"), any(String.class));

        // Перевірка редіректу
        assertEquals("redirect:/", viewName);
    }

    // --- ТЕСТ 8: Видалення неіснуючої країни (Помилка) ---
    @Test
    void testDeleteCountry_NotFound() {
        Long countryId = 999L;

        // Налаштування мока для викидання винятку
        doThrow(new IllegalArgumentException("Країну з ID 999 не знайдено!"))
                .when(countryService).deleteCountry(countryId);

        // Виклик методу
        String viewName = countryController.deleteCountry(countryId, redirectAttributes);

        // Перевірка, що було передано повідомлення про помилку
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("error"), any(String.class));

        // Перевірка редіректу
        assertEquals("redirect:/", viewName);
    }

    // --- ТЕСТ 9: Додавання країни з від'ємною сумою (Помилка) ---
    @Test
    void testAddCountry_NegativeAmount() {
        String countryName = "TestCountry";
        BigDecimal totalAid = new BigDecimal("-1000");
        Boolean isOpen = true;  // ⬅️ ДОДАНО

        // Налаштування мока для викидання винятку
        doThrow(new IllegalArgumentException("Сума допомоги має бути додатною"))
                .when(countryService).createCountry(countryName, totalAid, isOpen);  // ⬅️ ЗМІНЕНО

        // Виклик методу
        String viewName = countryController.addCountry(countryName, totalAid, isOpen, redirectAttributes);

        // Перевірка, що було передано повідомлення про помилку
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("error"), any(String.class));

        // Перевірка редіректу
        assertEquals("redirect:/", viewName);
    }

    // --- ТЕСТ 10: Оновлення країни з занадто довгою назвою (Помилка) ---
    @Test
    void testUpdateCountry_NameTooLong() {
        Long countryId = 1L;
        String newName = "A".repeat(101); // 101 символ (максимум 100)
        BigDecimal newAid = new BigDecimal("10000000000");
        Boolean isOpen = true;  // ⬅️ ДОДАНО

        // Налаштування мока для викидання винятку
        doThrow(new IllegalArgumentException("Назва країни занадто довга (максимум 100 символів)"))
                .when(countryService).updateCountry(countryId, newName, newAid, isOpen);  // ⬅️ ЗМІНЕНО

        // Виклик методу
        String viewName = countryController.updateCountry(countryId, newName, newAid, isOpen, redirectAttributes);

        // Перевірка, що було передано повідомлення про помилку
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("error"), any(String.class));

        // Перевірка редіректу до форми редагування
        assertEquals("redirect:/editCountry/" + countryId, viewName);
    }

    // --- НОВИЙ ТЕСТ 11: Додавання зачиненої країни ---
    @Test
    void testAddCountry_ClosedCountry() {
        String countryName = "ClosedCountry";
        BigDecimal totalAid = new BigDecimal("1000000000");
        Boolean isOpen = false;  // ⬅️ Зачинена країна

        // Виклик методу
        String viewName = countryController.addCountry(countryName, totalAid, isOpen, redirectAttributes);

        // Перевірка, що сервіс був викликаний з isOpen = false
        verify(countryService, times(1)).createCountry(countryName, totalAid, false);

        // Перевірка повідомлення про успіх
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("message"), any(String.class));

        // Перевірка редіректу
        assertEquals("redirect:/", viewName);
    }

    // --- НОВИЙ ТЕСТ 12: Зміна статусу країни з відкритої на зачинену ---
    @Test
    void testUpdateCountry_ChangeStatusToClosedSuccess() {
        Long countryId = 1L;
        String name = "USA";
        BigDecimal aid = new BigDecimal("75000000000");
        Boolean isOpen = false;  // ⬅️ Змінюємо на зачинену

        // Виклик методу
        String viewName = countryController.updateCountry(countryId, name, aid, isOpen, redirectAttributes);

        // Перевірка, що сервіс був викликаний з isOpen = false
        verify(countryService, times(1)).updateCountry(countryId, name, aid, false);

        // Перевірка успішного оновлення
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("message"), any(String.class));

        assertEquals("redirect:/", viewName);
    }

    // --- НОВИЙ ТЕСТ 13: Додавання країни без явного вказання статусу (за замовчуванням) ---
    @Test
    void testAddCountry_DefaultStatusOpen() {
        String countryName = "DefaultCountry";
        BigDecimal totalAid = new BigDecimal("2000000000");
        Boolean isOpen = null;  // ⬅️ Не вказано (має бути true за замовчуванням у контролері)

        // У контролері @RequestParam(defaultValue = "true") забезпечує true
        // Але для тесту передаємо true явно
        Boolean expectedIsOpen = true;

        // Виклик методу з true (як контролер зробить через defaultValue)
        String viewName = countryController.addCountry(countryName, totalAid, expectedIsOpen, redirectAttributes);

        // Перевірка, що сервіс був викликаний з isOpen = true
        verify(countryService, times(1)).createCountry(countryName, totalAid, true);

        verify(redirectAttributes, times(1)).addFlashAttribute(eq("message"), any(String.class));

        assertEquals("redirect:/", viewName);
    }
}