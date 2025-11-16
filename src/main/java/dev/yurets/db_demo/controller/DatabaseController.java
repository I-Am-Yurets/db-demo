package dev.yurets.db_demo.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class DatabaseController {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/database")
    public String database(Model model) {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            model.addAttribute("connected", true);
            return "database";
        } catch (Exception e) {
            model.addAttribute("connected", false);
            model.addAttribute("error", e.getMessage());
            return "database";
        }
    }

    @GetMapping("/db-tables")
    public String dbTables() {
        return "db-tables";
    }

    @GetMapping("/db-operations")
    public String dbOperations() {
        return "db-operations";
    }


    // Перегляд таблиць
    @GetMapping("/tables/countries")
    public String countriesTable(Model model) {
        try {
            List<Map<String, Object>> countries = jdbcTemplate.queryForList(
                    "SELECT * FROM countries ORDER BY id"
            );
            model.addAttribute("countries", countries);
            return "countries";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/tables/periods")
    public String periodsTable(Model model) {
        try {
            List<Map<String, Object>> periods = jdbcTemplate.queryForList(
                    "SELECT p.*, c.name as country_name FROM periods p " +
                            "LEFT JOIN countries c ON p.country_id = c.id ORDER BY p.id"
            );
            model.addAttribute("periods", periods);
            return "periods";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/tables/weapons")
    public String weaponsTable(Model model) {
        try {
            List<Map<String, Object>> weapons = jdbcTemplate.queryForList(
                    "SELECT w.*, p.period_name, c.name as country_name " +
                            "FROM weapons w " +
                            "LEFT JOIN periods p ON w.period_id = p.id " +
                            "LEFT JOIN countries c ON p.country_id = c.id " +
                            "ORDER BY w.id"
            );
            model.addAttribute("weapons", weapons);
            return "weapons";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}