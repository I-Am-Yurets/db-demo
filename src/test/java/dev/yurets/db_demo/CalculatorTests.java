package dev.yurets.db_demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тести калькулятора")
public class CalculatorTests {

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator(); // Створюємо новий калькулятор перед кожним тестом
    }

    @Test
    @DisplayName("Успішний тест: 1+1=2")
    void testCorrect() {
        double result = calculator.calculate("1+1");
        assertEquals(2.0, result, "1+1 має дорівнювати 2");
    }

    //@Test
    //@DisplayName("Неуспішний тест: 5-1 не дорівнює 2 (очікується помилка)")
    //void testFail() {
    //    double result = calculator.calculate("5-1");
    //    assertEquals(2.0, result, "5-1 не дорівнює 2, тест повинен впасти");
    //}

    // === Додаткові тести (можна закоментувати, якщо не потрібні) ===

    @Test
    @DisplayName("Віднімання: 10-3=7")
    void testSubtraction() {
        double result = calculator.calculate("10-3");
        assertEquals(7.0, result);
    }

    @Test
    @DisplayName("Множення: 4*5=20")
    void testMultiplication() {
        double result = calculator.calculate("4*5");
        assertEquals(20.0, result);
    }

    @Test
    @DisplayName("Порожній вираз викидає виняток")
    void testEmptyExpression() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculate("");
        }, "Порожній вираз має викинути виняток");
    }

    @Test
    @DisplayName("Перевірка валідності виразу")
    void testIsValidExpression() {
        assertTrue(calculator.isValidExpression("2+2"));
        assertTrue(calculator.isValidExpression("10*5"));
        assertFalse(calculator.isValidExpression(""));
        assertFalse(calculator.isValidExpression(null));
        assertFalse(calculator.isValidExpression("abc"));
    }
}