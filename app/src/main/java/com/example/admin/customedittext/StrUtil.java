package com.example.admin.customedittext;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;

import java.util.regex.Pattern;

/**
 * Created by xiesuichao on 2017/3/13.
 */

public class StrUtil {


    /**
     * 带反斜杠的复杂json串解析成对象
     */
    /*public static Object jsonStr2Obj(String jsonStr, Class vo) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            Field[] fields = vo.getDeclaredFields();
            Object obj = vo.newInstance();

            for (Field field : fields) {
                field.setAccessible(true);
                String name = field.getName();

                try {
                    if (jsonObject.get(name) != null && !TextUtils.isEmpty(jsonObject.getString(name))) {
                        if (field.getType().equals(String.class)) {
                            field.set(obj, jsonObject.getString(name));
                        } else if (field.getType().equals(int.class) || field.getType().equals(Integer.class)) {
                            field.set(obj, jsonObject.getInteger(name));
                        } else if (field.getType().equals(double.class) || field.getType().equals(Double.class)) {
                            field.set(obj, jsonObject.getDouble(name));
                        } else if (field.getType().equals(float.class) || field.getType().equals(Float.class)) {
                            field.set(obj, jsonObject.getFloat(name));
                        } else if (field.getType().equals(long.class) || field.getType().equals(Long.class)) {
                            field.set(obj, jsonObject.getLong(name));
                        } else if (field.getType().equals(BigDecimal.class)) {
                            field.set(obj, jsonObject.getBigDecimal(name));
                        } else if (field.getType().equals(List.class)) {
                            Type type = field.getGenericType();
//                            Print.log("type", type);
                            Class genericClazz = null;
                            if (type == null) continue;
                            // 【3】如果是泛型参数的类型
                            if (type instanceof ParameterizedType) {
                                ParameterizedType pt = (ParameterizedType) type;
                                //【4】 得到泛型里的class类型对象。
                                genericClazz = (Class) pt.getActualTypeArguments()[0];
//                                Print.log("genericClazz", genericClazz);
                            }

                            if (genericClazz != null) {
                                field.set(obj, JSON.parseArray(jsonObject.getString(name), genericClazz));
                            }

                        } else {
                            Class childVo = field.getType();
                            Object obj1 = jsonStr2Obj(jsonObject.getString(name), childVo);
                            field.set(obj, obj1);

                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }

            }
            return obj;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }*/

    /**
     * 用*号隐藏手机号码
     */
    public static String hidePhoneNum(String phoneNum){
        String centerFourNum = phoneNum.substring(3, 7);
        return phoneNum.replace(centerFourNum, "****");
    }

    /**
     * 判断字符串是否为空
     * */
    public static boolean isEmpty(String content){
        if (content!=null && content.length()>0){
            return true;
        }
        return false;
    }

    /**
     * 获取名字的大写首字母
     */
    /*public static String getFirstC(String name) {
        HanziToPinyin                  hanziToPinyin = HanziToPinyin.getInstance();
        ArrayList<HanziToPinyin.Token> tokens        = hanziToPinyin.get(name);
        return tokens.get(0).target.substring(0, 1).toUpperCase();
    }*/


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     * （DisplayMetrics类中属性scaledDensity）
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     * （DisplayMetrics类中属性scaledDensity）
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 得到的屏幕的宽度
     */
    public static int getWidthPx(Activity activity) {
        // DisplayMetrics 一个描述普通显示信息的结构，例如显示大小、密度、字体尺寸
        DisplayMetrics displaysMetrics = new DisplayMetrics();// 初始化一个结构
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);// 对该结构赋值
        return displaysMetrics.widthPixels;
    }

    /**
     * 得到的屏幕的高度
     */
    public static int getHeightPx(Activity activity) {
        // DisplayMetrics 一个描述普通显示信息的结构，例如显示大小、密度、字体尺寸
        DisplayMetrics displaysMetrics = new DisplayMetrics();// 初始化一个结构
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);// 对该结构赋值
        return displaysMetrics.heightPixels;
    }

    /**
     * 得到屏幕的dpi
     */
    public static int getDensityDpi(Activity activity) {
        // DisplayMetrics 一个描述普通显示信息的结构，例如显示大小、密度、字体尺寸
        DisplayMetrics displaysMetrics = new DisplayMetrics();// 初始化一个结构
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);// 对该结构赋值
        return displaysMetrics.densityDpi;
    }

    /**
     * 返回状态栏/通知栏的高度
     */
    public static int getStatusHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        return statusBarHeight;
    }

    public static boolean matches(String code) {
        if (null != code && code.length() > 0) {
            Pattern pattern = Pattern.compile("[0-9]+");
            return pattern.matcher(code).matches();
        }
        return false;
    }
}
