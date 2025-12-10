package dev.yurets.db_demo.repository;

import dev.yurets.db_demo.model.AidRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Репозиторій для роботи із запитами на допомогу
 */
public interface AidRequestRepository extends JpaRepository<AidRequest, Long> {

    List<AidRequest> findAllByOrderByRequestDateDesc();

    // Знайти всі запити конкретної країни-отримувача
    List<AidRequest> findByRequestingCountryId(Long countryId);

    // Знайти всі запити до конкретної країни-донора
    List<AidRequest> findByDonorCountryId(Long countryId);

    // Знайти запити за статусом
    List<AidRequest> findByStatus(String status);

    // Знайти запити за пріоритетом
    List<AidRequest> findByPriority(String priority);

    // Знайти запити за періодом
    List<AidRequest> findByPeriodId(Long periodId);

    // Знайти запити за статусом та пріоритетом
    List<AidRequest> findByStatusAndPriority(String status, String priority);
}