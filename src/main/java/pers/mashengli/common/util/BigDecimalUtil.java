package pers.mashengli.common.util;

import java.math.BigDecimal;

/**
 * 浮点数计算
 */
public class BigDecimalUtil {
    public static final int scale = 10;

    public static double add(double v1, double v2) {
        BigDecimal a1 = new BigDecimal(Double.toString(v1));
        BigDecimal a2 = new BigDecimal(Double.toString(v2));
        return a1.add(a2).setScale(2, 4).doubleValue();
    }

    public static double add(double v1, double v2, int sale) {
        BigDecimal a1 = new BigDecimal(Double.toString(v1));
        BigDecimal a2 = new BigDecimal(Double.toString(v2));
        if (sale < 0) {
            throw new IllegalArgumentException("精度指定错误,请指定一个>=0的精度");
        }
        return a1.add(a2).setScale(sale, 4).doubleValue();
    }

    public static double add(double v1, double v2, double v3) {
        BigDecimal a1 = new BigDecimal(Double.toString(v1));
        BigDecimal a2 = new BigDecimal(Double.toString(v2));
        BigDecimal a3 = new BigDecimal(Double.toString(v3));
        return a1.add(a2).add(a3).setScale(2, 4).doubleValue();
    }

    public static double div(double v1, double v2) {
        BigDecimal a1 = new BigDecimal(Double.toString(v1));
        BigDecimal a2 = new BigDecimal(Double.toString(v2));
        return divide(a1, a2);
    }

    public static double div(double v1, double v2, int v3) {
        BigDecimal a1 = new BigDecimal(Double.toString(v1));
        BigDecimal a2 = new BigDecimal(Double.toString(v2));
        return divide(a1, a2, v3);
    }

    public static double sub(double v1, double v2) {
        BigDecimal a1 = new BigDecimal(Double.toString(v1));
        BigDecimal a2 = new BigDecimal(Double.toString(v2));
        return a1.subtract(a2).setScale(2, 4).doubleValue();
    }

    public static double sub(double v1, double v2, double v3) {
        BigDecimal a1 = new BigDecimal(Double.toString(v1));
        BigDecimal a2 = new BigDecimal(Double.toString(v2));
        BigDecimal a3 = new BigDecimal(Double.toString(v3));
        return a1.subtract(a2).subtract(a3).setScale(2, 4).doubleValue();
    }

    public static double sub(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("精度指定错误,请指定一个>=0的精度");
        }
        BigDecimal a1 = new BigDecimal(Double.toString(v1));
        BigDecimal a2 = new BigDecimal(Double.toString(v2));
        return a1.subtract(a2).setScale(scale, 4).doubleValue();
    }

    public static double mul(double v1, double v2) {
        BigDecimal a1 = new BigDecimal(Double.toString(v1));
        BigDecimal a2 = new BigDecimal(Double.toString(v2));
        return a1.multiply(a2).setScale(2, 4).doubleValue();
    }

    public static double mul(double v1, double v2, int scale) {
        BigDecimal a1 = new BigDecimal(Double.toString(v1));
        BigDecimal a2 = new BigDecimal(Double.toString(v2));
        if (scale < 0) {
            throw new IllegalArgumentException("精度指定错误,请指定一个>=0的精度");
        }
        return a1.multiply(a2).setScale(scale, 4).doubleValue();
    }

    public static double add(String v1, String v2) {
        BigDecimal a1 = new BigDecimal(v1);
        BigDecimal a2 = new BigDecimal(v2);
        return a1.add(a2).setScale(2, 4).doubleValue();
    }

    public static double div(String v1, String v2) {
        BigDecimal a1 = new BigDecimal(v1);
        BigDecimal a2 = new BigDecimal(v2);
        return divide(a1, a2);
    }

    public static double div(String v1, String v2, int v3) {
        BigDecimal a1 = new BigDecimal(v1);
        BigDecimal a2 = new BigDecimal(v2);
        return divide(a1, a2, v3);
    }

    public static double sub(String v1, String v2) {
        BigDecimal a1 = new BigDecimal(v1);
        BigDecimal a2 = new BigDecimal(v2);
        return a1.subtract(a2).setScale(2, 4).doubleValue();
    }

    public static double mul(String v1, String v2) {
        BigDecimal a1 = new BigDecimal(v1);
        BigDecimal a2 = new BigDecimal(v2);
        return a1.multiply(a2).setScale(2, 4).doubleValue();
    }

    private static double divide(BigDecimal v1, BigDecimal v2) {
        return v1.divide(v2, 10, 4).doubleValue();
    }

    private static double divide(BigDecimal v1, BigDecimal v2, int v3) {
        if (v3 < 0) {
            throw new IllegalArgumentException("精度指定错误,请指定一个>=0的精度");
        }
        return v1.divide(v2, v3, 4).doubleValue();
    }
}

