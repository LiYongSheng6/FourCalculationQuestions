package com.mathgenerator.generator;

import com.mathgenerator.model.Fraction;
import com.mathgenerator.service.ExpressionEvaluator;
import com.mathgenerator.service.ExpressionNormalizer;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

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
            // 生成1-3个运算符
            int operatorCount = random.nextInt(3) + 1;  // 这样会生成1,2,3
            expression = generateSimpleExpression(operatorCount);
            try {
                result = ExpressionEvaluator.evaluate(expression);
                // 确保结果是有效的（不是负数且是真分数）
                if (isValidResult(result)) {
                    break;
                }
            } catch (Exception e) {
                System.out.println("生成的表达式无效：" + expression);
            }
        } while (true);

        // 新增检查，确保最终结果不为负数
        if (result.getNumerator() < 0||result.getDenominator() < 0) {
            return generateExpression(); // 重新生成表达式
        }

        // 添加计算过程的注释
        StringBuilder process = new StringBuilder();
        process.append("// ").append(expression).append("\n");
        process.append("// = ").append(result).append("\n");

        return new ExpressionResult(expression, result);
    }

    /**
     * 生成简单的线性表达式
     * @param operatorCount 运算符的数量
     * @return 生成的表达式字符串
     */
    private String generateSimpleExpression(int operatorCount) {
        // 首先生成一个基本的四则运算表达式
        String expression = generateNormalExpression(operatorCount);
        
        // 只有当运算符数量大于1（确保有足够的操作数）且随机概率为50%时才添加括号
        if (operatorCount > 1 && random.nextBoolean()) {
            expression = addParentheses(expression);
        }
        
        return expression;
    }

    /**
     * 在表达式中添加括号
     * 只在加减法运算中添加括号，确保括号内至少包含一个加减运算符
     * @param expression 原始表达式
     * @return 添加括号后的表达式
     */
    private String addParentheses(String expression) {
        // 按空格分割表达式，得到数字和运算符
        String[] parts = expression.split(" ");
        
        // 找到所有加减法运算符的位置
        List<Integer> addSubOperatorPositions = new ArrayList<>();
        for (int i = 0; i < parts.length; i++) {
            // 只收集加法和减法运算符的位置
            if (parts[i].equals("+") || parts[i].equals("-")) {
                addSubOperatorPositions.add(i);
            }
        }
        
        // 如果没有找到加减法运算符，则返回原表达式
        if (addSubOperatorPositions.isEmpty()) {
            return expression;
        }
        
        // 从所有加减法运算符位置中随机选择一个
        int operatorPos = addSubOperatorPositions.get(random.nextInt(addSubOperatorPositions.size()));
        
        // 使用StringBuilder构建新的带括号的表达式
        StringBuilder result = new StringBuilder();
        
        // 遍历表达式的每个部分，在适当位置添加括号
        for (int i = 0; i < parts.length; i++) {
            // 在加减法运算符前一个操作数前添加左括号
            if (i == operatorPos - 1) {
                result.append("( ");
            }
            // 添加当前部分（数字或运算符）
            result.append(parts[i]).append(" ");
            
            // 在运算符后一个操作数后添加右括号
            if (i == operatorPos + 1) {
                // 如果后面还有运算符，在当前操作数后添加右括号
                if (i + 1 < parts.length && (parts[i + 1].equals("+") || parts[i + 1].equals("-") || 
                    parts[i + 1].equals("×") || parts[i + 1].equals("÷"))) {
                    result.append(") ");
                } 
                // 如果是表达式的最后一个操作数，直接添加右括号
                else if (i == parts.length - 1) {
                    result.append(")");
                }
            }
        }
        
        // 确保括号正确闭合
        String resultStr = result.toString().trim();
        // 如果左括号数量大于右括号数量，在末尾添加右括号
        if (countChar(resultStr, '(') > countChar(resultStr, ')')) {
            resultStr += " )";
        }
        
        return resultStr;
    }

    /**
     * 计算字符串中特定字符的出现次数
     * @param str 要检查的字符串
     * @param target 要计数的目标字符
     * @return 目标字符在字符串中的出现次数
     */
    private int countChar(String str, char target) {
        return (int) str.chars().filter(ch -> ch == target).count();
    }

    // 重命名原来的生成逻辑为generateNormalExpression
    private String generateNormalExpression(int operatorCount) {
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
                            // 新增检查，确保中间结果不为负数
                            if (tempResult.getNumerator() < 0) {
                                attempts++;
                                continue;
                            }
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
                            // 新增检查，确保中间结果不为负数
                            if (tempResult.getNumerator() < 0) {
                                attempts++;
                                continue;
                            }
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
     * 验证括号的完整性和正确性
     * @param expression 要验证的表达式
     * @return 如果括号使用正确返回true，否则返回false
     */
    private boolean isValidParentheses(String expression) {
        int count = 0;
        boolean hasLeft = false;
        boolean hasRight = false;
        
        // 检查括号是否配对且位置正确
        for (char c : expression.toCharArray()) {
            if (c == '(') {
                count++;
                hasLeft = true;
            } else if (c == ')') {
                count--;
                hasRight = true;
            }
            // 如果count小于0，说明右括号在左括号之前出现
            if (count < 0) {
                return false;
            }
        }
        
        // 确保括号是成对的，且表达式中确实包含了一对括号
        return count == 0 && hasLeft && hasRight;
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
    public boolean isValidResult(Fraction result) {
        // 检查结果是否为负数
        if (result.getNumerator() < 0 || result.getDenominator() <= 0) {
            return false;
        }
        
        // 检查结果是否过大
        if (result.getNumerator() > range * range) {
            return false;
        }
        
        // 检查结果是否为真分数（如果不是整数）
        if (result.getDenominator() != 1 && 
            Math.abs(result.getNumerator()) >= Math.abs(result.getDenominator())) {
            return false;
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
                if ("+".equals(parts[i]) || "×".equals(parts[i])) {
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
     * 表达式树节点类
     */
    private abstract class ExpressionNode {
        String value;  // 数字或运算符
        ExpressionNode left;
        ExpressionNode right;
        boolean needParentheses;
        
        public ExpressionNode(String value) {
            this.value = value;
        }
        
        @Override
        public abstract String toString();
    }

    /**
     * 数字节点类
     */
    private class NumberNode extends ExpressionNode {
        public NumberNode(String value) {
            super(value);
        }
        
        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * 运算符节点类
     */
    private class OperatorNode extends ExpressionNode {
        String operator;
        
        public OperatorNode(String operator) {
            super(operator);
            this.operator = operator;
        }
        
        @Override
        public String toString() {
            if (left == null && right == null) {
                return operator;
            }
            
            String leftStr = left.toString();
            String rightStr = right.toString();
            
            // 根据运算符优先级决定是否需要括号
            if (needParentheses) {
                return "(" + leftStr + " " + operator + " " + rightStr + ")";
            } else {
                return leftStr + " " + operator + " " + rightStr;
            }
        }
    }

    /**
     * 生成表达式树
     * @param depth 剩余深度
     * @return 表达式树的根节点
     */
    private ExpressionNode generateExpressionTree(int depth) {
        if (depth == 0 || random.nextInt(3) == 0) {
            // 生成叶子节点（数字）
            return new NumberNode(generateNumber());
        }
        
        // 生成运算符节点
        OperatorNode node = new OperatorNode(String.valueOf(generateOperator()));
        
        // 生成左右子树
        node.left = generateExpressionTree(depth - 1);
        node.right = generateExpressionTree(depth - 1);
        
        try {
            // 验证当前子树的计算结果
            String subExpression = node.toString();
            if (!hasValidParentheses(subExpression)) {
                return generateExpressionTree(depth);
            }
            
            Fraction result = ExpressionEvaluator.evaluate(subExpression);
            
            // 如果子树结果无效，重新生成
            if (!isValidResult(result)) {
                return generateExpressionTree(depth);
            }
        } catch (Exception e) {
            // 如果计算出错，重新生成
            return generateExpressionTree(depth);
        }
        
        return node;
    }

    /**
     * 判断当前运算符是否比子节点运算符优先级低
     */
    private boolean isLowerPrecedence(String current, ExpressionNode child) {
        if (!(child instanceof OperatorNode)) return false;
        
        OperatorNode childOp = (OperatorNode)child;
        int currentPrecedence = getOperatorPrecedence(current);
        int childPrecedence = getOperatorPrecedence(childOp.operator);
        
        return currentPrecedence < childPrecedence;
    }

    /**
     * 获取运算符优先级
     */
    private int getOperatorPrecedence(String operator) {
        switch (operator) {
            case "+":
            case "-":
                return 1;
            case "×":
            case "÷":
                return 2;
            default:
                return 0;
        }
    }

    /**
     * 验证表达式是否有效
     * @param expression 要验证的表达式
     * @return 如果表达式有效返回true，否则返回false
     */
    private boolean isValidExpression(String expression) {
        try {
            // 检查括号是否匹配且合法
            if (!hasValidParentheses(expression)) {
                return false;
            }

            // 如果包含括号，先检查括号内的计算结果
            if (expression.contains("(")) {
                int start = expression.indexOf("(");
                int end = expression.lastIndexOf(")");
                if (start >= 0 && end >= 0) {
                    String subExpr = expression.substring(start + 1, end).trim();
                    Fraction subResult = ExpressionEvaluator.evaluate(subExpr);
                    // 确保括号内的计算结果也是有效的
                    if (!isValidResult(subResult)) {
                        return false;
                    }
                }
            }

            // 检查整个表达式的结果
            Fraction result = ExpressionEvaluator.evaluate(expression);
            return isValidResult(result);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查括号是否合法
     */
    private boolean hasValidParentheses(String expression) {
        int count = 0;
        boolean hasContent = false;
        
        // 不允许表达式以右括号开始或左括号结束
        if (expression.trim().startsWith(")") || expression.trim().endsWith("(")) {
            return false;
        }
        
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '(') {
                count++;
                // 检查左括号后是否有内容
                hasContent = false;
            } else if (c == ')') {
                count--;
                // 如果括号内没有内容，返回false
                if (!hasContent) {
                    return false;
                }
            } else if (!Character.isWhitespace(c)) {
                hasContent = true;
            }
            
            // 如果count小于0，说明右括号多于左括号
            if (count < 0) {
                return false;
            }
        }
        
        // count应该为0，表示括号配对
        return count == 0;
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

