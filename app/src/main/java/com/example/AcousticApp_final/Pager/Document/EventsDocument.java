package com.example.AcousticApp_final.Pager.Document;

import android.view.View;

import com.example.AcousticApp_final.App.MyApp;
import com.example.AcousticApp_final.Config.ConfigAudio;
import com.example.AcousticApp_final.Pager.Document.Folder.FolderAdapter;
import com.example.AcousticApp_final.Pager.Document.Folder.FolderManager;
import com.example.AcousticApp_final.Pager.Document.WavFile.WavAdapter;
import com.example.AcousticApp_final.Pager.Document.WavFile.WavManager;
import com.example.AcousticApp_final.Pager.Recording.EventsRecording;

import java.io.File;

/**
 * @author: wzt
 * @date: 2020/11/19
 */
public class EventsDocument {
    private PageDocument PD;
    public EventsDocument(PageDocument pageDocument){
        this.PD=pageDocument;
    }
    public void setEvent(){
        setOnClick();
    }

    private void setOnClick(){
        //folder item
        PD.folderAdapter.setFolderListener(new FolderAdapter.FolderListener() {
            @Override
            public void onClick(int pos) {
                PD.curFolder=PD.folderList.get(pos).folderFile;//设置当前要显示的文件夹
                PD.wavList= WavManager.initWavFile(PD.wavList,PD.curFolder);//获取当前文件夹下的wav列表
                PD.wavAdapter.notifyDataSetChanged();
                PD.recyclerView.setAdapter(PD.wavAdapter);
                PD.pathStack.setTranslationX(PD.XpathStack+PD.pathStack.getWidth()/3f);
                PD.bt_stack2.setText(PD.folderList.get(pos).userID);

            }
            @Override
            public void onClickSetting(View v) {
                PD.folderPopupWindow.showAsDropDown(v);
            }
        });

        //删除Folder
        PD.bt_deleteFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FolderManager.deleteFolder(PD.folderList.get(PD.folderAdapter.settingIndex).folderFile);
                PD.folderPopupWindow.dismiss();//关闭popup窗口
            }
        });

        //wav item
        PD.wavAdapter.setWavListener(new WavAdapter.WavListener() {
            @Override
            public void onClick(int pos) {

            }
            @Override
            public void onClickSetting(View v) {
                PD.wavPopupWindow.showAsDropDown(v);
            }
        });

        //删除Wav
        PD.bt_deleteWav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WavManager.deleteWav(PD.wavList.get(PD.wavAdapter.settingIndex).wavFile);
                PD.wavPopupWindow.dismiss();//关闭popup窗口
            }
        });
        //选择Wav文件
        PD.bt_identifyWav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PD.wavPopupWindow.dismiss();//关闭popup窗口

                if (MyApp.pageRecording.eventsRecording.state == EventsRecording.STATE.INIT) {
                    MyApp.pageRecording.bt_Control.callOnClick();
                    MyApp.pageRecording.bt_Control.callOnClick();
                }


                File wavFile = PD.wavList.get(PD.wavAdapter.settingIndex).wavFile;
                MyApp.pageRecording.trainFile = wavFile;
                MyApp.pageRecording.update(PD.curFolder.getName(), wavFile.getName().split("\\.")[0]);
                float time=(float) (wavFile.length()-42)/(ConfigAudio.getChannelAttr()*ConfigAudio.getEncodeAttr()*ConfigAudio.getSampleRateAttr()/8f);
                MyApp.pageRecording.recordTime=(int)(time*60);
                MyApp.pageRecording.eventsRecording.updateTime();
                //MyApp.pageRecording.eventsRecording.state= EventsRecording.STATE.FINISH;

                MyApp.mainActivity.viewPager.setCurrentItem(2, true);
            }
        });


        //path stack 1-2-3 : folder-wav-none
        PD.bt_stack1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PD.pathStack.setTranslationX(PD.XpathStack+PD.pathStack.getWidth()/3f*2);
                PD.recyclerView.setAdapter(PD.folderAdapter);
            }
        });
        PD.bt_stack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PD.pathStack.setTranslationX(PD.XpathStack+PD.pathStack.getWidth()/3f);
                PD.recyclerView.setAdapter(PD.wavAdapter);
            }
        });
        PD.bt_stack3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PD.pathStack.setTranslationX(PD.XpathStack);
            }
        });

    }


}
