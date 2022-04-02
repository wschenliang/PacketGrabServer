package com.jiangnan.constants;

import java.awt.*;

/**
 * 常用颜色选择
 *
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class ColorSelect {

    //标识规律：正常为紫色底，如果两个数据正好是相互交换，用灰色底标识出两个
    //包含144.144.144.144 之类的是DNS通讯，和UDP一样，用蓝色底

    //正常底色紫色底
    public static final Color NORMAL_BG = new Color(231,230,255);

    //黑底红字使用
    public static final Color RED_DATA = new Color(254,134,120);
    //黑底
    public static final Color BLACK_BG = new Color(0,0,0);

    //灰色底
    public static final Color GRAY_BG = new Color(160,160,160);

    //绿色底
    public static final Color GREEN_BG = new Color(228,255,199);

    //红色底
    public static final Color RED_BG = new Color(164,0,0);
    //红底黄字
    public static final Color YELLOW_DATA = new Color(255,180,62);

    //蓝色底
    public static final Color BLUE_BG = new Color(218,238,255);

    //黄色底
    public static final Color YELLOW_BG = new Color(250,240,215);


}
