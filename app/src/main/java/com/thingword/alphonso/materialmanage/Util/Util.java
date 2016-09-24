package com.thingword.alphonso.materialmanage.Util;

/**
 * Created by thingword-A on 2016/9/24.
 */
public class Util {
    public static String fillTaskCode(String code) {
        StringBuilder stringBuilder = new StringBuilder();
        if (code.length() < 10) {
            int len = 10 - code.length();
            for (int i = 0; i < len; i++) {
                stringBuilder.append('0');
            }
            stringBuilder.append(code);
        }
        return stringBuilder.toString();
    }

    public static String cutCode(String code) {
        if(code.length()>10){
            return code.substring(0,10);
        }
        return code;
    }

    public static String fillProductCode(String code, String worksop) {
        if (worksop.equals("二车间")) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append('3');
            stringBuilder.append(code);
            return stringBuilder.toString();
        } else if (worksop.equals("一车间")) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append('2');
            stringBuilder.append(code);
            return stringBuilder.toString();
        }
        return null;

    }

}
