package com.mathgenerator.model;

/**
 * @author redmi k50 ultra
 * * @date 2025/3/11
 */
public class Fraction {
    private int numerator;
    private int denominator;

    // 构造函数：接受分子和分母
    public Fraction(int numerator, int denominator) {
        if (denominator == 0) {
            throw new IllegalArgumentException("Denominator cannot be zero.");
        }
        this.numerator = numerator;
        this.denominator = denominator;
        simplify();
    }

    // 构造函数：接受字符串形式的分数（如 "3/4" 或 "2'3/8"）
    public Fraction(String fractionStr) {
        if (fractionStr.contains("'")) {
            // 处理带分数（如 "2'3/8"）
            String[] parts = fractionStr.split("'");
            int whole = Integer.parseInt(parts[0]);
            String[] fractionParts = parts[1].split("/");
            int numerator = Integer.parseInt(fractionParts[0]);
            int denominator = Integer.parseInt(fractionParts[1]);
            this.numerator = whole * denominator + numerator;
            this.denominator = denominator;
        } else if (fractionStr.contains("/")) {
            // 处理真分数（如 "3/4"）
            String[] parts = fractionStr.split("/");
            this.numerator = Integer.parseInt(parts[0]);
            this.denominator = Integer.parseInt(parts[1]);
        } else {
            // 处理整数（如 "5"）
            this.numerator = Integer.parseInt(fractionStr);
            this.denominator = 1;
        }
        simplify();
    }

    // 简化分数
    private void simplify() {
        int gcd = gcd(Math.abs(numerator), Math.abs(denominator));
        numerator /= gcd;
        denominator /= gcd;
        if (denominator < 0) {
            numerator *= -1;
            denominator *= -1;
        }
    }

    // 计算最大公约数
    private int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    // 分数的加法
    public Fraction add(Fraction other) {
        int newNumerator = this.numerator * other.denominator + other.numerator * this.denominator;
        int newDenominator = this.denominator * other.denominator;
        return new Fraction(newNumerator, newDenominator);
    }

    // 分数的减法
    public Fraction subtract(Fraction other) {
        int newNumerator = this.numerator * other.denominator - other.numerator * this.denominator;
        int newDenominator = this.denominator * other.denominator;
        return new Fraction(newNumerator, newDenominator);
    }

    // 分数的乘法
    public Fraction multiply(Fraction other) {
        int newNumerator = this.numerator * other.numerator;
        int newDenominator = this.denominator * other.denominator;
        return new Fraction(newNumerator, newDenominator);
    }

    // 分数的除法
    public Fraction divide(Fraction other) {
        if (other.numerator == 0) {
            throw new IllegalArgumentException("Cannot divide by zero.");
        }
        int newNumerator = this.numerator * other.denominator;
        int newDenominator = this.denominator * other.numerator;
        return new Fraction(newNumerator, newDenominator);
    }

    // 重写 equals 方法，用于比较两个分数是否相等
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Fraction other = (Fraction) obj;
        return this.numerator == other.numerator && this.denominator == other.denominator;
    }

    // 重写 toString 方法，将分数转换为字符串
    @Override
    public String toString() {
        if (denominator == 1) {
            return Integer.toString(numerator);
        } else if (Math.abs(numerator) > denominator) {
            int wholePart = numerator / denominator;
            int remainder = Math.abs(numerator) % denominator;
            return wholePart + "'" + remainder + "/" + denominator;
        } else {
            return numerator + "/" + denominator;
        }
    }

    public int compareTo(Fraction other) {
        // 转换为相同分母进行比较
        int commonDenominator = this.denominator * other.denominator;
        int thisNumerator = this.numerator * other.denominator;
        int otherNumerator = other.numerator * this.denominator;
        return Integer.compare(thisNumerator, otherNumerator);
    }

    public int getNumerator() {
        return numerator;
    }

    public int getDenominator() {
        return denominator;
    }
}