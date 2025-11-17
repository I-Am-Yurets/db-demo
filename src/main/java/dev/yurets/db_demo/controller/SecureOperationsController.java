package dev.yurets.db_demo.controller;

import dev.yurets.db_demo.service.SecureDatabaseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Безпечний контролер з PreparedStatement
 * Для завдання 1.3.3
 */
@Controller
@RequestMapping("/secure")
public class SecureOperationsController {

    private final SecureDatabaseService dbService;

    public SecureOperationsController(SecureDatabaseService dbService) {
        this.dbService = dbService;
    }

    @GetMapping
    public String secureCommandForm() {
        return "secure-command";
    }

    @PostMapping("/execute")
    public String executeSecureCommand(@RequestParam String command, Model model) {
        System.out.println("=== SECURE COMMAND EXECUTE START ===");
        System.out.println("Command: " + command);

        try {
            Map<String, Object> result = dbService.executeCommand(command);

            // Перевірка чи є помилка
            if (result.containsKey("error")) {
                System.out.println("ERROR: " + result.get("error"));
                model.addAttribute("query", "Secure Command: " + command);
                model.addAttribute("error", result.get("error"));
                return "sql_error";
            }

            // Додаємо оригінальну команду
            model.addAttribute("query", "Secure Command (Protected): " + command);

            // Якщо це SELECT (read)
            if ((Boolean) result.get("isSelect")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> results = (List<Map<String, Object>>) result.get("results");

                model.addAttribute("results", results);
                model.addAttribute("isSelect", true);

                if (!results.isEmpty()) {
                    model.addAttribute("columns", result.get("columns"));
                }

                System.out.println("Results: " + results.size() + " row(s)");
            } else {
                // INSERT, UPDATE, DELETE
                model.addAttribute("rowsAffected", result.get("rowsAffected"));
                model.addAttribute("isSelect", false);

                System.out.println("Rows affected: " + result.get("rowsAffected"));
            }

            return "sql_result";

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();

            model.addAttribute("query", "Secure Command: " + command);
            model.addAttribute("error", e.getMessage());

            return "sql_error";
        } finally {
            System.out.println("=== SECURE COMMAND EXECUTE END ===");
        }
    }
}