package com.mathgenerator.service;

import com.mathgenerator.model.Fraction;

import java.util.Stack;

/**
 * @author redmi k50 ultra
 * * @date 2025/3/11
 */
/**
 * 表达式计算器类
 * 用于计算四则运算表达式的结果
 */
public class ExpressionEvaluator {
    /**
     * 计算表达式的值
     * @param expression 要计算的表达式
     * @return 计算结果（分数形式）
     */
    public static Fraction evaluate(String expression) {
        Stack<Fraction> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (Character.isDigit(c) || c == '\'') {
                StringBuilder sb = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '\'' || expression.charAt(i) == '/')) {
                    sb.append(expression.charAt(i));
                    i++;
                }
                i--;
                numbers.push(parseFraction(sb.toString()));
            } else if (c == '(') {
                operators.push(c);
            } else if (c == ')') {
                while (operators.peek() != '(') {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.pop();
            } else if (isOperator(c)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(c);
            }
        }
        while (!operators.isEmpty()) {
            numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
        }
        return numbers.pop();
    }

    /**
     * 解析分数字符串
     * @param s 分数字符串
     * @return Fraction对象
     */
    private static Fraction parseFraction(String s) {
        if (s.contains("'")) {
            String[] parts = s.split("'");
            int whole = Integer.parseInt(parts[0]);
            String[] fractionParts = parts[1].split("/");
            int numerator = Integer.parseInt(fractionParts[0]);
            int denominator = Integer.parseInt(fractionParts[1]);
            return new Fraction(whole * denominator + numerator, denominator);
        } else if (s.contains("/")) {
            String[] parts = s.split("/");
            int numerator = Integer.parseInt(parts[0]);
            int denominator = Integer.parseInt(parts[1]);
            return new Fraction(numerator, denominator);
        } else {
            return new Fraction(Integer.parseInt(s), 1);
        }
    }

    /**
     * 执行运算操作
     * @param operator 运算符
     * @param b 第二个操作数
     * @param a 第一个操作数
     * @return 运算结果
     */
    private static Fraction applyOperation(char operator, Fraction b, Fraction a) {
        switch (operator) {
            case '+':
                return a.add(b);
            case '-':
                return a.subtract(b);
            case '×':
                return a.multiply(b);
            case '÷':
                return a.divide(b);
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '×' || c == '÷';
    }

    private static int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '×':
            case '÷':
                return 2;
            default:
                return 0;
        }
    }
}