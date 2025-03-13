package com.mathgenerator.service;

import java.util.ArrayList;
import java.util.List;

import static com.mathgenerator.constant.GlobalConstant.*;


/**
 * @author: YiLaiL
 * @date: 2025/03/13
 * @description: 标准化表达式
 */
public class ExpressionNormalizer {

    /**
     * 标准化四则运算表达式，使相同的题目具有相同的形式，目标是在不改变表达式意思的情况下，
     * 将式子的大小尽量由小到大排序，以达到统一标准去重的目的，不能改变其答案。
     *
     * @param expression 原始表达式
     * @return 标准化后的表达式
     */
    public String normalizeExpression(String expression) {
        // 1. 分词
        List<String> tokens = tokenize(expression);

        // 2. 转换为逆波兰表达式 (考虑运算符优先级)
        List<String> rpn = shuntingYard(tokens);

        // 3. 表达式树 (便于操作)
        BaseExpressionNode root = buildExpressionTree(rpn);

        // 4. 标准化表达式树 (递归排序)
        normalizeNode(root);

        // 5. 转换为字符串
        return root.toString();
    }

    /**
     * 分词器，将表达式分割成 token 列表
     *
     * @param expression 表达式
     * @return token 列表
     */
    public List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        for (char c : expression.toCharArray()) {
            if (Character.isWhitespace(c)) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
            } else if (isOperator(String.valueOf(c))) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
                tokens.add(String.valueOf(c));
            } else {
                currentToken.append(c);
            }
        }
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }
        return tokens;
    }

    /**
     * Shunting Yard 算法，将中缀表达式转换为逆波兰表达式
     *
     * @param tokens token 列表
     * @return 逆波兰表达式列表
     */
    public List<String> shuntingYard(List<String> tokens) {
        // 输出队列
        List<String> outputQueue = new ArrayList<>();
        // 运算符栈
        List<String> operatorStack = new ArrayList<>();

        for (String token : tokens) {
            // 判断是否为数字
            if (isNumber(token)) {
                outputQueue.add(token);
                // 判断是否为操作符
            } else if (isOperator(token)) {
                // 操作符栈不为空且栈顶元素优先级大于等于当前元素，则将栈顶元素弹出并加入输出队列
                while (!operatorStack.isEmpty() && hasHigherPrecedence(operatorStack.get(operatorStack.size() - 1), token)) {
                    outputQueue.add(operatorStack.remove(operatorStack.size() - 1));
                }
                operatorStack.add(token);
            } else if (LEFT_BRACKET.equals(token)) {
                operatorStack.add(token);
            } else if (RIGHT_BRACKET.equals(token)) {
                // 将栈顶元素弹出并加入输出队列，直到遇到左括号
                while (!operatorStack.isEmpty() && !LEFT_BRACKET.equals(operatorStack.get(operatorStack.size() - 1))) {
                    outputQueue.add(operatorStack.remove(operatorStack.size() - 1));
                }
                // 将 "(" 从操作符栈中弹出
                operatorStack.remove(operatorStack.size() - 1);
            }
        }

        while (!operatorStack.isEmpty()) {
            outputQueue.add(operatorStack.remove(operatorStack.size() - 1));
        }
        return outputQueue;
    }

    /**
     * 构建表达式树
     *
     * @param rpn 逆波兰表达式
     * @return 表达式树的根节点
     */
    public BaseExpressionNode buildExpressionTree(List<String> rpn) {
        List<BaseExpressionNode> stack = new ArrayList<>();
        for (String token : rpn) {
            if (isOperator(token)) {
                BaseExpressionNode right = stack.remove(stack.size() - 1);
                BaseExpressionNode left = stack.remove(stack.size() - 1);
                OperatorNodeBase operatorNodeBase = new OperatorNodeBase(token, left, right);
                stack.add(operatorNodeBase);
                setFrontOperator(operatorNodeBase);
            } else {
                stack.add(new NumberNodeBase(token));
            }
        }
        return stack.get(0);
    }


    private void setFrontOperator(OperatorNodeBase node) {
        if (node.left instanceof OperatorNodeBase) {
            ((OperatorNodeBase) node.left).frontOperator = node.operator;
        }
        if (node.right instanceof OperatorNodeBase) {
            ((OperatorNodeBase) node.right).frontOperator = node.operator;
        }
    }

    /**
     * 递归标准化表达式树
     *
     * @param node 当前节点
     */
    private void normalizeNode(BaseExpressionNode node) {
        if (node instanceof OperatorNodeBase) {
            OperatorNodeBase operatorNode = (OperatorNodeBase) node;
            normalizeNode(operatorNode.left);
            normalizeNode(operatorNode.right);

            if (PLUS.equals(operatorNode.operator) || MULTIPLICATION.equals(operatorNode.operator)) {
                // 交换律
                if (compareNodes(operatorNode.left, operatorNode.right) > 0) {
                    BaseExpressionNode temp = operatorNode.left;
                    operatorNode.left = operatorNode.right;
                    operatorNode.right = temp;
                }
            }
        }
    }

    /**
     * 比较两个节点的大小
     *
     * @param a 节点 a
     * @param b 节点 b
     * @return 比较结果
     */
    private int compareNodes(BaseExpressionNode a, BaseExpressionNode b) {
        if (!(a instanceof NumberNodeBase) || !(b instanceof NumberNodeBase)) {
            return a.toString().compareTo(b.toString());
        }

        NumberNodeBase numNodeA = (NumberNodeBase) a;
        NumberNodeBase numNodeB = (NumberNodeBase) b;

        String[] partsA = numNodeA.value.split(APOSTROPHE);
        String[] partsB = numNodeB.value.split(APOSTROPHE);

        if (partsA.length != 2 || partsB.length != 2) {
            return Integer.compare(partsA.length, partsB.length);
        }

        int integerA = Integer.parseInt(partsA[0]);
        int integerB = Integer.parseInt(partsB[0]);

        int integerComparison = Integer.compare(integerA, integerB);
        if (integerComparison != 0) {
            return integerComparison;
        }

        double fractionA = parseFraction(partsA[1]);
        double fractionB = parseFraction(partsB[1]);

        return Double.compare(fractionA, fractionB);
    }

    private double parseFraction(String fractionString) {
        try {
            return Double.parseDouble(fractionString);
        } catch (NumberFormatException e) {
            String[] fractionParts = fractionString.split(SEMICOLON);
            if (fractionParts.length == 2) {
                double numerator = Double.parseDouble(fractionParts[0]);
                double denominator = Double.parseDouble(fractionParts[1]);
                return numerator / denominator;
            }
            throw new IllegalArgumentException("Invalid fraction format: " + fractionString, e);
        }
    }

    private boolean isOperator(String token) {
        return PLUS.equals(token) || MINUS.equals(token) || MULTIPLICATION.equals(token) || DIVISION.equals(token);
    }

    private boolean isNumber(String token) {
        boolean containsDigit = false;
        for (int i = 0; i < token.length(); i++) {
            if (Character.isDigit(token.charAt(i))) {
                containsDigit = true;
                break;
            }
        }
        return containsDigit;
    }

    /**
     * 判断是否有更高的优先级操作符
     *
     * @param op1 操作符1 操作符栈弹出的操作符
     * @param op2 操作符2 传入的操作符
     * @return 是否有更高的优先级操作符
     */
    private boolean hasHigherPrecedence(String op1, String op2) {
        return !("(".equals(op2) || ")".equals(op2)) && precedence(op1) >= precedence(op2);
    }

    private int precedence(String operator) {
        switch (operator) {
            case PLUS:
            case MINUS:
                return 1;
            case MULTIPLICATION:
            case DIVISION:
                return 2;
            default:
                return 0;
        }
    }

    /**
     * 表达式树节点抽象类
     */
    public abstract static class BaseExpressionNode {
        /**
         * 转化字符串
         *
         * @return 转化后的字符串
         */
        @Override
        public abstract String toString();
    }

    /**
     * 操作符节点
     */
    private static class OperatorNodeBase extends BaseExpressionNode {
        String operator;
        BaseExpressionNode left;
        BaseExpressionNode right;
        String frontOperator;

        public OperatorNodeBase(String operator, BaseExpressionNode left, BaseExpressionNode right) {
            this.operator = operator;
            this.left = left;
            this.right = right;
        }

        @Override
        public String toString() {
            if ((PLUS.equals(operator) || MINUS.equals(operator)) &&
                    (MULTIPLICATION.equals(frontOperator) || DIVISION.equals(frontOperator))) {
                return "(" + left.toString() + " " + operator + " " + right.toString() + ")";
            } else {
                return left.toString() + " " + operator + " " + right.toString();
            }
        }
    }

    /**
     * 数字节点
     */
    private static class NumberNodeBase extends BaseExpressionNode {
        String value;

        public NumberNodeBase(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}