package com.mathgenerator.handler;

import com.mathgenerator.model.Fraction;
import com.mathgenerator.service.ExpressionEvaluator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.io.*;

/**
 * @author redmi k50 ultra
 * * @date 2025/3/11
 */
/**
 * 文件处理类
 * 负责处理题目和答案的文件读写操作
 */
public class FileHandler {

    /**
     * 将生成的题目写入文件（使用 UTF-8 编码）
     * @param expressions 题目集合
     * @param fileName 输出文件名
     */
    public static void writeExpressions(Map<String,Fraction> expressions, String fileName){
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(fileName)), StandardCharsets.UTF_8))) {
            int[] index = {1};
            expressions.forEach((expression,result) -> {
                try {
                    writer.write("题目" + index[0] + ": " + expression + " =\n");
                    index[0]++;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            System.err.println("写入题目文件时出错: " + e.getMessage());
        }
    }

    /**
     * 将答案写入文件（使用 UTF-8 编码）
     * 在写入答案前会重新计算表达式结果以确保准确性
     * @param expressions 题目和答案的映射集合
     * @param fileName 答案文件名
     */
    public static void writeAnswers(Map<String,Fraction> expressions, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(Files.newOutputStream(Paths.get(fileName)), StandardCharsets.UTF_8))) {
            
            final int[] index = {1};
            expressions.forEach((expression, result) -> {
                try {
                    // 重新计算表达式的结果
                    Fraction recalculatedResult;
                    try {
                        // 清理表达式：去除首尾空格和末尾等号
                        String cleanExpression = expression.trim();
                        if (cleanExpression.endsWith("=")) {
                            cleanExpression = cleanExpression.substring(0, cleanExpression.length() - 1).trim();
                        }
                        // 使用表达式计算器重新计算结果
                        recalculatedResult = ExpressionEvaluator.evaluate(cleanExpression);
                    } catch (Exception e) {
                        // 如果重新计算出错，使用原始结果
                        recalculatedResult = result;
                    }
                    
                    // 写入答案到文件
                    writer.write("答案" + index[0] + ": " + recalculatedResult + "\n");
                    index[0]++;
                } catch (IOException e) {
                    System.err.println("写入答案文件时出错: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            System.err.println("写入答案文件时出错: " + e.getMessage());
        }
    }

    /**
     * 对比答案文件并生成统计结果（使用 UTF-8 编码）
     * @param exerciseFile 练习题文件
     * @param answerFile 答案文件
     * @param gradeFile 成绩报告文件
     */
    public static void gradeAnswers(String exerciseFile, String answerFile, String gradeFile) {
        try (BufferedReader exerciseReader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(exerciseFile)), StandardCharsets.UTF_8));
             BufferedReader answerReader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(answerFile)), StandardCharsets.UTF_8));
             BufferedWriter gradeWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(gradeFile)), StandardCharsets.UTF_8))) {

            String exerciseLine, answerLine;
            int correctCount = 0, wrongCount = 0;
            StringBuilder correctIndices = new StringBuilder();
            StringBuilder wrongIndices = new StringBuilder();

            while ((exerciseLine = exerciseReader.readLine()) != null && (answerLine = answerReader.readLine()) != null) {
                String indexStr = exerciseLine.split(":")[0].replace("题目", "").replaceAll("[^0-9]", "");
                int index = Integer.parseInt(indexStr);

                try {
                    // 获取表达式（去掉末尾的等号）
                    String expression = exerciseLine.split(":")[1].trim();
                    expression = expression.substring(0, expression.length() - 1).trim();
                    
                    // 重新计算表达式结果
                    Fraction expectedResult = ExpressionEvaluator.evaluate(expression);
                    
                    // 获取答案文件中的结果
                    String actualResultStr = answerLine.split(":")[1].trim();
                    Fraction actualResult = new Fraction(actualResultStr);

                    if (expectedResult.equals(actualResult)) {
                        correctCount++;
                        correctIndices.append(index).append(", ");
                    } else {
                        wrongCount++;
                        wrongIndices.append(index).append(", ");
                    }
                } catch (Exception e) {
                    wrongCount++;
                    wrongIndices.append(index).append(", ");
                }
            }

            // 写入统计结果
            if (correctIndices.length() > 0) {
                gradeWriter.write("Correct: " + correctCount + " (" + correctIndices.substring(0, correctIndices.length() - 2) + ")\n");
            } else {
                gradeWriter.write("Correct: 0\n");
            }
            
            if (wrongIndices.length() > 0) {
                gradeWriter.write("Wrong: " + wrongCount + " (" + wrongIndices.substring(0, wrongIndices.length() - 2) + ")\n");
            } else {
                gradeWriter.write("Wrong: 0\n");
            }

        } catch (IOException e) {
            System.err.println("对比答案文件时出错: " + e.getMessage());
        }
    }
}