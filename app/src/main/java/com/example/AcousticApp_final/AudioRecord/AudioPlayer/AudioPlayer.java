package com.example.AcousticApp_final.AudioRecord.AudioPlayer;

import android.media.MediaPlayer;

import com.example.AcousticApp_final.App.MyApp;
import com.example.AcousticApp_final.Utils.UtilSys;

import java.io.File;
import java.io.IOException;

/**
 * @author: wzt
 * @date: 2020/12/13
 */
public class AudioPlayer {
    private MediaPlayer mediaPlayer;
    public AudioPlayer(){
        mediaPlayer=null;
    }
    public void play(File wavFile){
        mediaPlayer=new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                MyApp.pageRecording.bt_play.callOnClick();//触发
            }
        });
        try {
            mediaPlayer.setDataSource(wavFile.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();

        }catch (IOException e){
            UtilSys.LOG_W("播放不了音频");
            return;
        }
    }
    public void stop(){

        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer=null;
    }
}
