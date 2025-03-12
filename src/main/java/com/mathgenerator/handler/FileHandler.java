package com.mathgenerator.handler;

import com.mathgenerator.model.Fraction;
import com.mathgenerator.service.ExpressionEvaluator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
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
    public static void writeExpressions(Set<String> expressions, String fileName) throws FileNotFoundException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(fileName)), StandardCharsets.UTF_8))) {
            int index = 1;
            for (String expression : expressions) {
                writer.write("题目" + index + ": " + expression + " =\n");
                index++;
            }
        } catch (IOException e) {
            System.err.println("写入题目文件时出错: " + e.getMessage());
        }
    }

    /**
     * 将答案写入文件（使用 UTF-8 编码）
     * @param expressions 题目集合
     * @param fileName 答案文件名
     */
    public static void writeAnswers(Set<String> expressions, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(fileName)), StandardCharsets.UTF_8))) {
            int index = 1;
            for (String expression : expressions) {
                try {
                    Fraction result = ExpressionEvaluator.evaluate(expression);
                    writer.write("答案" + index + ": " + result + "\n");
                } catch (IllegalArgumentException e) {
                    writer.write("答案" + index + ": 错误 (除零或无效表达式)\n");
                    System.err.println("计算表达式时出错 - 题目 " + index + ": " + e.getMessage());
                }
                index++;
            }
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
        try (BufferedReader exerciseReader = new BufferedReader(new InputStreamReader(new FileInputStream(exerciseFile), StandardCharsets.UTF_8));
             BufferedReader answerReader = new BufferedReader(new InputStreamReader(new FileInputStream(answerFile), StandardCharsets.UTF_8));
             BufferedWriter gradeWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(gradeFile), StandardCharsets.UTF_8))) {

            String exerciseLine, answerLine;
            int correctCount = 0, wrongCount = 0;
            StringBuilder correctIndices = new StringBuilder();
            StringBuilder wrongIndices = new StringBuilder();

            while ((exerciseLine = exerciseReader.readLine()) != null && (answerLine = answerReader.readLine()) != null) {
                String indexStr = exerciseLine.split(":")[0].replace("题目", "").replaceAll("[^0-9]", "");
                int index = Integer.parseInt(indexStr);

                try {
                    String expression = exerciseLine.split("=")[0].trim();
                    Fraction expectedResult = ExpressionEvaluator.evaluate(expression);
                    
                    // 检查答案是否为错误标记
                    if (answerLine.contains("错误")) {
                        wrongCount++;
                        wrongIndices.append(index).append(", ");
                        continue;
                    }
                    
                    Fraction actualResult = new Fraction(answerLine.split(":")[1].trim());

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
                    System.err.println("处理题目 " + index + " 时出错: " + e.getMessage());
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