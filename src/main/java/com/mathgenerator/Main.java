package com.mathgenerator;

import com.mathgenerator.handler.FileHandler;
import com.mathgenerator.generator.ExpressionGenerator;
import com.mathgenerator.generator.ExpressionGenerator.ExpressionResult;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author redmi k50 ultra
 * * @date 2025/3/11
 */
public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length < 2) {
            System.out.println("Usage: Myapp.exe -n <number> -r <range>");
            System.out.println("   or: Myapp.exe -e <exercisefile>.txt -a <answerfile>.txt");
            return;
        }

        // 处理生成题目和答案的逻辑
        if (args[0].equals("-n") && args[2].equals("-r")) {
            int n = Integer.parseInt(args[1]);
            int r = Integer.parseInt(args[3]);

            if (r == 0) {
                System.out.println("Range parameter -r is required.");
                return;
            }

            ExpressionGenerator generator = new ExpressionGenerator(r);
            Set<String> expressions = new HashSet<>();
            while (expressions.size() < n) {
                ExpressionResult result = generator.generateExpression();
                String normalizedExpression = generator.normalizeExpression(result.getExpression());
                if (!expressions.contains(normalizedExpression)) {
                    expressions.add(normalizedExpression);
                }
            }

            // 将题目和答案写入文件
            FileHandler.writeExpressions(expressions, "D:\\code\\Java\\FourCalculationQuestions\\FourCalculationQuestions\\src\\main\\resources\\Exercises.txt");
            FileHandler.writeAnswers(expressions, "D:\\code\\Java\\FourCalculationQuestions\\FourCalculationQuestions\\src\\main\\resources\\Answer.txt");

            System.out.println("生成题目和答案成功！");
        }
        // 处理对比答案的逻辑
        else if (args[0].equals("-e") && args[2].equals("-a")) {
            String exerciseFile = args[1];
            String answerFile = args[3];

            // 调用 gradeAnswers 函数对比答案
            FileHandler.gradeAnswers(exerciseFile, answerFile, "D:\\code\\Java\\FourCalculationQuestions\\FourCalculationQuestions\\src\\main\\resources\\Grade.txt");

            System.out.println("对比答案完成，结果已写入 Grade.txt！");
        } else {
            System.out.println("无效的参数。");
            System.out.println("Usage: Myapp.exe -n <number> -r <range>");
            System.out.println("   or: Myapp.exe -e <exercisefile>.txt -a <answerfile>.txt");
        }
    }
}