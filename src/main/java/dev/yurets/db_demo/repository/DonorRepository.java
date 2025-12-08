package dev.yurets.db_demo.repository;

import dev.yurets.db_demo.model.Donor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Репозиторій для роботи з донорами
 */
public interface DonorRepository extends JpaRepository<Donor, Long> {

    List<Donor> findAllByOrderByIdAsc();

    // Знайти всіх донорів конкретної країни
    List<Donor> findByCountryId(Long countryId);
}