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
@RequestMapping("/operations/periods")
public class PeriodsController {

    private final JdbcTemplate jdbcTemplate;

    public PeriodsController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public String periodsCrud() {
        return "periods-crud";
    }

    @PostMapping("/execute")
    public String execute(
            @RequestParam String action,
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String country_id,
            @RequestParam(required = false) String period_name,
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String end_date,
            @RequestParam(required = false) String aid_amount_usd,
            Model model) {

        System.out.println("=== PERIODS CRUD EXECUTE START ===");
        System.out.println("Action: " + action);

        try {
            String query = "";

            switch (action) {
                case "find":
                    if (id == null || id.isEmpty()) {
                        throw new IllegalArgumentException("Please enter ID for Find operation");
                    }
                    query = "SELECT * FROM periods WHERE id = " + id;
                    List<Map<String, Object>> results = jdbcTemplate.queryForList(query);

                    model.addAttribute("query", query);
                    model.addAttribute("results", results);
                    model.addAttribute("isSelect", true);

                    if (!results.isEmpty()) {
                        model.addAttribute("columns", results.get(0).keySet());
                    }

                    return "sql_result";

                case "add":
                    if (country_id == null || period_name == null || start_date == null) {
                        throw new IllegalArgumentException("Please fill required fields: Country ID, Period Name, Start Date");
                    }
                    query = "INSERT INTO periods (country_id, period_name, start_date, end_date, aid_amount_usd) VALUES ("
                            + country_id + ", '" + period_name + "', '" + start_date + "', "
                            + (end_date != null && !end_date.isEmpty() ? "'" + end_date + "'" : "NULL") + ", "
                            + (aid_amount_usd != null && !aid_amount_usd.isEmpty() ? aid_amount_usd : "0") + ")";
                    int added = jdbcTemplate.update(query);

                    model.addAttribute("query", query);
                    model.addAttribute("rowsAffected", added);
                    model.addAttribute("isSelect", false);

                    return "sql_result";

                case "update":
                    if (id == null || id.isEmpty()) {
                        throw new IllegalArgumentException("Please enter ID for Update operation");
                    }
                    query = "UPDATE periods SET country_id = " + country_id
                            + ", period_name = '" + period_name + "', start_date = '" + start_date + "', end_date = "
                            + (end_date != null && !end_date.isEmpty() ? "'" + end_date + "'" : "NULL")
                            + ", aid_amount_usd = " + (aid_amount_usd != null && !aid_amount_usd.isEmpty() ? aid_amount_usd : "0")
                            + " WHERE id = " + id;
                    int updated = jdbcTemplate.update(query);

                    model.addAttribute("query", query);
                    model.addAttribute("rowsAffected", updated);
                    model.addAttribute("isSelect", false);

                    return "sql_result";

                case "delete":
                    if (id == null || id.isEmpty()) {
                        throw new IllegalArgumentException("Please enter ID for Delete operation");
                    }
                    query = "DELETE FROM periods WHERE id = " + id;
                    int deleted = jdbcTemplate.update(query);

                    // Reset sequence after delete
                    try {
                        jdbcTemplate.execute("SELECT setval('periods_id_seq', (SELECT COALESCE(MAX(id), 0) FROM periods))");
                    } catch (Exception e) {
                        // Ignore sequence reset errors
                    }

                    model.addAttribute("query", query);
                    model.addAttribute("rowsAffected", deleted);
                    model.addAttribute("isSelect", false);

                    return "sql_result";

                default:
                    throw new IllegalArgumentException("Unknown action: " + action);
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();

            model.addAttribute("query", "Periods CRUD - Action: " + action);
            model.addAttribute("error", e.getMessage());

            return "sql_error";
        } finally {
            System.out.println("=== PERIODS CRUD EXECUTE END ===");
        }
    }
}