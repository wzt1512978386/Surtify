package com.example.AcousticApp_final.Pager.Recording;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.AcousticApp_final.App.MyApp;
import com.example.AcousticApp_final.AudioRecord.AudioPlayer.AudioPlayer;
import com.example.AcousticApp_final.AudioRecord.AudioRecorder;
import com.example.AcousticApp_final.Config.ConfigAudio;
import com.example.AcousticApp_final.Config.ConfigFile;
import com.example.AcousticApp_final.R;
import com.example.AcousticApp_final.UI.Recording.ViewControl;
import com.example.AcousticApp_final.UI.Recording.ViewWave;
import com.example.AcousticApp_final.Model.Classifier;

import java.io.File;
import java.util.Timer;


public class PageRecording extends Fragment {
    //外部调用
    public Activity activity;
    public Context context;
    public View root;
    //事件管理工具
    public EventsRecording eventsRecording;

    //控件
    protected EditText userID;
    protected EditText wavName;
    //计时器工具
    protected TextView time;//时间
    protected Timer timer;//用来更新时间
    public int recordTime=0;//时间变量
    //按钮
    public ViewControl bt_Control;//录制和暂停按钮
    protected ImageButton bt_correct,bt_wrong;
    public ImageButton bt_play;
    protected ViewWave viewWave;//

    //音频
    protected AudioRecorder audioRecorder;
    protected AudioPlayer audioPlayer;
    protected boolean isPlay=false;

    //模型训练
    protected Button bt_train;


    //即将训练的文件
    public File trainFile;
    protected File cacheFile;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.pager_recording, container, false);//设置对应布局
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.activity=getActivity();
        this.context=getContext();
        MyApp.pageRecording=this;
        //控件初始化
        bt_Control=(ViewControl)root.findViewById(R.id.viewcontrol_recording);
        bt_correct=(ImageButton)root.findViewById(R.id.imagebutton_recording_correct);
        bt_play=(ImageButton)root.findViewById(R.id.imagebutton_recording_play);
        bt_wrong=(ImageButton)root.findViewById(R.id.imagebutton_recording_wrong);

        userID=(EditText)root.findViewById(R.id.edittext_recording_userID);
        wavName=(EditText)root.findViewById(R.id.edittext_recording_wavName);

        viewWave=(ViewWave)root.findViewById(R.id.viewwave_recording);
        time=(TextView)root.findViewById(R.id.textview_recording_time);

        //音频
        audioRecorder=new AudioRecorder();
        audioPlayer=new AudioPlayer();
        //初始化训练按钮
        initTrain();

        eventsRecording=new EventsRecording();
        eventsRecording.setEvents(this);
    }
    private void initTrain(){
        //训练器
        MyApp.classifier=new Classifier();
        //训练文件
        cacheFile=new File(ConfigFile.getCachePath()+"/"+ConfigFile.CACHE_NAME+ ConfigAudio.FOMAT);
        trainFile=cacheFile;

        //控件获取
        bt_train=(Button)root.findViewById(R.id.button_recording_train);


    }
    public void update(String userID,String wavName){
        this.userID.setText(userID);
        this.wavName.setText(wavName);
    }


}
