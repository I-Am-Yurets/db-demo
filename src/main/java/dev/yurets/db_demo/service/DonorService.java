package dev.yurets.db_demo.service;

import dev.yurets.db_demo.model.Country;
import dev.yurets.db_demo.model.Donor;
import dev.yurets.db_demo.repository.CountryRepository;
import dev.yurets.db_demo.repository.DonorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Сервіс для роботи з донорами
 */
@Slf4j
@Service
@Transactional
public class DonorService {

    private final DonorRepository donorRepository;
    private final CountryRepository countryRepository;

    public DonorService(DonorRepository donorRepository, CountryRepository countryRepository) {
        this.donorRepository = donorRepository;
        this.countryRepository = countryRepository;
    }

    public List<Donor> getAllDonors() {
        return donorRepository.findAllByOrderByIdAsc();
    }

    public Optional<Donor> getDonorById(Long id) {
        return donorRepository.findById(id);
    }

    public List<Donor> getDonorsByCountryId(Long countryId) {
        return donorRepository.findByCountryId(countryId);
    }

    public void createDonor(String organizationName, String organizationType,
                            String contactInfo, Long countryId) {
        // Валідація
        if (organizationName == null || organizationName.trim().isEmpty()) {
            throw new IllegalArgumentException("Назва організації не може бути порожньою");
        }
        if (organizationType == null || organizationType.trim().isEmpty()) {
            throw new IllegalArgumentException("Тип організації не може бути порожнім");
        }
        if (countryId == null) {
            throw new IllegalArgumentException("Країна обов'язкова");
        }

        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Країну з ID " + countryId + " не знайдено!"));

        Donor donor = new Donor(organizationName.trim(), organizationType.trim(), contactInfo, country);
        Donor saved = donorRepository.save(donor);

        log.info("Створено донора: {} (тип: {}, ID: {})",
                saved.getOrganizationName(), saved.getOrganizationType(), saved.getId());
    }

    public void updateDonor(Long id, String organizationName, String organizationType,
                            String contactInfo, Long countryId) {
        Donor donor = donorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Донора з ID " + id + " не знайдено!"));

        // Валідація
        if (organizationName == null || organizationName.trim().isEmpty()) {
            throw new IllegalArgumentException("Назва організації не може бути порожньою");
        }
        if (organizationType == null || organizationType.trim().isEmpty()) {
            throw new IllegalArgumentException("Тип організації не може бути порожнім");
        }
        if (countryId == null) {
            throw new IllegalArgumentException("Країна обов'язкова");
        }

        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Країну з ID " + countryId + " не знайдено!"));

        donor.setOrganizationName(organizationName.trim());
        donor.setOrganizationType(organizationType.trim());
        donor.setContactInfo(contactInfo);
        donor.setCountry(country);

        Donor updated = donorRepository.save(donor);
        log.info("Оновлено донора: {} (ID: {})", updated.getOrganizationName(), updated.getId());
    }

    public void deleteDonor(Long id) {
        Donor donor = donorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Донора з ID " + id + " не знайдено!"));

        log.info("Видалення донора: {} (ID: {})", donor.getOrganizationName(), donor.getId());
        donorRepository.deleteById(id);
    }
}