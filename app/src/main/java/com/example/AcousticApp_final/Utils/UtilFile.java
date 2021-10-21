package com.example.AcousticApp_final.Utils;

import android.content.Context;
import android.util.Log;

import com.example.AcousticApp_final.Config.ConfigFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author: wzt
 * @date: 2020/11/18
 */
public class UtilFile {
    public static void initFile(){
        //初始化存储文件夹
        File relativeFile=new File(ConfigFile.getRelativePath());
        if(!relativeFile.exists()){

            Log.i("IN101", ConfigFile.getRelativePath());
            Log.i("IN101", "relative文件夹不存在!");
            if(relativeFile.mkdirs()){
                Log.i("IN101", "relative文件夹创建成功");
            }
            else {
                Log.i("IN101", "relative文件夹创建失败");
                return;
            }
        }
        File storageFile=new File(ConfigFile.getStoragePath());
        if(storageFile.exists())
            Log.i("IN101","storage文件夹已存在!");
        else{
            Log.i("IN101","storage文件夹未存在？？");
            if(storageFile.mkdirs())
                Log.i("IN101","storage文件夹创建成功~");
            else
                Log.i("IN101","storage文件夹创建失败@@@");
        }
        //初始化缓存文件夹
        File cacheFile=new File(ConfigFile.getCachePath());
        if(cacheFile.exists())
            Log.i("IN101","cache文件夹已存在!");
        else{
            Log.i("IN101","cache文件夹未存在？？");
            if(cacheFile.mkdirs())
                Log.i("IN101","cache文件夹创建成功~");
            else
                Log.i("IN101","cache文件夹创建失败@@@");
        }
    }
    public static String assetFilePath(Context context, String assetName) {
        File file = new File(context.getFilesDir(), assetName);

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        } catch (IOException e) {
            Log.e("IN101", "Error process asset " + assetName + " to file path");
        }
        return null;
    }
}
