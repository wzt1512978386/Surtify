package com.example.AcousticApp_final.Pager.Document.WavFile;

import com.example.AcousticApp_final.App.MyApp;
import com.example.AcousticApp_final.Config.ConfigAudio;
import com.example.AcousticApp_final.Config.ConfigFile;
import com.example.AcousticApp_final.R;
import com.example.AcousticApp_final.Utils.UtilSys;
import com.example.AcousticApp_final.Utils.UtilTime;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author: wzt
 * @date: 2020/11/18
 */
public class WavManager {
    public static List<WavEntity> initWavFile(List<WavEntity> wavList,File userFile){
        wavList.clear();
        File []childFile=userFile.listFiles();
        Calendar calendar=Calendar.getInstance();//计算时间的工具
        for(int i=0;i<childFile.length;i++){
            String wavID=childFile[i].getName();
            float size=(int)childFile[i].length()/1024f/1024;
            float time=(float) (childFile[i].length()-42)/(ConfigAudio.getChannelAttr()*ConfigAudio.getEncodeAttr()*ConfigAudio.getSampleRateAttr()/8f);
            //时间
            Long curTime=childFile[i].lastModified();//获得建造时间
            calendar.setTimeInMillis(curTime);
            //获取时分秒
            int h=calendar.get(Calendar.HOUR_OF_DAY);
            int m=calendar.get(Calendar.MINUTE);
            int s=calendar.get(Calendar.SECOND);
            //创建wav实体
            WavEntity wavEntity=new WavEntity(wavID,size,h,m,s,time,childFile[i]);
            wavList.add(wavEntity);//添加进列表中
        }
        return wavList;
    }
    //从cache中复制音频至storage
    public static void copyCacheForWaveFile(String folder,String destWavName)  {
        String sourcePath=ConfigFile.getCachePath()+"/"+ConfigFile.CACHE_NAME+ConfigAudio.FOMAT;//源地址
        String destPath= ConfigFile.getStoragePath()+"/"+folder+"/"+destWavName+ConfigAudio.FOMAT;//目标地址
        try {
            Files.copy(Paths.get(sourcePath), Paths.get(destPath));//要求版本为24！
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    //根据用户文件夹获取要添加的新的wav文件名
    //格式为 wavNo.3_(12-1)   表示编号为3，在12月1号录制
    public static String getNewWavName(File userFile){

        List<WavEntity> wavListTemp=initWavFile(new ArrayList<WavEntity>(), userFile);
        int no=wavListTemp.size()+1;
        return String.format(MyApp.mainActivity.getResources().getString(R.string.wavName_format)
                ,no,UtilTime.getCurMo(),UtilTime.getCurD());
    }
    //删除Wav文件
    public static boolean deleteWav(File file) {
        if (!file.exists()) {
            UtilSys.LOG_W("wav地址不对，删除不了");
            return false;
        }
        if (!file.isDirectory()) {
            UtilSys.LOG_I("File<"+file.getName()+">删除成功！");
            file.delete();
            notifyWavChange(MyApp.pageDocument.curFolder);
            return true;
        }
        UtilSys.LOG_W("File<"+file.getName()+">是文件夹，删除失败！");
        return false;
    }
    //提醒wav更新
    public static void notifyWavChange(File curFolder){
        //wavList= WavManager.initWavFile()
        WavManager.initWavFile(MyApp.pageDocument.wavList,curFolder);
        MyApp.pageDocument.wavAdapter.notifyDataSetChanged();
    }
}
