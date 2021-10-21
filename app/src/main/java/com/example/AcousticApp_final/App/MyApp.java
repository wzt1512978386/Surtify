package com.example.AcousticApp_final.App;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.example.AcousticApp_final.MainActivity;
import com.example.AcousticApp_final.Pager.Document.PageDocument;
import com.example.AcousticApp_final.Pager.Furniture.PageFurniture;
import com.example.AcousticApp_final.Pager.MyViewPager;
import com.example.AcousticApp_final.Pager.Outcome.PageOutcome;
import com.example.AcousticApp_final.Pager.Recording.PageRecording;
import com.example.AcousticApp_final.Pager.Setting.PageSetting;
import com.example.AcousticApp_final.Proccess.PythonManager;
import com.example.AcousticApp_final.UI.Outcome.FrameOutcome;
import com.example.AcousticApp_final.UI.Outcome.ViewLineChart;
import com.example.AcousticApp_final.UI.Recording.FrameRecording;
import com.example.AcousticApp_final.UI.Recording.ViewWave;
import com.example.AcousticApp_final.Utils.UtilSys;
import com.example.AcousticApp_final.Model.Classifier;

/**
 * @author: wzt
 * @date: 2020/11/17
 */
public class MyApp extends Application {
    //外部调用
    public static MyApp myApp;
    public static Context context;
    //线程
    public static final Handler handler = new Handler();
    //主活动
    public static MainActivity mainActivity;
    //页面
    public static MyViewPager myViewPager;
    public static PageRecording pageRecording;
    public static PageDocument pageDocument;
    public static PageSetting pageSetting;
    public static PageOutcome pageOutcome;
    public static PageFurniture pageFurniture;
    //布局框架
    public static FrameRecording frameRecording;
    public static FrameOutcome frameOutcome;
    //view
    public static ViewWave viewWave;
    public static ViewLineChart viewLineChart;
    //数据处理
    public static PythonManager pythonManager;
    //模型分类器
    public static Classifier classifier;

    //flag 单个测试0 或全体测试 1
    public static int testFlag=0;
    @Override
    public void onCreate() {
        super.onCreate();
        myApp=this;
    }
    //权限申请
    public static void requestPermission(Activity activity){
        UtilSys.requestPermisson(activity, Manifest.permission.RECORD_AUDIO);
        UtilSys.requestPermisson(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        UtilSys.requestPermisson(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

}
