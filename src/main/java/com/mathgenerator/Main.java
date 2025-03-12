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
 * @modified YiLaiL
 */
public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length < 8 ) {
            System.out.println("Usage: Myapp.exe -n <number> -r <range> -e <exercisefile>.txt -a <answerfile>.txt");
            System.out.println("   or: Myapp.exe -e <exercisefile>.txt -a <answerfile>.txt -g <gradefile>.txt");
            return;
        }

        // 处理生成题目和答案的逻辑
        if ("-n".equals(args[0]) && "-r".equals(args[2])) {
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
                expressions.add(normalizedExpression);
            }

            // 将题目和答案写入文件
            FileHandler.writeExpressions(expressions, args[5]);
            FileHandler.writeAnswers(expressions, args[7]);

            System.out.println("生成题目和答案成功！");
        }
        // 处理对比答案的逻辑
        else if ("-e".equals(args[0]) && "-a".equals(args[2])) {
            String exerciseFile = args[1];
            String answerFile = args[3];

            // 调用 gradeAnswers 函数对比答案
            FileHandler.gradeAnswers(exerciseFile, answerFile,args[5]);

            System.out.println("对比答案完成，结果已写入 Grade.txt！");
        } else {
            System.out.println("无效的参数。");
            System.out.println("Usage: Myapp.exe -n <number> -r <range> -e <exercisefile>.txt -a <answerfile>.txt");
            System.out.println("   or: Myapp.exe -e <exercisefile>.txt -a <answerfile>.txt -g <gradefile>.txt");
        }
    }
}