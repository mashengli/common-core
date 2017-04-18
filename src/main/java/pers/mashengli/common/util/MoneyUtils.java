package pers.mashengli.common.util;

import java.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;

/**
 * 文件名：MoneyUtils.java 货币处理相关工具类
 * @date 2016-7-7
 * @author mashengli
 */
public class MoneyUtils {

    /**
     * 将分转换为元
     * @param money
     * @return 字符串，保留两位小数
     */
    public static String formatFen2Yuan(long money) {
        if (money <= 0) {
            return "0.00";
        }
        if (money < 10) {
            return "0.0" + money;
        }
        if (money < 100) {
            return "0." + money;
        }
        double result = money / 100.00;
        return new DecimalFormat("0.00").format(result);
    }

    /**
     * 将分转化为元
     * @param money
     * @return
     * @throws NumberFormatException
     */
    public static String formatFen2Yuan(String money) throws NumberFormatException{
        if (StringUtils.isBlank(money) || !StringUtils.isNumeric(money)) {
            throw new NumberFormatException("格式不支持");
        }
        return formatFen2Yuan(Long.valueOf(money));
    }

    /**
     * 元转化为分
     * @param money
     * @return 字符串
     */
    public static long formYuan2Fen(String money) {
        Double dmoney = new Double(money);
        return new Double(BigDecimalUtil.mul(dmoney, 100)).longValue();
    }

    public static String format2Decimal(double money) {
        DecimalFormat format = new DecimalFormat("#0.00");
        return format.format(money);
    }

}
