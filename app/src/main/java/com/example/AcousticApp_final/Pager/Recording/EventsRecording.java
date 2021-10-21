package com.example.AcousticApp_final.Pager.Recording;

import android.view.View;
import android.widget.Toast;

import com.example.AcousticApp_final.App.MyApp;
import com.example.AcousticApp_final.Pager.Document.Folder.FolderManager;
import com.example.AcousticApp_final.Pager.Document.WavFile.WavManager;
import com.example.AcousticApp_final.Proccess.ProccessManager;
import com.example.AcousticApp_final.R;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author: wzt
 * @date: 2020/11/17
 */
public class EventsRecording{
    private PageRecording PR;

    //状态定义
    public enum STATE{INIT,RECORD,STOP,PLAY,FINISH,CANCLE};
    public  STATE state=STATE.INIT;

    public  void setEvents(PageRecording pageRecording){
        PR=pageRecording;
        setOnClick();
    }
    private  void setOnClick(){
        //开始录音控制
        PR.bt_Control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (state){
                    case INIT:
                        state=STATE.RECORD;
                        //开始录音
                        PR.audioRecorder.start();
                        //控件状态变化
                        PR.bt_Control.changeState();//控制按钮变化状态
                        MyApp.frameRecording.startSectionDown();
                        //启动计时器
                        PR.timer=new Timer();
                        PR.timer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() { updateTime();
                            }
                        },0,50);
                        break;
                    case RECORD:
                        state=STATE.FINISH;
                        PR.audioRecorder.stop();//停止录音

                        //控件状态变化
                        PR.bt_Control.changeState();//控制按钮变化状态
                        MyApp.frameRecording.reverseSectionDown();
                        MyApp.frameRecording.startButtonRoate();
                        PR.bt_Control.setClickable(false);
                        //停止计时器
                        if(PR.timer!=null){//停止计时器
                            PR.timer.cancel();
                            PR.timer.purge();
                            PR.timer=null;
                        }
                        //将即将训练的文件更新为刚才录制的文件
                        MyApp.pageRecording.trainFile=MyApp.pageRecording.cacheFile;
                        break;
                    case FINISH:
                        //MyApp.frameRecording.reverseButtonRoate();
                        state=STATE.INIT;
                }
            }
        });
        //保存录音
        PR.bt_correct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (state){
                    case FINISH:
                        state=STATE.INIT;
                        //重置计时器时间
                        PR.recordTime=0;
                        PR.time.setText(String.format(PR.context.getString(R.string.recordtime_format),PR.recordTime/60,PR.recordTime%60));
                        //按钮恢复位置
                        PR.bt_Control.setClickable(true);//不选选取
                        MyApp.frameRecording.reverseButtonRoate();
                        PR.viewWave.setClear();

                        //保存
                        //创建文件夹
                        String userID=PR.userID.getText().toString().trim();
                        if(userID.isEmpty())
                            userID="default";
                        //创建用户文件夹
                        File userFile=FolderManager.createStorageFolder(userID);
                        //获取存储wav的名字
                        String newWavName=WavManager.getNewWavName(userFile);
                        //从cache中复制音频文件
                        WavManager.copyCacheForWaveFile(userID,newWavName);
                        //通知文件夹更新，因为wav新增后folder的子项数量信息要更新，不过有点点重复
                        FolderManager.notifyFolderChange();
                        WavManager.notifyWavChange(userFile);
                        break;
                }
            }
        });
        PR.bt_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (state){
                    case FINISH:
                        PR.bt_play.setImageResource(R.drawable.p_button_recording_stop);
                        state=STATE.PLAY;
                        PR.audioPlayer.play(PR.trainFile);
                        break;
                    case PLAY:
                        PR.audioPlayer.stop();
                        PR.bt_play.setImageResource(R.drawable.p_button_recording_play);
                        state=STATE.STOP;
                        state=STATE.FINISH;

                        break;
                    case STOP:break;
                }
            }
        });
        PR.bt_wrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (state){
                    case FINISH:
                        state=STATE.INIT;
                        //重置计时器时间
                        PR.recordTime=0;
                        PR.time.setText(String.format(PR.context.getString(R.string.recordtime_format),PR.recordTime/60,PR.recordTime%60));
                        //按钮恢复位置
                        PR.bt_Control.setClickable(true);
                        MyApp.frameRecording.reverseButtonRoate();
                        PR.viewWave.setClear();
                        //
                        break;
                }
            }
        });

        //训练
        PR.bt_train.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.testFlag=0;
                //空
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int ans=0;
                        if(ProccessManager.type==1) {
                            ans=ProccessManager.testForResult1(MyApp.pageRecording.trainFile);

                        }
                        else if(ProccessManager.type==2){
                            ans=ProccessManager.testForResult2(MyApp.pageRecording.trainFile);
                        }
                    }
                }).start();

            }
        });




    }
    public void updateTime(){
        PR.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PR.recordTime+=3;
                PR.time.setText(String.format(PR.context.getString(R.string.recordtime_format),PR.recordTime/60,PR.recordTime%60));
            }
        });
    }


}
