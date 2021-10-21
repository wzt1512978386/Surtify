package com.example.AcousticApp_final.Config;

import android.media.AudioFormat;
import android.media.MediaRecorder;

/**
 * @author: wzt
 * @date: 2020/11/17
 */
//音频参数
public class ConfigAudio {
    private static final int SOURCE= MediaRecorder.AudioSource.MIC;  //麦克风设备
    private static int SAMPLERATE=44100;
    private static int CHANNEL= AudioFormat.CHANNEL_IN_STEREO;
    //final
    public static final int ENCODE=AudioFormat.ENCODING_PCM_16BIT;
    public static final String FOMAT=".wav";

    //获取参数
    public static int getChannel() {
        return CHANNEL;
    }
    public static int getSampleRate() {
        return SAMPLERATE;
    }
    public static int getSource() {
        return SOURCE;
    }


    //获取属性值
    public static int getChannelAttr() {
        if(CHANNEL==AudioFormat.CHANNEL_IN_STEREO)
            return 2;
        else
            return 1;
    }
    public static int getEncodeAttr(){
        if(ENCODE==AudioFormat.ENCODING_PCM_16BIT)
            return 16;
        else
            return 8;
    }
    public static int getSampleRateAttr(){
        return SAMPLERATE;
    }



    //设置
    public static enum SAMPLERATE_TYPE{RATE_8000,RATE_16000,RATE_44100}
    public static void setSampleRate(SAMPLERATE_TYPE type){
        switch (type){
            case RATE_8000:
                SAMPLERATE=8000;break;
            case RATE_16000:
                SAMPLERATE=16000;break;
            case RATE_44100:
                SAMPLERATE=44100;break;
            default:break;
        }
    }
    public enum CHANNEL_TYPE{STEREO,MONO};
    public static void setChannel(CHANNEL_TYPE type){
        switch (type){
            case STEREO:
                CHANNEL=AudioFormat.CHANNEL_IN_STEREO;break;
            case MONO:
                CHANNEL=AudioFormat.CHANNEL_IN_MONO;break;
            default:break;
        }
    }
}