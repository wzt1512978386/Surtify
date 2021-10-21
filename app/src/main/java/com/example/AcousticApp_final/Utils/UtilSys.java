package com.example.AcousticApp_final.Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.AcousticApp_final.App.MyApp;

public class UtilSys {
    //申请权限
    public static void requestPermisson(Activity activity, String permission){
        if(ContextCompat.checkSelfPermission(activity,permission)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,new String[]{permission},0);
        }
    }
    public static void sendInfo(String info){
        Intent intent=new Intent("com.INFO");
        intent.putExtra("info",info);
        MyApp.mainActivity.context.sendBroadcast(intent);
    }
    public static void LOG_I(String info){
        Intent intent=new Intent("com.LOGI");
        intent.putExtra("info",info);
        MyApp.mainActivity.context.sendBroadcast(intent);
    }
    public static void LOG_W(String info){
        Intent intent=new Intent("com.LOGW");
        intent.putExtra("info",info);
        MyApp.mainActivity.context.sendBroadcast(intent);
    }
    public static void trash(){
        Intent intent=new Intent("com.TRASH");
        MyApp.mainActivity.context.sendBroadcast(intent);
    }
}
