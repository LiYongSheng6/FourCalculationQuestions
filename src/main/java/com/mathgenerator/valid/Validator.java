package com.mathgenerator.valid;

import java.util.Stack;

/**
 * @author redmi k50 ultra
 * * @date 2025/3/11
 */
public class Validator {

    // 验证表达式是否合法
    public static boolean validateExpression(String expression) {
        // 检查括号是否匹配
        if (!checkParentheses(expression)) {
            return false;
        }

        // 检查运算符是否合法
        if (!checkOperators(expression)) {
            return false;
        }

        // 检查表达式是否包含负数
        if (expression.contains("-")) {
            return false;
        }

        return true;
    }

    // 检查括号是否匹配
    private static boolean checkParentheses(String expression) {
        Stack<Character> stack = new Stack<>();
        for (char c : expression.toCharArray()) {
            if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                if (stack.isEmpty() || stack.pop() != '(') {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }

    // 检查运算符是否合法
    private static boolean checkOperators(String expression) {
        char[] operators = {'+', '-', '×', '÷'};
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (isOperator(c)) {
                // 检查运算符前后是否有空格
                if (i == 0 || i == expression.length() - 1 || expression.charAt(i - 1) != ' ' || expression.charAt(i + 1) != ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    // 检查字符是否为运算符
    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '×' || c == '÷';
    }

    // 检查表达式是否包含负数
    private static boolean checkNegativeNumbers(String expression) {
        String[] tokens = expression.split(" ");
        for (String token : tokens) {
            if (token.startsWith("-")) {
                return false;
            }
        }
        return true;
    }
}