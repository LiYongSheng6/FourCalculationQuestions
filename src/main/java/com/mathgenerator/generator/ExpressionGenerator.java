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
            // 修改这里：确保至少有1个运算操作数（2-3之间的随机数）
            int operatorCount = random.nextInt(2) + 2;  // 这样会生成2或3
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
        if (operatorCount == 0) {
            return generateNumber();
        }
        
        // 生成简单的线性表达式
        StringBuilder expression = new StringBuilder(generateNumber());
        for (int i = 0; i < operatorCount; i++) {
            char operator = generateOperator();
            String nextNumber = generateNumber();
            
            // 检查减法和除法的特殊情况
            try {
                Fraction leftValue = ExpressionEvaluator.evaluate(expression.toString());
                Fraction rightValue = ExpressionEvaluator.evaluate(nextNumber);
                
                if (operator == '-' && leftValue.compareTo(rightValue) < 0) {
                    // 如果左值小于右值，重新生成一个较小的右值
                    continue;
                }
                
                if (operator == '÷') {
                    // 确保除法结果是真分数
                    if (leftValue.compareTo(rightValue) >= 0 || rightValue.getNumerator() == 0) {
                        continue;
                    }
                }
                
                expression.append(" ").append(operator).append(" ").append(nextNumber);
                
            } catch (Exception e) {
                i--; // 重试这个运算符
                continue;
            }
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

