package dev.yurets.db_demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

@DisplayName("Тести калькулятора")
public class CalculatorTests {

    @Test
    @DisplayName("Успішний тест: 1+1=2")
    void testCorrect() {
        Expression expr = new ExpressionBuilder("1+1").build();
        double result = expr.evaluate();
        assertEquals(2.0, result, "1+1 має дорівнювати 2");
    }

    //@Test
    //@DisplayName("Неуспішний тест: 5-1 не дорівнює 2 (очікується помилка)")
    //void testFail() {
    //    Expression expr = new ExpressionBuilder("5-1").build();
    //    double result = expr.evaluate();
    //    assertEquals(2.0, result, "5-1 не дорівнює 2, тест повинен впасти");
    //}
}