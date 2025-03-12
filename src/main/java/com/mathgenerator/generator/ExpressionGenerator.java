package com.mathgenerator.generator;

import com.mathgenerator.model.Fraction;
import com.mathgenerator.service.ExpressionEvaluator;

import java.util.Random;

/**
 * 表达式生成器类
 * 用于生成四则运算表达式
 */
public class ExpressionGenerator {
    private final Random random;  // 随机数生成器
    private final int range;      // 数值范围上限

    /**
     * 构造函数
     * @param range 生成数字的范围上限
     */
    public ExpressionGenerator(int range) {
        this.random = new Random();
        this.range = range;
    }

    /**
     * 生成一个有效的四则运算表达式
     * @return 包含表达式和结果的ExpressionResult对象
     */
    public ExpressionResult generateExpression() {
        String expression;
        Fraction result;
        
        do {
            // 修改这里：生成1-3个运算符
            int operatorCount = random.nextInt(3) + 1;  // 这样会生成1,2,3
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

    /**
     * 生成简单的线性表达式
     * @param operatorCount 运算符的数量
     * @return 生成的表达式字符串
     */
    private String generateSimpleExpression(int operatorCount) {
        // 生成简单的线性表达式
        StringBuilder expression = new StringBuilder(generateNumber());
        int successfulOperators = 0;  // 跟踪成功添加的运算符数量
        
        while (successfulOperators < operatorCount) {
            char operator = generateOperator();
            String nextNumber = generateNumber();
            
            // 检查减法和除法的特殊情况
            try {
                Fraction leftValue = ExpressionEvaluator.evaluate(expression.toString());
                Fraction rightValue = ExpressionEvaluator.evaluate(nextNumber);
                
                if (operator == '-' && leftValue.compareTo(rightValue) < 0) {
                    // 如果左值小于右值，重试
                    continue;
                }
                
                if (operator == '÷') {
                    // 确保除法结果是真分数
                    if (leftValue.compareTo(rightValue) >= 0 || rightValue.getNumerator() == 0) {
                        continue;
                    }
                }
                
                expression.append(" ").append(operator).append(" ").append(nextNumber);
                successfulOperators++;  // 只有在成功添加运算符后才增加计数
                
            } catch (Exception e) {
                // 如果计算出错，继续尝试
                continue;
            }
        }
        
        return expression.toString();
    }

    /**
     * 生成一个随机数（整数或分数）
     * @return 生成的数字字符串
     */
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

    /**
     * 随机生成一个运算符
     * @return 运算符字符
     */
    private char generateOperator() {
        char[] operators = {'+', '-', '×', '÷'};
        return operators[random.nextInt(operators.length)];
    }

    /**
     * 验证计算结果是否有效
     * @param result 计算结果
     * @return 如果结果有效返回true，否则返回false
     */
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

    /**
     * 表达式结果内部类
     * 用于存储表达式及其计算结果
     */
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

