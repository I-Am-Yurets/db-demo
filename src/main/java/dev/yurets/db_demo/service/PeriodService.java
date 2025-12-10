package dev.yurets.db_demo.service;

import dev.yurets.db_demo.model.Country;
import dev.yurets.db_demo.model.Period;
import dev.yurets.db_demo.repository.CountryRepository;
import dev.yurets.db_demo.repository.PeriodRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Сервіс для роботи з періодами допомоги
 * Прошарок між контролерами та репозиторієм
 */
@Slf4j
@Service
@Transactional
public class PeriodService {

    private final PeriodRepository periodRepository;
    private final CountryRepository countryRepository;

    public PeriodService(PeriodRepository periodRepository,
                         CountryRepository countryRepository) {
        this.periodRepository = periodRepository;
        this.countryRepository = countryRepository;
    }

    /**
     * Отримати всі періоди відсортовані за ID
     */
    public List<Period> getAllPeriods() {
        return periodRepository.findAllByOrderByIdAsc();
    }

    /**
     * Знайти період за ID
     */
    public Optional<Period> getPeriodById(Long id) {
        return periodRepository.findById(id);
    }

    /**
     * Отримати всі періоди для конкретної країни
     */
    public List<Period> getPeriodsByCountryId(Long countryId) {
        return periodRepository.findByCountryId(countryId);
    }

    /**
     * Створити новий період з валідацією
     */
    public void createPeriod(String periodName, LocalDate startDate,
                             LocalDate endDate, BigDecimal aidAmountUsd,
                             Long countryId) {
        // Валідація назви періоду
        if (periodName == null || periodName.trim().isEmpty()) {
            throw new IllegalArgumentException("Назва періоду не може бути порожньою");
        }
        if (periodName.trim().length() < 2) {
            throw new IllegalArgumentException("Назва періоду має містити мінімум 2 символи");
        }
        if (periodName.trim().length() > 100) {
            throw new IllegalArgumentException("Назва періоду занадто довга (максимум 100 символів)");
        }

        // Валідація дати початку
        if (startDate == null) {
            throw new IllegalArgumentException("Дата початку обов'язкова");
        }

        // Валідація дат (якщо вказано дату закінчення)
        if (endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Дата закінчення не може бути раніше дати початку");
        }

        // Валідація суми
        if (aidAmountUsd == null) {
            throw new IllegalArgumentException("Сума допомоги обов'язкова");
        }
        if (aidAmountUsd.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сума допомоги має бути додатною");
        }
        if (aidAmountUsd.compareTo(new BigDecimal("999999999999999")) > 0) {
            throw new IllegalArgumentException("Сума допомоги занадто велика");
        }

        // Валідація країни
        if (countryId == null) {
            throw new IllegalArgumentException("Країна обов'язкова");
        }
        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Країну з ID " + countryId + " не знайдено!"));

        Period period = new Period(periodName.trim(), startDate, endDate, aidAmountUsd, country);
        Period saved = periodRepository.save(period);

        log.info("Створено період: {} для країни {} (ID: {})",
                saved.getPeriodName(), country.getName(), saved.getId());

    }

    /**
     * Оновити існуючий період з валідацією
     */
    public void updatePeriod(Long id, String periodName, LocalDate startDate,
                             LocalDate endDate, BigDecimal aidAmountUsd,
                             Long countryId) {
        Period period = periodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Період з ID " + id + " не знайдено!"));

        // Валідація назви періоду
        if (periodName == null || periodName.trim().isEmpty()) {
            throw new IllegalArgumentException("Назва періоду не може бути порожньою");
        }
        if (periodName.trim().length() < 2) {
            throw new IllegalArgumentException("Назва періоду має містити мінімум 2 символи");
        }
        if (periodName.trim().length() > 100) {
            throw new IllegalArgumentException("Назва періоду занадто довга (максимум 100 символів)");
        }

        // Валідація дати початку
        if (startDate == null) {
            throw new IllegalArgumentException("Дата початку обов'язкова");
        }

        // Валідація дат (якщо вказано дату закінчення)
        if (endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Дата закінчення не може бути раніше дати початку");
        }

        // Валідація суми
        if (aidAmountUsd == null) {
            throw new IllegalArgumentException("Сума допомоги обов'язкова");
        }
        if (aidAmountUsd.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сума допомоги має бути додатною");
        }
        if (aidAmountUsd.compareTo(new BigDecimal("999999999999999")) > 0) {
            throw new IllegalArgumentException("Сума допомоги занадто велика");
        }

        // Валідація країни
        if (countryId == null) {
            throw new IllegalArgumentException("Країна обов'язкова");
        }
        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Країну з ID " + countryId + " не знайдено!"));

        period.setPeriodName(periodName.trim());
        period.setStartDate(startDate);
        period.setEndDate(endDate);
        period.setAidAmountUsd(aidAmountUsd);
        period.setCountry(country);

        Period updated = periodRepository.save(period);

        log.info("Оновлено період: {} (ID: {})", updated.getPeriodName(), updated.getId());

    }

    /**
     * Видалити період за ID
     * Каскадно видалить всю пов'язану зброю
     */
    public void deletePeriod(Long id) {
        Period period = periodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Період з ID " + id + " не знайдено!"));

        log.info("Видалення періоду: {} (ID: {})", period.getPeriodName(), period.getId());

        periodRepository.deleteById(id);
    }
}