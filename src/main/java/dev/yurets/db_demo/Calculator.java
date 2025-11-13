package dev.yurets.db_demo;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

/**
 * Клас для обчислення математичних виразів
 */
public class Calculator {

    /**
     * Обчислює математичний вираз
     * @param expression - рядок з виразом, наприклад "2+2*2"
     * @return результат обчислення
     * @throws IllegalArgumentException якщо вираз некоректний
     */
    public double calculate(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Expression cannot be empty");
        }

        try {
            Expression expr = new ExpressionBuilder(expression).build();
            return expr.evaluate();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid expression: " + expression, e);
        }
    }

    /**
     * Перевіряє чи коректний вираз
     * @param expression - рядок з виразом
     * @return true якщо вираз можна обчислити
     */
    public boolean isValidExpression(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            return false;
        }
        try {
            Expression expr = new ExpressionBuilder(expression).build();
            double result = expr.evaluate();
            // Перевіряємо чи результат не NaN та не Infinity
            return !Double.isNaN(result) && !Double.isInfinite(result);
        } catch (Exception e) {
            return false;
        }
    }
}