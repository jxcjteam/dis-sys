package cn.ds.commons.utils;

import java.math.BigDecimal;

/**
 * 金额转换
 * @author zj
 */
public class NumberUtil {

    /**
     * RMB分转成元 采用四舍五入，保留两位精度
     * @param fen
     * @return
     */
    public static BigDecimal fenToYuan(long fen){
        return BigDecimal.valueOf(fen).divide(new BigDecimal(100), 2, BigDecimal.ROUND_DOWN);
    }

    /**
     * RMB元转分
     */
    public static BigDecimal yuanToFen(double yuan){
        return BigDecimal.valueOf(yuan).multiply(new BigDecimal(100));
    }

    /**
     * RMB厘转成元 采用四舍五入，保留二位精度
     * @param cy
     * @return
     */
    public static BigDecimal cyToYuan(long cy){
        return BigDecimal.valueOf(cy).divide(new BigDecimal(1000), 2, BigDecimal.ROUND_DOWN);
    }

    /**
     * RMB元转厘
     */
    public static BigDecimal yuanTcy(double yuan){
        return BigDecimal.valueOf(yuan).multiply(new BigDecimal(1000));
    }

    /**
     * RMB毫转成元 采用四舍五入，保留二位精度
     * @param my
     * @return
     */
    public static BigDecimal myToYuan(long my){
        return BigDecimal.valueOf(my).divide(new BigDecimal(10000), 2, BigDecimal.ROUND_DOWN);
    }

    /**
     * RMB元转毫
     */
    public static BigDecimal yuanTmy(double yuan){
        return BigDecimal.valueOf(yuan).multiply(new BigDecimal(10000));
    }

    /**
     * 字符串元转毫
     * @param yuan
     * @return
     */
    public static BigDecimal yuanTmy(String yuan) {
        BigDecimal value = new BigDecimal(yuan);
        return value.multiply(new BigDecimal(10000));
    }

    /**
     * RMB毫转分
     */
    public static BigDecimal myTfen(long my){
        return BigDecimal.valueOf(my).divide(new BigDecimal(100), 0, BigDecimal.ROUND_DOWN);
    }

    /**
     * 毫转元  保留3位小数
     * @param hao
     * @param newScale
     * @param roundingMode
     * @return
     */
    public static String hao2yuan(long hao, int newScale , int roundingMode){
        BigDecimal yuan = new BigDecimal(hao).divide(new BigDecimal("10000"));
        return yuan.setScale(newScale,roundingMode).stripTrailingZeros().toPlainString();
    }

    /**
     * RMB毫转成元 采用四舍五入，保留二位精度
     * @param count
     * @return
     */
    public static BigDecimal beanToRmb(long count, int rate){
        return BigDecimal.valueOf(count).divide(new BigDecimal(rate), 2, BigDecimal.ROUND_DOWN);
    }
}
