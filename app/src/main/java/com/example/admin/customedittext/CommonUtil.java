package com.example.admin.customedittext;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by bob on 2015/2/2.
 */
public class CommonUtil {


    /**
     * 当前页面截图
     */
    public static Bitmap screenCapture(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        //获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        System.out.println(statusBarHeight);//获取屏幕长和高

        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();//去掉标题栏
        Bitmap bitmap = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return bitmap;
    }

    /**
     * 获取listView总高度
     */
    public static int getListViewHeight(ListView listView) {
        ListAdapter mAdapter = listView.getAdapter();
        if (mAdapter == null) {
            return 0;
        }
        int totalHeight = 0;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);
            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            //mView.measure(0,0);
            totalHeight += mView.getMeasuredHeight();
        }
        return totalHeight;

    }

    /**
     * view转成bitmap
     */
    public static Bitmap viewToBitmap(View view) {
        view.destroyDrawingCache();
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        return view.getDrawingCache(true);
    }

    /**
     * 计算地球上任意两点(经纬度)距离
     *
     * @param long1 第一点经度
     * @param lat1  第一点纬度
     * @param long2 第二点经度
     * @param lat2  第二点纬度
     * @return 返回距离 单位：米
     */
    public static double distanceByLongNLat(double long1, double lat1, double long2, double lat2) {
        double a, b, R, d, sa2, sb2;
        R = 6378137;//地球半径
        lat1 = lat1 * Math.PI / 180.0;
        lat2 = lat2 * Math.PI / 180.0;
        a = lat1 - lat2;
        b = (long1 - long2) * Math.PI / 180.0;
        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2 * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));
        return d;
    }

    // IMEI码
    public static String getIMIEStatus(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        return deviceId;
    }

    // Mac地址
    public static String getLocalMac(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    // Android Id
    public static String getAndroidId(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return androidId;
    }

    /**
     * pop弹窗设置屏幕透明度
     */
    public static void setWindowAlpha(Activity activity, float alphaValue) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = alphaValue; //0.0-1.0
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 键盘是否显示
     */
    public static void isKeyboardShow(final Activity activity, int resourceId) {
        final View root = activity.findViewById(resourceId);
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int screenHeight = root.getRootView().getHeight();
                int myAppHeight = root.getHeight();
                int heightDiff = screenHeight - myAppHeight;
                PrintUtil.log("screenHeight", screenHeight);
                PrintUtil.log("myHeight", myAppHeight);

                if (heightDiff > 25) {
                    PrintUtil.log("keyboard is showing");
                } else {
                    PrintUtil.log("keyboard is hidden");
                }
            }
        });

    }

    public static boolean isKeyboardShow(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();//isOpen若返回true，则表示输入法打开
        return isOpen;
    }

    /**
     * 获取键盘高度
     */
    public static void getKeyboardHeight(Activity activity) {
        final View decorView = activity.getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);
                //计算出可见屏幕的高度
                int displayHeight = rect.bottom - rect.top;
                //获得屏幕整体的高度
                int height = decorView.getHeight();
                //获得键盘高度
                int keyboardHeight = height - displayHeight;

                /*boolean visible = (double) displayHeight / height < 0.8;
                if (visible != isVisiableForLast) {
                    listener.onSoftKeyBoardVisible(visible, keyboardHeight);
                }
                isVisiableForLast = visible;*/
            }
        });
    }

    /**
     * 隐藏键盘
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager manager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 显示键盘
     *
     * @param activity
     */
    public static void showKeyboard(Activity activity) {
        InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void showKeyboard(View view, Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 跳转到拨号界面，同时传递电话号码
     */
    public static void callPhone(Context context, String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNum));
        context.startActivity(intent);
    }

    public static void callPhone(Context context, int phoneNumSourceId) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + context.getResources().getString(phoneNumSourceId)));
        context.startActivity(intent);
    }

    /**
     * 得到屏幕高度
     */
    public static int getScreenHeight(Activity context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        return height;

    }

    /**
     * 得到屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Activity context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }

    /**
     * 得到状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Activity context) {
        int statusHeight = 0;
        Rect frame = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        statusHeight = frame.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    /**
     * 获得当前的版本信息
     *
     * @return
     */
    public static String[] getVersionInfo(Context context) {
        String[] version = new String[2];

        PackageManager packageManager = context.getPackageManager();

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            version[0] = String.valueOf(packageInfo.versionCode);
            version[1] = packageInfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    /**
     * 邮箱格式是否正确
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {

        if (TextUtils.isEmpty(email))
            return false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();

    }

    /**
     * 手机号格式是否正确
     *
     * @param phoneNum
     * @return
     */
    public static boolean isPhoneNum(String phoneNum) {
        if (TextUtils.isEmpty(phoneNum))
            return false;

        String expression = "^((1[3-5,7-8])\\d{9}$)";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(phoneNum);

        return matcher.matches();

    }

    /**
     * 是否开启 GPS
     *
     * @param context
     * @return
     */
    public static boolean isOpenGPS(Context context) {

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNPEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        return isGPSEnable || isNPEnable;


    }

    public static void openGPS(Context context) {

        Intent GPSIntent = new Intent();

        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");

        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));

        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }


}
