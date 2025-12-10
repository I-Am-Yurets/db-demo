package dev.yurets.db_demo.service;

import dev.yurets.db_demo.model.AidRequest;
import dev.yurets.db_demo.model.Country;
import dev.yurets.db_demo.model.Period;
import dev.yurets.db_demo.repository.AidRequestRepository;
import dev.yurets.db_demo.repository.CountryRepository;
import dev.yurets.db_demo.repository.PeriodRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Сервіс для роботи із запитами на допомогу
 * Містить бізнес-логіку та валідацію
 */
@Slf4j
@Service
@Transactional
public class AidRequestService {

    private final AidRequestRepository requestRepository;
    private final CountryRepository countryRepository;
    private final PeriodRepository periodRepository;
    private final WeaponDeliveryService deliveryService;

    public AidRequestService(AidRequestRepository requestRepository,
                             CountryRepository countryRepository,
                             PeriodRepository periodRepository,
                             WeaponDeliveryService deliveryService) {
        this.requestRepository = requestRepository;
        this.countryRepository = countryRepository;
        this.periodRepository = periodRepository;
        this.deliveryService = deliveryService;
    }

    public List<AidRequest> getAllRequests() {
        return requestRepository.findAllByOrderByRequestDateDesc();
    }

    public Optional<AidRequest> getRequestById(Long id) {
        return requestRepository.findById(id);
    }

    public List<AidRequest> getRequestsByStatus(String status) {
        return requestRepository.findByStatus(status);
    }

    public List<AidRequest> getRequestsByPriority(String priority) {
        return requestRepository.findByPriority(priority);
    }

