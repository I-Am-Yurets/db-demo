package dev.yurets.db_demo.service;

import dev.yurets.db_demo.model.Country;
import dev.yurets.db_demo.model.Period;
import dev.yurets.db_demo.repository.CountryRepository;
import dev.yurets.db_demo.repository.PeriodRepository;
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
@Service
@Transactional
public class PeriodService {

    private final PeriodRepository periodRepository;
    private final CountryRepository countryRepository;

    // Dependency Injection через конструктор
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
     * Створити новий період
     */
    public Period createPeriod(String periodName, LocalDate startDate,
                               LocalDate endDate, BigDecimal aidAmountUsd,
                               Long countryId) {
        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Країну з ID " + countryId + " не знайдено!"));

        Period period = new Period(periodName, startDate, endDate, aidAmountUsd, country);
        Period saved = periodRepository.save(period);

        System.out.println("[SERVICE] Створено період: " + saved.getPeriodName() +
                " для країни " + country.getName() +
                " (ID: " + saved.getId() + ")");

        return saved;
    }

    /**
     * Оновити існуючий період
     */
    public Period updatePeriod(Long id, String periodName, LocalDate startDate,
                               LocalDate endDate, BigDecimal aidAmountUsd,
                               Long countryId) {
        Period period = periodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Період з ID " + id + " не знайдено!"));

        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Країну з ID " + countryId + " не знайдено!"));

        period.setPeriodName(periodName);
        period.setStartDate(startDate);
        period.setEndDate(endDate);
        period.setAidAmountUsd(aidAmountUsd);
        period.setCountry(country);

        Period updated = periodRepository.save(period);

        System.out.println("[SERVICE] Оновлено період: " + updated.getPeriodName() +
                " (ID: " + updated.getId() + ")");

        return updated;
    }

    /**
     * Видалити період за ID
     * Каскадно видалить всю пов'язану зброю
     */
    public void deletePeriod(Long id) {
        Period period = periodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Період з ID " + id + " не знайдено!"));

        System.out.println("[SERVICE] Видалення періоду: " + period.getPeriodName() +
                " (ID: " + period.getId() + ")");

        periodRepository.deleteById(id);
    }
}