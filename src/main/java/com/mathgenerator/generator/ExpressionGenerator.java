package com.mathgenerator.generator;

import com.mathgenerator.model.Fraction;
import com.mathgenerator.service.ExpressionEvaluator;

import java.util.Random;

/**
 * @author redmi k50 ultra
 * * @date 2025/3/11
 */
public class ExpressionGenerator {
    private final Random random;
    private final int range;

    public ExpressionGenerator(int range) {
        this.random = new Random();
        this.range = range;
    }

    public ExpressionResult generateExpression() {
        String expression;
        Fraction result;
        
        do {
            // 随机生成1到3个运算符
            int operatorCount = random.nextInt(3) + 1; // 确保至少有1个运算符
            expression = generateSimpleExpression(operatorCount);
            try {
                result = ExpressionEvaluator.evaluate(expression);
                // 确保结果是有效的（不是负数且是真分数）
                if (isValidResult(result)) {
                    break;
                }
            } catch (Exception e) {
                continue;
            }
        } while (true);

        return new ExpressionResult(expression, result);
    }

    private String generateSimpleExpression(int operatorCount) {
        // 生成第一个数
        StringBuilder expression = new StringBuilder(generateNumber());
        
        // 确保至少生成一个运算符和一个操作数
        char operator = generateOperator();
        String nextNumber = generateNumber();
        
        // 检查第一个运算是否有效
        try {
            Fraction leftValue = ExpressionEvaluator.evaluate(expression.toString());
            Fraction rightValue = ExpressionEvaluator.evaluate(nextNumber);
            
            // 处理减法和除法的特殊情况
            while ((operator == '-' && leftValue.compareTo(rightValue) < 0) ||
                   (operator == '÷' && (leftValue.compareTo(rightValue) >= 0 || rightValue.getNumerator() == 0))) {
                nextNumber = generateNumber();
                rightValue = ExpressionEvaluator.evaluate(nextNumber);
            }
            
            expression.append(" ").append(operator).append(" ").append(nextNumber);
            
            // 继续生成剩余的运算符和操作数
            for (int i = 1; i < operatorCount; i++) {
                operator = generateOperator();
                nextNumber = generateNumber();
                
                leftValue = ExpressionEvaluator.evaluate(expression.toString());
                rightValue = ExpressionEvaluator.evaluate(nextNumber);
                
                // 处理减法和除法的特殊情况
                while ((operator == '-' && leftValue.compareTo(rightValue) < 0) ||
                       (operator == '÷' && (leftValue.compareTo(rightValue) >= 0 || rightValue.getNumerator() == 0))) {
                    nextNumber = generateNumber();
                    rightValue = ExpressionEvaluator.evaluate(nextNumber);
                }
                
                expression.append(" ").append(operator).append(" ").append(nextNumber);
            }
            
        } catch (Exception e) {
            // 如果出现异常，重新生成表达式
            return generateSimpleExpression(operatorCount);
        }
        
        return expression.toString();
    }

    private String generateNumber() {
        if (random.nextBoolean()) {
            // 生成1到range-1的整数
            return Integer.toString(random.nextInt(range - 1) + 1);
        } else {
            // 生成真分数
            int numerator = random.nextInt(range - 1) + 1;
            int denominator = random.nextInt(range - 1) + 1;
            if (numerator >= denominator) {
                // 如果分子大于等于分母，转换为带分数
                int whole = numerator / denominator;
                numerator = numerator % denominator;
                if (numerator == 0) {
                    return Integer.toString(whole);
                }
                return whole + "'" + numerator + "/" + denominator;
            }
            return numerator + "/" + denominator;
        }
    }

    private char generateOperator() {
        char[] operators = {'+', '-', '×', '÷'};
        return operators[random.nextInt(operators.length)];
    }

    private boolean isValidResult(Fraction result) {
        // 检查结果是否为负数
        if (result.getNumerator() < 0 || result.getDenominator() < 0) {
            return false;
        }
        // 检查结果是否为真分数（如果不是整数）
        if (result.getDenominator() != 1) {
            return Math.abs(result.getNumerator()) < Math.abs(result.getDenominator());
        }
        return true;
    }

    public static class ExpressionResult {
        private final String expression;
        private final Fraction result;

        public ExpressionResult(String expression, Fraction result) {
            this.expression = expression;
            this.result = result;
        }

        public String getExpression() {
            return expression;
        }

        public Fraction getResult() {
            return result;
        }
    }
}