    /**
     * Створити новий запит на допомогу
     */
    public void createRequest(String weaponType, String weaponName,
                              Integer requestedQuantity, String priority,
                              String requestReason, Long requestingCountryId,
                              Long donorCountryId, Long periodId) {

        // Валідація типу зброї
        if (weaponType == null || weaponType.trim().isEmpty()) {
            throw new IllegalArgumentException("Тип зброї не може бути порожнім");
        }
        if (!isValidWeaponType(weaponType)) {
            throw new IllegalArgumentException(
                    "Невірний тип зброї. Дозволені: Artillery, Air Defense, Vehicles, Ammunition, Aircraft");
        }

        // Валідація назви зброї
        if (weaponName == null || weaponName.trim().isEmpty()) {
            throw new IllegalArgumentException("Назва зброї не може бути порожньою");
        }
        if (weaponName.trim().length() < 2 || weaponName.trim().length() > 200) {
            throw new IllegalArgumentException("Назва зброї має містити від 2 до 200 символів");
        }

        // Валідація кількості
        if (requestedQuantity == null || requestedQuantity <= 0) {
            throw new IllegalArgumentException("Кількість має бути додатною");
        }
        if (requestedQuantity > 100000) {
            throw new IllegalArgumentException("Кількість занадто велика (максимум 100,000)");
        }

        // Валідація пріоритету
        if (priority == null || priority.trim().isEmpty()) {
            throw new IllegalArgumentException("Пріоритет обов'язковий");
        }
        if (!isValidPriority(priority)) {
            throw new IllegalArgumentException(
                    "Невірний пріоритет. Дозволені: URGENT, HIGH, MEDIUM, LOW");
        }

        // Валідація країни-отримувача
        if (requestingCountryId == null) {
            throw new IllegalArgumentException("Країна-отримувач обов'язкова");
        }
        Country requestingCountry = countryRepository.findById(requestingCountryId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Країну-отримувача з ID " + requestingCountryId + " не знайдено!"));

        // Валідація країни-донора (опціонально)
        Country donorCountry = null;
        if (donorCountryId != null) {
            donorCountry = countryRepository.findById(donorCountryId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Країну-донора з ID " + donorCountryId + " не знайдено!"));

            // БІЗНЕС-ЛОГІКА: Перевірка чи країна-донор відкрита
            if (!donorCountry.isOpen()) {
                throw new IllegalStateException(
                        "Країна " + donorCountry.getName() + " зараз не приймає запити (зачинена)");
            }
        }

        // Валідація періоду
        if (periodId == null) {
            throw new IllegalArgumentException("Період обов'язковий");
        }
        Period period = periodRepository.findById(periodId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Період з ID " + periodId + " не знайдено!"));

        // БІЗНЕС-ЛОГІКА: Перевірка чи період активний
        if (period.getEndDate() != null && period.getEndDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException(
                    "Період '" + period.getPeriodName() + "' вже завершено. Неможливо створити запит.");
        }

        // Створення запиту
        AidRequest request = new AidRequest(
                weaponType.trim(),
                weaponName.trim(),
                requestedQuantity,
                priority.toUpperCase(),
                "PENDING", // Початковий статус
                LocalDate.now(),
                requestReason != null ? requestReason.trim() : null,
                requestingCountry,
                donorCountry,
                period
        );

        AidRequest saved = requestRepository.save(request);
        log.info("Створено запит на допомогу: {} x {} (ID: {}, Пріоритет: {})",
                saved.getRequestedQuantity(), saved.getWeaponName(), saved.getId(), saved.getPriority());
    }

    /**
     * Оновити існуючий запит
     */
    public void updateRequest(Long id, String weaponType, String weaponName,
                              Integer requestedQuantity, String priority,
                              String requestReason, Long requestingCountryId,
                              Long donorCountryId, Long periodId) {

        AidRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Запит з ID " + id + " не знайдено!"));

        // Перевірка чи можна редагувати
        if (request.getStatus().equals("APPROVED") || request.getStatus().equals("DELIVERED")) {
            throw new IllegalStateException(
                    "Неможливо редагувати запит зі статусом " + request.getStatus());
        }

        // Валідація аналогічна createRequest
        if (weaponType == null || weaponType.trim().isEmpty() || !isValidWeaponType(weaponType)) {
            throw new IllegalArgumentException("Невірний тип зброї");
        }
        if (weaponName == null || weaponName.trim().isEmpty()) {
            throw new IllegalArgumentException("Назва зброї не може бути порожньою");
        }
        if (requestedQuantity == null || requestedQuantity <= 0) {
            throw new IllegalArgumentException("Кількість має бути додатною");
        }
        if (priority == null || !isValidPriority(priority)) {
            throw new IllegalArgumentException("Невірний пріоритет");
        }

        Country requestingCountry = countryRepository.findById(requestingCountryId)
                .orElseThrow(() -> new IllegalArgumentException("Країну-отримувача не знайдено"));

        Country donorCountry = null;
        if (donorCountryId != null) {
            donorCountry = countryRepository.findById(donorCountryId)
                    .orElseThrow(() -> new IllegalArgumentException("Країну-донора не знайдено"));

            if (!donorCountry.isOpen()) {
                throw new IllegalStateException(
                        "Країна " + donorCountry.getName() + " зараз не приймає запити");
            }
        }

        Period period = periodRepository.findById(periodId)
                .orElseThrow(() -> new IllegalArgumentException("Період не знайдено"));

        // Оновлення полів
        request.setWeaponType(weaponType.trim());
        request.setWeaponName(weaponName.trim());
        request.setRequestedQuantity(requestedQuantity);
        request.setPriority(priority.toUpperCase());
        request.setRequestReason(requestReason != null ? requestReason.trim() : null);
        request.setRequestingCountry(requestingCountry);
        request.setDonorCountry(donorCountry);
        request.setPeriod(period);

        AidRequest updated = requestRepository.save(request);
        log.info("Оновлено запит на допомогу (ID: {})", updated.getId());
    }

    /**
     * Схвалити запит та створити поставку
     */
    public void approveRequest(Long id) {
        AidRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Запит з ID " + id + " не знайдено!"));

        if (!request.getStatus().equals("PENDING")) {
            throw new IllegalStateException(
                    "Можна схвалити тільки запити зі статусом PENDING");
        }

        if (request.getDonorCountry() == null) {
            throw new IllegalStateException(
                    "Неможливо схвалити запит без призначеної країни-донора");
        }

        // Зміна статусу
        request.setStatus("APPROVED");
        requestRepository.save(request);

        log.info("✅ Схвалено запит на допомогу (ID: {}). Статус: APPROVED", request.getId());
    }

    /**
     * Відхилити запит
     */
    public void rejectRequest(Long id, String rejectionReason) {
        AidRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Запит з ID " + id + " не знайдено!"));

        if (!request.getStatus().equals("PENDING")) {
            throw new IllegalStateException(
                    "Можна відхилити тільки запити зі статусом PENDING");
        }

        request.setStatus("REJECTED");
        request.setRejectionReason(rejectionReason);
        requestRepository.save(request);

        log.info("❌ Відхилено запит на допомогу (ID: {}). Причина: {}", request.getId(), rejectionReason);
    }

    /**
     * Видалити запит
     */
    public void deleteRequest(Long id) {
        AidRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Запит з ID " + id + " не знайдено!"));

        // Можна видалити тільки якщо не DELIVERED
        if (request.getStatus().equals("DELIVERED")) {
            throw new IllegalStateException(
                    "Неможливо видалити запит зі статусом DELIVERED (вже доставлено)");
        }

        log.info("Видалення запиту на допомогу (ID: {})", request.getId());
        requestRepository.deleteById(id);
    }

    // --- Допоміжні методи валідації ---

    private boolean isValidWeaponType(String type) {
        return type.equals("Artillery") ||
                type.equals("Air Defense") ||
                type.equals("Vehicles") ||
                type.equals("Ammunition") ||
                type.equals("Aircraft");
    }

    private boolean isValidPriority(String priority) {
        return priority.equalsIgnoreCase("URGENT") ||
                priority.equalsIgnoreCase("HIGH") ||
                priority.equalsIgnoreCase("MEDIUM") ||
                priority.equalsIgnoreCase("LOW");
    }
}