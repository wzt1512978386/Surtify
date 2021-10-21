package com.example.AcousticApp_final.Utils;

import java.util.Calendar;

/**
 * @author: wzt
 * @date: 2020/12/2
 */
public class UtilTime {
    //返回时
    public static int getCurH(){
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }
    //获取分
    public static int getCurMi(){
        return Calendar.getInstance().get(Calendar.MINUTE);
    }
    //获取秒
    public static int getCurS(){
        return Calendar.getInstance().get(Calendar.SECOND);
    }
    //获取月
    public static int getCurMo(){
        return Calendar.getInstance().get(Calendar.MONTH)+1;
    }
    //获取日
    public static int getCurD(){
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }



}
