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
@RequestMapping("/operations/countries")
public class CountriesController {

    private final JdbcTemplate jdbcTemplate;

    public CountriesController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public String countriesCrud() {
        return "countries-crud";
    }

    @PostMapping("/execute")
    public String execute(
            @RequestParam String action,
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String total_aid_usd,
            Model model) {

        System.out.println("=== COUNTRIES CRUD EXECUTE START ===");
        System.out.println("Action: " + action);
        System.out.println("ID: " + id);

        try {
            String query = "";

            switch (action) {
                case "find":
                    if (id == null || id.isEmpty()) {
                        throw new IllegalArgumentException("Please enter ID for Find operation");
                    }
                    // SQL Injection vulnerable - concatenation
                    query = "SELECT * FROM countries WHERE id = " + id;
                    List<Map<String, Object>> results = jdbcTemplate.queryForList(query);

                    model.addAttribute("query", query);
                    model.addAttribute("results", results);
                    model.addAttribute("isSelect", true);

                    if (!results.isEmpty()) {
                        model.addAttribute("columns", results.get(0).keySet());
                    }

                    System.out.println("Find results: " + results.size());
                    return "sql_result";

                case "add":
                    if (name == null || name.isEmpty()) {
                        throw new IllegalArgumentException("Please enter Name for Add operation");
                    }
                    // SQL Injection vulnerable - concatenation
                    query = "INSERT INTO countries (name, total_aid_usd) VALUES ('"
                            + name + "', " + (total_aid_usd != null && !total_aid_usd.isEmpty() ? total_aid_usd : "0") + ")";
                    int added = jdbcTemplate.update(query);

                    model.addAttribute("query", query);
                    model.addAttribute("rowsAffected", added);
                    model.addAttribute("isSelect", false);

                    System.out.println("Rows added: " + added);
                    return "sql_result";

                case "update":
                    if (id == null || id.isEmpty()) {
                        throw new IllegalArgumentException("Please enter ID for Update operation");
                    }
                    // SQL Injection vulnerable - concatenation
                    query = "UPDATE countries SET name = '" + name
                            + "', total_aid_usd = " + (total_aid_usd != null && !total_aid_usd.isEmpty() ? total_aid_usd : "0")
                            + " WHERE id = " + id;
                    int updated = jdbcTemplate.update(query);

                    model.addAttribute("query", query);
                    model.addAttribute("rowsAffected", updated);
                    model.addAttribute("isSelect", false);

                    System.out.println("Rows updated: " + updated);
                    return "sql_result";

                case "delete":
                    if (id == null || id.isEmpty()) {
                        throw new IllegalArgumentException("Please enter ID for Delete operation");
                    }
                    // SQL Injection vulnerable - concatenation
                    query = "DELETE FROM countries WHERE id = " + id;
                    int deleted = jdbcTemplate.update(query);

                    // Reset sequence after delete
                    try {
                        jdbcTemplate.execute("SELECT setval('countries_id_seq', (SELECT COALESCE(MAX(id), 0) FROM countries))");
                    } catch (Exception e) {
                        // Ignore sequence reset errors
                    }

                    model.addAttribute("query", query);
                    model.addAttribute("rowsAffected", deleted);
                    model.addAttribute("isSelect", false);

                    System.out.println("Rows deleted: " + deleted);
                    return "sql_result";

                default:
                    throw new IllegalArgumentException("Unknown action: " + action);
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();

            model.addAttribute("query", "Countries CRUD - Action: " + action);
            model.addAttribute("error", e.getMessage());

            return "sql_error";
        } finally {
            System.out.println("=== COUNTRIES CRUD EXECUTE END ===");
        }
    }
}