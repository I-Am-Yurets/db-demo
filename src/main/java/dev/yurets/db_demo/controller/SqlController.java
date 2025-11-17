package dev.yurets.db_demo.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sql")
public class SqlController {

    private final JdbcTemplate jdbcTemplate;

    public SqlController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public String sqlForm() {
        System.out.println("=== SQL FORM OPENED ===");
        return "sql";
    }

    @PostMapping("/execute")
    public String executeSql(@RequestParam String query, Model model) {
        System.out.println("===========================================");
        System.out.println("=== SQL EXECUTE START ===");
        System.out.println("Query received: " + query);

        try {
            String upperQuery = query.trim().toUpperCase();

            if (upperQuery.startsWith("SELECT")) {
                System.out.println("Executing SELECT query...");
                List<Map<String, Object>> results = jdbcTemplate.queryForList(query);
                System.out.println("Results count: " + results.size());

                model.addAttribute("query", query);
                model.addAttribute("results", results);
                model.addAttribute("isSelect", true);

                if (!results.isEmpty()) {
                    model.addAttribute("columns", results.get(0).keySet());
                    System.out.println("Columns: " + results.get(0).keySet());
                }

                System.out.println("Returning: sql_result");
                System.out.println("===========================================");
                return "sql_result";
            } else {
                System.out.println("Executing non-SELECT query...");
                int rowsAffected = jdbcTemplate.update(query);
                System.out.println("Rows affected: " + rowsAffected);

                model.addAttribute("query", query);
                model.addAttribute("rowsAffected", rowsAffected);
                model.addAttribute("isSelect", false);

                System.out.println("Returning: sql_result");
                System.out.println("===========================================");
                return "sql_result";
            }
        } catch (Exception e) {
            System.out.println("!!! ERROR OCCURRED !!!");
            System.out.println("ERROR MESSAGE: " + e.getMessage());
            System.out.println("ERROR CLASS: " + e.getClass().getName());
            e.printStackTrace();

            model.addAttribute("query", query);
            model.addAttribute("error", e.getMessage());

            System.out.println("Returning: sql_error");
            System.out.println("===========================================");
            return "sql_error";
        }
    }
}