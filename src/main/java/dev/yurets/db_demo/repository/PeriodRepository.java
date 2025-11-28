package dev.yurets.db_demo.repository;

import dev.yurets.db_demo.model.Period;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Репозиторій для роботи з періодами допомоги
 */
public interface PeriodRepository extends JpaRepository<Period, Long> {

    List<Period> findAllByOrderByIdAsc();

    // Знайти всі періоди для конкретної країни
    List<Period> findByCountryId(Long countryId);
}
