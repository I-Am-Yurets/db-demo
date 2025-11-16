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
@RequestMapping("/operations/weapons")
public class WeaponsController {

    private final JdbcTemplate jdbcTemplate;

    public WeaponsController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public String weaponsCrud() {
        return "weapons-crud";
    }

    @PostMapping("/execute")
    public String execute(
            @RequestParam String action,
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String period_id,
            @RequestParam(required = false) String weapon_type,
            @RequestParam(required = false) String weapon_name,
            @RequestParam(required = false) String quantity,
            @RequestParam(required = false) String unit_cost_usd,
            @RequestParam(required = false) String total_cost_usd,
            Model model) {

        System.out.println("=== WEAPONS CRUD EXECUTE START ===");
        System.out.println("Action: " + action);

        try {
            String query = "";

            switch (action) {
                case "find":
                    if (id == null || id.isEmpty()) {
                        throw new IllegalArgumentException("Please enter ID for Find operation");
                    }
                    query = "SELECT * FROM weapons WHERE id = " + id;
                    List<Map<String, Object>> results = jdbcTemplate.queryForList(query);

                    model.addAttribute("query", query);
                    model.addAttribute("results", results);
                    model.addAttribute("isSelect", true);

                    if (!results.isEmpty()) {
                        model.addAttribute("columns", results.get(0).keySet());
                    }

                    return "sql_result";

                case "add":
                    if (period_id == null || weapon_type == null || weapon_name == null) {
                        throw new IllegalArgumentException("Please fill required fields: Period ID, Weapon Type, Weapon Name");
                    }
                    query = "INSERT INTO weapons (period_id, weapon_type, weapon_name, quantity, unit_cost_usd, total_cost_usd) VALUES ("
                            + period_id + ", '" + weapon_type + "', '" + weapon_name + "', "
                            + (quantity != null && !quantity.isEmpty() ? quantity : "0") + ", "
                            + (unit_cost_usd != null && !unit_cost_usd.isEmpty() ? unit_cost_usd : "0") + ", "
                            + (total_cost_usd != null && !total_cost_usd.isEmpty() ? total_cost_usd : "0") + ")";
                    int added = jdbcTemplate.update(query);

                    model.addAttribute("query", query);
                    model.addAttribute("rowsAffected", added);
                    model.addAttribute("isSelect", false);

                    return "sql_result";

                case "update":
                    if (id == null || id.isEmpty()) {
                        throw new IllegalArgumentException("Please enter ID for Update operation");
                    }
                    query = "UPDATE weapons SET period_id = " + period_id
                            + ", weapon_type = '" + weapon_type + "', weapon_name = '" + weapon_name + "', quantity = "
                            + (quantity != null && !quantity.isEmpty() ? quantity : "0")
                            + ", unit_cost_usd = " + (unit_cost_usd != null && !unit_cost_usd.isEmpty() ? unit_cost_usd : "0")
                            + ", total_cost_usd = " + (total_cost_usd != null && !total_cost_usd.isEmpty() ? total_cost_usd : "0")
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
                    query = "DELETE FROM weapons WHERE id = " + id;
                    int deleted = jdbcTemplate.update(query);

                    // Reset sequence after delete
                    try {
                        jdbcTemplate.execute("SELECT setval('weapons_id_seq', (SELECT COALESCE(MAX(id), 0) FROM weapons))");
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

            model.addAttribute("query", "Weapons CRUD - Action: " + action);
            model.addAttribute("error", e.getMessage());

            return "sql_error";
        } finally {
            System.out.println("=== WEAPONS CRUD EXECUTE END ===");
        }
    }
}