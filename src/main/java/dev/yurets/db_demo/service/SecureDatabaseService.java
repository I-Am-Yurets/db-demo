package dev.yurets.db_demo.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Arrays; // Для діагностики
import java.util.ArrayList;
import java.time.LocalDate; // Потрібно для роботи з датами
import java.sql.Date;       // Потрібно для передачі дати в JDBC
/**
 * Безпечний сервіс для роботи з БД через PreparedStatement
 * Захищено від SQL Injection
 */
@Service
public class SecureDatabaseService {

    private final JdbcTemplate jdbcTemplate;

    public SecureDatabaseService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Виконує команду безпечно через PreparedStatement
     */
    public Map<String, Object> executeCommand(String command) {
        try {
            // Перевірка на небезпечні команди
            validateCommand(command);

            CommandParser parser = new CommandParser(command);

            switch (parser.getOperation()) {
                case "read":
                    return executeRead(parser);
                case "insert":
                    return executeInsert(parser);
                case "update":
                    return executeUpdate(parser);
                case "delete":
                    return executeDelete(parser);
                default:
                    throw new IllegalArgumentException("Unknown operation: " + parser.getOperation());
            }
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }

    /**
     * Перевіряє команду на небезпечні SQL ключові слова
     */
    private void validateCommand(String command) throws IllegalArgumentException {
        String upperCommand = command.toUpperCase();

        // Список заборонених команд
        String[] dangerousKeywords = {
                "DROP", "TRUNCATE", "CASCADE", "ALTER", "CREATE TABLE",
                "GRANT", "REVOKE", "EXEC", "EXECUTE", "SHUTDOWN",
                "--", "/*", "*/", "XP_", "SP_", "INFORMATION_SCHEMA"
        };

        for (String keyword : dangerousKeywords) {
            if (upperCommand.contains(keyword)) {
                throw new IllegalArgumentException(
                        "Security violation: Command contains forbidden keyword '" + keyword + "'. " +
                                "Only INSERT, READ, UPDATE, DELETE operations are allowed."
                );
            }
        }
    }

    private Map<String, Object> executeRead(CommandParser parser) {
        String table = parser.getTableName();
        String id = parser.getParameter("id");

        String sql = "SELECT * FROM " + table + " WHERE id = ?";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, Integer.parseInt(id));

        return Map.of(
                "isSelect", true,
                "results", results,
                "columns", results.isEmpty() ? List.of() : results.get(0).keySet()
        );
    }

    private Map<String, Object> executeInsert(CommandParser parser) {
        String table = parser.getTableName();
        Map<String, String> params = parser.getParameters();

        int rows = switch (table) {
            case "countries" -> insertCountry(params);
            case "periods" -> insertPeriod(params);
            case "weapons" -> insertWeapon(params);
            default -> throw new IllegalArgumentException("Unknown table: " + table);
        };

        return Map.of(
                "isSelect", false,
                "rowsAffected", rows
        );
    }

    // --- ОНОВЛЕНО: insertCountry (З підтримкою опціонального ID) ---
    private int insertCountry(Map<String, String> params) {
        String idStr = params.get("id");
        String name = params.get("name");
        long totalAid = params.containsKey("total_aid_usd")
                ? Long.parseLong(params.get("total_aid_usd")) : 0;

        if (idStr != null && !idStr.isEmpty()) {
            // Вставка з явним ID
            String sql = "INSERT INTO countries (id, name, total_aid_usd) VALUES (?, ?, ?)";
            List<Object> values = List.of(Integer.parseInt(idStr), name, totalAid);
            return jdbcTemplate.update(sql, values.toArray());
        } else {
            // Вставка з автоінкрементом
            String sql = "INSERT INTO countries (name, total_aid_usd) VALUES (?, ?)";
            return jdbcTemplate.update(sql, name, totalAid);
        }
    }


    private int insertPeriod(Map<String, String> params) {

        // 1. Зчитування всіх параметрів
        String idStr = params.get("id"); // Опціональний ID, якщо користувач хоче задати його вручну

        // Обов'язкові поля
        String countryIdStr = params.get("country_id");
        String periodName = params.get("period_name");
        String startDateStr = params.get("start_date");

        // Необов'язкові поля
        String endDateStr = params.get("end_date");
        String aidAmountStr = params.getOrDefault("aid_amount_usd", "0");

        // 2. Коректне перетворення типів

        // Перетворення дат на java.sql.Date
        Date startDate = Date.valueOf(LocalDate.parse(startDateStr));
        Date endDate = (endDateStr != null && !endDateStr.isEmpty())
                ? Date.valueOf(LocalDate.parse(endDateStr))
                : null; // Передаємо null, якщо поле пусте

        int countryId = Integer.parseInt(countryIdStr);
        long aidAmount = Long.parseLong(aidAmountStr);

        // 3. Динамічна генерація SQL (залежно від наявності ID)

        if (idStr != null && !idStr.isEmpty()) {
            // Якщо ID заданий (вставка з явним ключем)
            String sql = "INSERT INTO periods (id, country_id, period_name, start_date, end_date, aid_amount_usd) VALUES (?, ?, ?, ?, ?, ?)";
            List<Object> values = new ArrayList<>();

            values.add(Integer.parseInt(idStr)); // ID ПЕРШИМ
            values.add(countryId);
            values.add(periodName);
            values.add(startDate);
            values.add(endDate);
            values.add(aidAmount);

            return jdbcTemplate.update(sql, values.toArray());

        } else {
            // Якщо ID не заданий (використовуємо автоінкремент)
            String sql = "INSERT INTO periods (country_id, period_name, start_date, end_date, aid_amount_usd) VALUES (?, ?, ?, ?, ?)";

            return jdbcTemplate.update(sql, countryId, periodName, startDate, endDate, aidAmount);
        }
    }

