package dev.yurets.db_demo.controller.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Кореневий REST API контролер
 * Показує інформацію про доступні endpoints
 */
@RestController
@RequestMapping("/api")
public class ApiRootController {

    /**
     * GET /api
     * Відображає список доступних REST API endpoints
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> apiInfo() {
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("message", "Military Aid System - REST API");
        response.put("version", "1.0.0");
        response.put("status", "operational");

        // Інформація про авторизацію
        Map<String, String> auth = new LinkedHashMap<>();
        auth.put("type", "Basic Authentication");
        auth.put("admin", "admin:admin (повний доступ)");
        auth.put("user", "user:user (тільки GET запити)");
        response.put("authentication", auth);

        // Список endpoints (групуємо по ресурсах)
        Map<String, List<Map<String, String>>> endpoints = new LinkedHashMap<>();

        // Countries
        List<Map<String, String>> countries = new ArrayList<>();
        countries.add(createEndpoint("GET", "/api/countries", "Отримати всі країни", "USER, ADMIN"));
        countries.add(createEndpoint("GET", "/api/countries/{id}", "Отримати країну за ID", "USER, ADMIN"));
        countries.add(createEndpoint("POST", "/api/countries", "Створити країну", "ADMIN"));
        countries.add(createEndpoint("PUT", "/api/countries/{id}", "Оновити країну", "ADMIN"));
        countries.add(createEndpoint("DELETE", "/api/countries/{id}", "Видалити країну", "ADMIN"));
        endpoints.put("countries", countries);

        // Periods
        List<Map<String, String>> periods = new ArrayList<>();
        periods.add(createEndpoint("GET", "/api/periods", "Отримати всі періоди", "USER, ADMIN"));
        periods.add(createEndpoint("GET", "/api/periods/{id}", "Отримати період за ID", "USER, ADMIN"));
        periods.add(createEndpoint("POST", "/api/periods", "Створити період", "ADMIN"));
        periods.add(createEndpoint("PUT", "/api/periods/{id}", "Оновити період", "ADMIN"));
        periods.add(createEndpoint("DELETE", "/api/periods/{id}", "Видалити період", "ADMIN"));
        endpoints.put("periods", periods);

        // Weapons
        List<Map<String, String>> weapons = new ArrayList<>();
        weapons.add(createEndpoint("GET", "/api/weapons", "Отримати всю зброю", "USER, ADMIN"));
        weapons.add(createEndpoint("GET", "/api/weapons/{id}", "Отримати зброю за ID", "USER, ADMIN"));
        weapons.add(createEndpoint("POST", "/api/weapons", "Створити зброю", "ADMIN"));
        weapons.add(createEndpoint("PUT", "/api/weapons/{id}", "Оновити зброю", "ADMIN"));
        weapons.add(createEndpoint("DELETE", "/api/weapons/{id}", "Видалити зброю", "ADMIN"));
        endpoints.put("weapons", weapons);

        // Donors (NEW!)
        List<Map<String, String>> donors = new ArrayList<>();
        donors.add(createEndpoint("GET", "/api/donors", "Отримати всіх донорів", "USER, ADMIN"));
        donors.add(createEndpoint("GET", "/api/donors/{id}", "Отримати донора за ID", "USER, ADMIN"));
        donors.add(createEndpoint("POST", "/api/donors", "Створити донора", "ADMIN"));
        donors.add(createEndpoint("PUT", "/api/donors/{id}", "Оновити донора", "ADMIN"));
        donors.add(createEndpoint("DELETE", "/api/donors/{id}", "Видалити донора", "ADMIN"));
        endpoints.put("donors", donors);

        // Weapon Deliveries (NEW!)
        List<Map<String, String>> deliveries = new ArrayList<>();
        deliveries.add(createEndpoint("GET", "/api/deliveries", "Отримати всі поставки", "USER, ADMIN"));
        deliveries.add(createEndpoint("GET", "/api/deliveries/{id}", "Отримати поставку за ID", "USER, ADMIN"));
        deliveries.add(createEndpoint("GET", "/api/deliveries/weapon/{id}", "Поставки конкретної зброї", "USER, ADMIN"));
        deliveries.add(createEndpoint("GET", "/api/deliveries/donor/{id}", "Поставки конкретного донора", "USER, ADMIN"));
        deliveries.add(createEndpoint("GET", "/api/deliveries/status/{status}", "Поставки за статусом", "USER, ADMIN"));
        deliveries.add(createEndpoint("POST", "/api/deliveries", "Створити поставку", "ADMIN"));
        deliveries.add(createEndpoint("PUT", "/api/deliveries/{id}", "Оновити поставку", "ADMIN"));
        deliveries.add(createEndpoint("DELETE", "/api/deliveries/{id}", "Видалити поставку", "ADMIN"));
        endpoints.put("deliveries", deliveries);

        response.put("endpoints", endpoints);

        // Додаємо приклади використання
        Map<String, String> examples = new LinkedHashMap<>();
        examples.put("curl_get", "curl -u admin:admin http://localhost:8080/api/countries");
        examples.put("curl_post", "curl -u admin:admin -X POST http://localhost:8080/api/countries -H \"Content-Type: application/json\" -d '{\"name\":\"Poland\",\"totalAidUsd\":3500000000}'");
        examples.put("browser", "Відкрийте http://localhost:8080/api/countries в браузері після входу");
        response.put("examples", examples);

        return ResponseEntity.ok(response);
    }

    private Map<String, String> createEndpoint(String method, String path, String description, String access) {
        Map<String, String> endpoint = new LinkedHashMap<>();
        endpoint.put("method", method);
        endpoint.put("path", path);
        endpoint.put("description", description);
        endpoint.put("access", access);
        return endpoint;
    }
}