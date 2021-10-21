package com.example.AcousticApp_final.Pager.Document.Folder;

import com.example.AcousticApp_final.App.MyApp;
import com.example.AcousticApp_final.Config.ConfigFile;
import com.example.AcousticApp_final.Utils.UtilSys;

import java.io.File;
import java.util.Calendar;
import java.util.List;

/**
 * @author: wzt
 * @date: 2020/11/18
 */
public class FolderManager {
    public static List<FolderEntity> initFolder(List<FolderEntity> folderList){
        folderList.clear();
        File storageFile=new File(ConfigFile.getStoragePath());
        File []childFile=storageFile.listFiles();
        Calendar calendar=Calendar.getInstance();//计算时间的工具
        for(int i=0;i<childFile.length;i++){
            String userID=childFile[i].getName();
            String userInfo=" ";
            int fileNUm=childFile[i].listFiles().length;
            //时间
            Long curTime=childFile[i].lastModified();//获得建造时间
            calendar.setTimeInMillis(curTime);
            int y=calendar.get(Calendar.YEAR);
            int m=calendar.get(Calendar.MONTH);
            int d=calendar.get(Calendar.DAY_OF_MONTH);
            FolderEntity folderEntity=new FolderEntity(userID,userInfo,y,m,d,fileNUm,childFile[i]);
            folderList.add(folderEntity);
        }
        return folderList;
    }
    public static File createStorageFolder(String folderName){
        File storageFile=new File(ConfigFile.getStoragePath());
        File folderFile=new File(ConfigFile.getStoragePath()+"/"+folderName);
        File []childFile=storageFile.listFiles();
        for (File file : childFile) {
            if (folderName.equals(file.getName())) {//
                UtilSys.LOG_I("User<"+folderName+">的记录已存在");
                return folderFile;
            }
        }

        if(folderFile.mkdir()) {
            UtilSys.LOG_I("User<" + folderName + ">记录创建成功！");
            UtilSys.sendInfo("用户记录创建成功！");
        }
        else {
            UtilSys.LOG_I("User<" + folderName + ">记录创建失败！");
            UtilSys.sendInfo("用户记录创建失败！");
        }
        notifyFolderChange();
        return folderFile;
    }
    public static boolean deleteFolder(File file) {
        if (!file.exists()) {
            UtilSys.LOG_W("file地址不对，删除不了");
            return false;
        }
        if (!file.isDirectory()) {
            UtilSys.LOG_I("File<"+file.getName()+">删除成功！");
            file.delete();
            notifyFolderChange();
            return true;
        }
        File[] childFile = file.listFiles();
        // 空文件夹
        if (childFile.length == 0) {
            UtilSys.LOG_I("Folder<"+file.getName()+">删除成功！");
            file.delete();
            notifyFolderChange();
            return true;
        }
        // 删除子文件夹和子文件
        for (File F : childFile) {
            if (F.isDirectory()) {
                deleteFolder(F);
            } else {
                UtilSys.LOG_I("File<"+F.getName()+">删除成功！");
                F.delete();
            }
        }

        // 删除文件夹本身
        UtilSys.LOG_I("Folder<"+file.getName()+">删除成功！");
        file.delete();
        notifyFolderChange();
        return true;
    }
    //提醒文件夹更新
    public static void notifyFolderChange(){
        //wavList= WavManager.initWavFile()
        FolderManager.initFolder(MyApp.pageDocument.folderList);
        MyApp.pageDocument.folderAdapter.notifyDataSetChanged();
    }


}
