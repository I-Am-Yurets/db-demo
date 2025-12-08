package dev.yurets.db_demo.repository;

import dev.yurets.db_demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторій для роботи з користувачами
 */
public interface UserRepository extends JpaRepository<User, Long> {

    // Пошук користувача за логіном (для аутентифікації)
    Optional<User> findByUsername(String username);
}