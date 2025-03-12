package com.mathgenerator.generator;

import com.mathgenerator.model.Fraction;
import com.mathgenerator.service.ExpressionEvaluator;

import java.util.Random;
import java.util.ArrayList;

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
        StringBuilder expression = new StringBuilder();
        ArrayList<String> numbers = new ArrayList<>();
        ArrayList<Character> operators = new ArrayList<>();
        
        // 首先生成第一个数
        numbers.add(generateNumber());
        Fraction currentResult = ExpressionEvaluator.evaluate(numbers.get(0));
        
        // 记录连续的运算符
        char lastOperator = ' ';
        int consecutiveCount = 0;
        
        // 生成剩余的数字和运算符
        for (int i = 0; i < operatorCount; i++) {
            char operator;
            String nextNumber;
            boolean validCombination = false;
            
            int maxAttempts = 10;
            int attempts = 0;
            
            while (!validCombination && attempts < maxAttempts) {
                operator = generateOperator();
                
                // 避免连续使用相同的运算符超过两次
                if (operator == lastOperator) {
                    consecutiveCount++;
                    if (consecutiveCount >= 2) {
                        attempts++;
                        continue;
                    }
                } else {
                    consecutiveCount = 0;
                }
                
                nextNumber = generateNumber();
                
                try {
                    Fraction nextValue = ExpressionEvaluator.evaluate(nextNumber);
                    Fraction tempResult;
                    
                    // 根据运算符进行特殊处理
                    switch (operator) {
                        case '-':
                            // 确保减法后的结果为正数且不太接近0
                            if (currentResult.compareTo(nextValue) <= 0 || 
                                currentResult.subtract(nextValue).getNumerator() < currentResult.getNumerator() / 4) {
                                attempts++;
                                continue;
                            }
                            tempResult = currentResult.subtract(nextValue);
                            break;
                        case '×':
                            // 更严格地限制乘法结果的大小
                            tempResult = currentResult.multiply(nextValue);
                            if (tempResult.getNumerator() > range || 
                                (nextValue.getDenominator() != 1 && currentResult.getDenominator() != 1)) {
                                // 避免两个分数相乘
                                attempts++;
                                continue;
                            }
                            break;
                        case '÷':
                            // 确保除数不为0且结果为合适的分数
                            if (nextValue.getNumerator() == 0 || 
                                currentResult.compareTo(nextValue) >= 0 ||
                                nextValue.getDenominator() > range/2) {
                                attempts++;
                                continue;
                            }
                            tempResult = currentResult.divide(nextValue);
                            break;
                        default: // 加法
                            tempResult = currentResult.add(nextValue);
                            // 限制加法结果不要过大
                            if (tempResult.getNumerator() > range * 2) {
                                attempts++;
                                continue;
                            }
                            break;
                    }
                    
                    if (isValidResult(tempResult)) {
                        currentResult = tempResult;
                        operators.add(operator);
                        numbers.add(nextNumber);
                        lastOperator = operator;
                        validCombination = true;
                    }
                } catch (Exception e) {
                    attempts++;
                    continue;
                }
                attempts++;
            }
            
            if (!validCombination) {
                // 如果多次尝试都失败，减少运算符数量重新生成
                return generateSimpleExpression(Math.max(1, operatorCount - 1));
            }
        }
        
        // 构建最终表达式
        expression.append(numbers.get(0));
        for (int i = 0; i < operators.size(); i++) {
            expression.append(" ").append(operators.get(i)).append(" ").append(numbers.get(i + 1));
        }
        
        return expression.toString();
    }

    /**
     * 生成一个随机数（整数或分数）
     * @return 生成的数字字符串
     */
    private String generateNumber() {
        int maxRange = Math.min(range, 9); // 确保最大范围不超过9

        if (random.nextBoolean()) {
            // 生成1到maxRange的整数
            return Integer.toString(random.nextInt(maxRange) + 1);
        } else {
            // 生成真分数或带分数，但概率降低
            if (random.nextInt(3) != 0) { // 66.7%的概率生成整数
                return Integer.toString(random.nextInt(maxRange) + 1);
            }
            
            int numerator = random.nextInt(maxRange - 1) + 1; // 分子范围1到maxRange-1
            int denominator = random.nextInt(maxRange - 1) + 2; // 分母范围2到maxRange，确保分母至少为2
            
            if (numerator >= denominator) {
                // 生成带分数，确保分子小于分母且整数部分较小
                int whole = Math.min(2, numerator / denominator); // 限制整数部分最大为2
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
        if (result.getNumerator() < 0 || result.getDenominator() <= 0) {
            return false;
        }
        
        // 检查结果是否过大
        if (result.getNumerator() > range * range) {
            return false;
        }
        
        // 检查结果是否为真分数（如果不是整数）
        if (result.getDenominator() != 1) {
            return Math.abs(result.getNumerator()) < Math.abs(result.getDenominator());
        }
        
        return true;
    }

    /**
     * 标准化表达式，使相同的题目具有相同的形式
     * @param expression 原始表达式
     * @return 标准化后的表达式
     */
    public String normalizeExpression(String expression) {
        // 分割表达式
        String[] parts = expression.split(" ");
        StringBuilder result = new StringBuilder();
        
        // 对于加法和乘法，对操作数排序
        for (int i = 0; i < parts.length; i++) {
            if (i % 2 == 1) { // 运算符位置
                if (parts[i].equals("+") || parts[i].equals("×")) {
                    // 比较前后两个操作数，确保较小的在前
                    String prev = parts[i-1];
                    String next = parts[i+1];
                    try {
                        Fraction prevF = ExpressionEvaluator.evaluate(prev);
                        Fraction nextF = ExpressionEvaluator.evaluate(next);
                        if (prevF.compareTo(nextF) > 0) {
                            // 交换操作数
                            parts[i-1] = next;
                            parts[i+1] = prev;
                        }
                    } catch (Exception e) {
                        // 如果无法比较，保持原样
                    }
                }
            }
            result.append(parts[i]);
            if (i < parts.length - 1) {
                result.append(" ");
            }
        }
        return result.toString();
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

