package dev.yurets.db_demo.controller;

import dev.yurets.db_demo.Calculator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/calculator")
    public String calculatorForm() {
        return "calculator";
    }

    @GetMapping("/calculate")
    public String calculate(@RequestParam String expr, Model model) {
        try {
            Calculator calculator = new Calculator();
            double result = calculator.calculate(expr);
            model.addAttribute("expr", expr);
            model.addAttribute("result", result);
            return "result";
        } catch (Exception e) {
            return "error";
        }
    }
}