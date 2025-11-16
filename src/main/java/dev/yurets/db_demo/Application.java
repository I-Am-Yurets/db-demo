package dev.yurets.db_demo;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.TimeZone;

@SpringBootApplication
public class Application {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Bean
    public CommandLineRunner initDatabase(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                System.out.println("✔ Spring Boot connected to PostgreSQL!");

                jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS countries (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        total_aid_usd BIGINT DEFAULT 0
                    )
                """);

                jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS periods (
                        id SERIAL PRIMARY KEY,
                        country_id INT NOT NULL,
                        period_name VARCHAR(100) NOT NULL,
                        start_date DATE NOT NULL,
                        end_date DATE,
                        aid_amount_usd BIGINT DEFAULT 0,
                        FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE CASCADE
                    )
                """);

                jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS weapons (
                        id SERIAL PRIMARY KEY,
                        period_id INT NOT NULL,
                        weapon_type VARCHAR(100) NOT NULL,
                        weapon_name VARCHAR(200) NOT NULL,
                        quantity INT DEFAULT 0,
                        unit_cost_usd BIGINT DEFAULT 0,
                        total_cost_usd BIGINT DEFAULT 0,
                        FOREIGN KEY (period_id) REFERENCES periods(id) ON DELETE CASCADE
                    )
                """);

                System.out.println("✔ Tables created successfully!");

                Integer countUSA = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM countries WHERE name = 'USA'", Integer.class
                );

                if (countUSA == 0) {
                    jdbcTemplate.update(
                            "INSERT INTO countries (name, total_aid_usd) VALUES (?, ?)",
                            "USA", 75000000000L
                    );
                    jdbcTemplate.update(
                            "INSERT INTO countries (name, total_aid_usd) VALUES (?, ?)",
                            "Germany", 28000000000L
                    );
                    jdbcTemplate.update(
                            "INSERT INTO countries (name, total_aid_usd) VALUES (?, ?)",
                            "United Kingdom", 15000000000L
                    );
                    System.out.println("✔ Test data inserted!");
                }

            } catch (Exception e) {
                System.err.println("Error during database initialization!");
                e.printStackTrace();
            }
        };
    }
}