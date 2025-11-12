package dev.yurets.db_demo;

import jakarta.annotation.PostConstruct;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@SpringBootApplication
@Controller
public class Application {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // ==== 1. Веб-калькулятор з Thymeleaf ====

    @RequestMapping("/")
    public String home() {
        return "home"; // повертає home.html
    }

    @GetMapping("/calculator")
    public String calculatorForm() {
        return "calculator"; // повертає calculator.html
    }

    @GetMapping("/calculate")
    public String calculate(@RequestParam String expr, Model model) {
        try {
            Expression expression = new ExpressionBuilder(expr).build();
            double result = expression.evaluate();

            model.addAttribute("expr", expr);
            model.addAttribute("result", result);

            return "result"; // повертає result.html
        } catch (Exception e) {
            return "error"; // повертає error.html
        }
    }

    // ==== 2. Робота з PostgreSQL через JdbcTemplate ====
    @Bean
    public CommandLineRunner demo(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                System.out.println("✔ Spring Boot connected to PostgreSQL!");

                jdbcTemplate.execute("""
                        CREATE TABLE IF NOT EXISTS demo_users (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(100)
                        )
                        """);
                System.out.println("✔ Table 'demo_users' is ready.");

                jdbcTemplate.update(
                        "INSERT INTO demo_users (name) VALUES (?)",
                        "Hello from Spring + JdbcTemplate!"
                );
                System.out.println("✔ Inserted 1 row.");

                List<Map<String, Object>> users = jdbcTemplate.queryForList("SELECT * FROM demo_users");
                System.out.println("📋 Table content:");
                users.forEach(user -> {
                    System.out.println(user.get("id") + " | " + user.get("name"));
                });

            } catch (Exception e) {
                System.err.println("Error during database operation!");
                e.printStackTrace();
            }
        };
    }
}