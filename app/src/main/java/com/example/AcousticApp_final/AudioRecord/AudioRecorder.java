package com.example.AcousticApp_final.AudioRecord;

import android.media.AudioRecord;
import android.util.Log;


import com.example.AcousticApp_final.App.MyApp;
import com.example.AcousticApp_final.Config.ConfigAudio;
import com.example.AcousticApp_final.Config.ConfigFile;
import com.example.AcousticApp_final.Utils.UtilWav;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioRecorder {


    enum  STATE{RECORD,PAUSE,STOP}
    private volatile STATE state=STATE.STOP;

    private AudioRecord record;
    private AudioRecordThread ARThread;
    private int bufSize;//每次读取的最低字节数


    File fileS;


    public AudioRecorder(){
        bufSize= AudioRecord.getMinBufferSize(ConfigAudio.getSampleRate(),
                ConfigAudio.getChannel(), ConfigAudio.ENCODE);



    }

    public void start(){
        record=new AudioRecord(ConfigAudio.getSource(),
                ConfigAudio.getSampleRate(), ConfigAudio.getChannel(),
                ConfigAudio.ENCODE,bufSize);
        fileS=new File(ConfigFile.getCachePath() + "/"+ConfigFile.CACHE_NAME + ConfigAudio.FOMAT);
        state= STATE.RECORD;

        ARThread=new AudioRecordThread();
        ARThread.start();
    }
    public void stop(){
        state= STATE.STOP;
    }

    private class AudioRecordThread extends Thread{
        @Override
        public void run() {

            FileOutputStream fos=null;
            super.run();
            try {

                fos = new FileOutputStream(fileS);
                byte []buf = new byte[bufSize];
                record.startRecording();
                while (state == STATE.RECORD) {
                    int end = record.read(buf, 0, buf.length);
                    //recordListener.getBuff(buf,end);--------------
                    MyApp.viewWave.setData(buf,end);
                    fos.write(buf, 0, end);
                    fos.flush();
                }
                fos.flush();

                fos.close();
                record.stop();


                byte[] header = UtilWav.generateWavFileHeader((int) fileS.length(), ConfigAudio.getSampleRate(), 2, 16);
                UtilWav.writeHeader(fileS, header);

                //Util.sendInfo(context,"录音完成，文件在：\n"+AudioConfig.dirPathStorage+AudioConfig.getFileName());
                //recordResultListener.getRecordFile(fileS);-------------------
            } catch (FileNotFoundException e) {
                Log.e("IN101", "文件输出流创建错误!");
            } catch (IOException e) {
                Log.e("IN101", "缓冲区写入错误!");
            }
        }
    }
}