    // --- ОНОВЛЕНО: insertWeapon (З підтримкою опціонального ID) ---
    private int insertWeapon(Map<String, String> params) {
        String idStr = params.get("id"); // Опціональний ID

        int periodId = Integer.parseInt(params.get("period_id"));
        String weaponType = params.get("weapon_type");
        String weaponName = params.get("weapon_name");

        int quantity = params.containsKey("quantity") ? Integer.parseInt(params.get("quantity")) : 0;
        long unitCost = params.containsKey("unit_cost_usd") ? Long.parseLong(params.get("unit_cost_usd")) : 0;
        long totalCost = params.containsKey("total_cost_usd") ? Long.parseLong(params.get("total_cost_usd")) : 0;

        if (idStr != null && !idStr.isEmpty()) {
            // Вставка з явним ID
            String sql = "INSERT INTO weapons (id, period_id, weapon_type, weapon_name, quantity, unit_cost_usd, total_cost_usd) VALUES (?, ?, ?, ?, ?, ?, ?)";
            List<Object> values = List.of(
                    Integer.parseInt(idStr), periodId, weaponType, weaponName, quantity, unitCost, totalCost
            );
            return jdbcTemplate.update(sql, values.toArray());
        } else {
            // Вставка з автоінкрементом
            String sql = "INSERT INTO weapons (period_id, weapon_type, weapon_name, quantity, unit_cost_usd, total_cost_usd) VALUES (?, ?, ?, ?, ?, ?)";
            return jdbcTemplate.update(sql, periodId, weaponType, weaponName, quantity, unitCost, totalCost);
        }
    }


    private Map<String, Object> executeUpdate(CommandParser parser) {
        String table = parser.getTableName();
        Map<String, String> params = parser.getParameters();

        if (!params.containsKey("id")) {
            throw new IllegalArgumentException("Update requires 'id' parameter");
        }

        int id = Integer.parseInt(params.get("id"));

        // 1. Визначаємо список полів для оновлення, виключаючи 'id'
        List<String> updateFields = new ArrayList<>(params.keySet());
        updateFields.remove("id");

        if (updateFields.isEmpty()) {
            return Map.of("isSelect", false, "rowsAffected", 0);
        }

        // 2. Сортуємо поля, щоб забезпечити фіксований порядок, незалежний від HashMap.
        // Це забезпечить, що SQL-рядок завжди буде однаковим: field_a = ?, field_b = ?, ...
        Collections.sort(updateFields); // <<=== ФІКСУЄМО ПОРЯДОК

        // 3. Ініціалізуємо масив параметрів. Розмір = (Кількість полів) + 1 (для ID)
        int fieldsToUpdateCount = updateFields.size();
        Object[] values = new Object[fieldsToUpdateCount + 1];

        // Build UPDATE dynamically and safely
        StringBuilder sql = new StringBuilder("UPDATE " + table + " SET ");

// 4. Генеруємо SQL та заповнюємо масив values у фіксованому порядку
        for (int i = 0; i < fieldsToUpdateCount; i++) {
            String field = updateFields.get(i);
            String value = params.get(field);

            if (i > 0) sql.append(", ");

            sql.append(field).append(" = ?");
            // ЗМІНА ТУТ: передаємо і ключ, і значення
            values[i] = parseValue(field, value);
        }

        // 5. Додаємо WHERE частину та ID в кінець масиву values
        sql.append(" WHERE id = ?");
        values[fieldsToUpdateCount] = id; // ID додається останнім

        // Діагностика (дуже корисна для перевірки)
        System.out.println("Generated SQL: " + sql.toString());
        System.out.println("Parameters order: " + Arrays.toString(values));

        // 6. Виконання запиту
        int rows = jdbcTemplate.update(sql.toString(), values);

        return Map.of(
                "isSelect", false,
                "rowsAffected", rows
        );
    }

    // Потрібні імпорти:

    private Map<String, Object> executeDelete(CommandParser parser) {
        String table = parser.getTableName();
        String id = parser.getParameter("id");

        String sql = "DELETE FROM " + table + " WHERE id = ?";
        int rows = jdbcTemplate.update(sql, Integer.parseInt(id));

        return Map.of(
                "isSelect", false,
                "rowsAffected", rows
        );
    }

    private Object parseValue(String key, String value) {
        // Якщо поле є датою, спробувати перетворити його на java.sql.Date
        if (key.endsWith("_date")) {
            try {
                if (value == null || value.isEmpty()) {
                    return null;
                }
                // Припускаємо формат YYYY-MM-DD
                return Date.valueOf(LocalDate.parse(value));
            } catch (Exception e) {
                // Якщо парсинг дати не вдався, або DB не підтримує NULL, продовжуємо як рядок
                return value;
            }
        }

        // Інакше — стандартна логіка: спробувати Long, інакше залишити String
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return value;
        }
    }
}