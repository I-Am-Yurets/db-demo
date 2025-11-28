package dev.yurets.db_demo.repository;

import dev.yurets.db_demo.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Репозиторій для роботи з країнами
 * JpaRepository автоматично надає методи: save, findById, findAll, deleteById
 */
public interface CountryRepository extends JpaRepository<Country, Long> {

    // Spring Data JPA автоматично згенерує SQL:
    // "SELECT * FROM countries ORDER BY id ASC"
    List<Country> findAllByOrderByIdAsc();

    // Пошук країни за назвою
    Country findByName(String name);
}
