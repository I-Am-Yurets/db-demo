package dev.yurets.db_demo.service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Парсер команд для безпечного виконання CRUD операцій
 * Формат: operation table(field1='value1', field2='value2')
 *
 * Приклади:
 * insert countries(name='Poland', total_aid_usd='3000000000')
 * read countries(id='1')
 * update countries(id='1', name='USA Updated')
 * delete countries(id='1')
 */
public class CommandParser {

    private String operation;
    private String tableName;
    private Map<String, String> parameters;

    public CommandParser(String command) throws IllegalArgumentException {
        parseCommand(command);
    }

    private void parseCommand(String command) throws IllegalArgumentException {
        if (command == null || command.trim().isEmpty()) {
            throw new IllegalArgumentException("Command cannot be empty");
        }

        command = command.trim();

        // Regex pattern: operation table(param1='value1', param2='value2')
        Pattern pattern = Pattern.compile("^(insert|read|update|delete)\\s+(\\w+)\\((.+)\\)$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);

        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                    "Invalid command format. Expected: operation table(field='value', ...)\n" +
                            "Example: insert countries(name='Poland', total_aid_usd='3000000000')"
            );
        }

        this.operation = matcher.group(1).toLowerCase();
        this.tableName = matcher.group(2).toLowerCase();
        String paramsString = matcher.group(3);

        this.parameters = parseParameters(paramsString);

        validateCommand();
    }

    private Map<String, String> parseParameters(String paramsString) throws IllegalArgumentException {
        Map<String, String> params = new HashMap<>();

        // Split by comma, but not inside quotes
        String[] parts = paramsString.split(",(?=(?:[^']*'[^']*')*[^']*$)");

        for (String part : parts) {
            part = part.trim();

            // Parse key='value'
            Pattern paramPattern = Pattern.compile("^(\\w+)\\s*=\\s*'([^']*)'$");
            Matcher paramMatcher = paramPattern.matcher(part);

            if (!paramMatcher.matches()) {
                throw new IllegalArgumentException(
                        "Invalid parameter format: " + part + "\n" +
                                "Expected format: field='value'"
                );
            }

            String key = paramMatcher.group(1);
            String value = paramMatcher.group(2);

            params.put(key, value);
        }

        return params;
    }

    private void validateCommand() throws IllegalArgumentException {
        // Validate table name
        if (!tableName.equals("countries") && !tableName.equals("periods") && !tableName.equals("weapons")) {
            throw new IllegalArgumentException(
                    "Invalid table name: " + tableName + "\n" +
                            "Valid tables: countries, periods, weapons"
            );
        }

        // Validate operation-specific requirements
        switch (operation) {
            case "read":
            case "delete":
                if (!parameters.containsKey("id")) {
                    throw new IllegalArgumentException(operation + " requires 'id' parameter");
                }
                break;
            case "insert":
                validateInsertParameters();
                break;
            case "update":
                if (!parameters.containsKey("id")) {
                    throw new IllegalArgumentException("update requires 'id' parameter");
                }
                break;
        }
    }

    private void validateInsertParameters() throws IllegalArgumentException {
        switch (tableName) {
            case "countries":
                if (!parameters.containsKey("name")) {
                    throw new IllegalArgumentException("insert into countries requires 'name' parameter");
                }
                break;
            case "periods":
                if (!parameters.containsKey("country_id") || !parameters.containsKey("period_name")
                        || !parameters.containsKey("start_date")) {
                    throw new IllegalArgumentException(
                            "insert into periods requires: country_id, period_name, start_date"
                    );
                }
                break;
            case "weapons":
                if (!parameters.containsKey("period_id") || !parameters.containsKey("weapon_type")
                        || !parameters.containsKey("weapon_name")) {
                    throw new IllegalArgumentException(
                            "insert into weapons requires: period_id, weapon_type, weapon_name"
                    );
                }
                break;
        }
    }

    // Getters
    public String getOperation() {
        return operation;
    }

    public String getTableName() {
        return tableName;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public boolean hasParameter(String key) {
        return parameters.containsKey(key);
    }

    @Override
    public String toString() {
        return "CommandParser{" +
                "operation='" + operation + '\'' +
                ", tableName='" + tableName + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}