package com.example.AcousticApp_final.Pager.Document;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AcousticApp_final.App.MyApp;
import com.example.AcousticApp_final.Pager.Document.Folder.FolderAdapter;
import com.example.AcousticApp_final.Pager.Document.Folder.FolderEntity;
import com.example.AcousticApp_final.Pager.Document.Folder.FolderManager;
import com.example.AcousticApp_final.Pager.Document.WavFile.WavAdapter;
import com.example.AcousticApp_final.Pager.Document.WavFile.WavEntity;
import com.example.AcousticApp_final.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class PageDocument extends Fragment {
    //外部调用
    public Activity activity;
    public Context context;
    public View root;
    //控件定义
    //列表相关定义
    protected RecyclerView recyclerView;


    //文件夹
    public List<FolderEntity> folderList;
    public FolderAdapter folderAdapter;
    //音频文件
    public File curFolder;//当前的文件夹
    public List<WavEntity> wavList;
    public WavAdapter wavAdapter;
    //事件管理工具
    private EventsDocument eventsDocument;
    //folder弹出窗口
    protected PopupWindow folderPopupWindow;
    protected LinearLayout folderSettingWindow;
    protected Button bt_deleteFolder;
    //folder弹出窗口
    protected PopupWindow wavPopupWindow;
    protected LinearLayout wavSettingWindow;
    protected Button bt_deleteWav;
    protected Button bt_identifyWav;

    //文件路径栈
    protected LinearLayout pathStack;
    protected float XpathStack;
    protected Button bt_stack1,bt_stack2,bt_stack3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.pager_document, container, false);//设置对应布局
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.activity=getActivity();
        this.context=getContext();
        MyApp.pageDocument=this;
        eventsDocument=new EventsDocument(this);
        initRecycler();
        initPopupWindow();
        initPathStack();
        eventsDocument.setEvent();
    }
    private void initPathStack(){
        pathStack=(LinearLayout)root.findViewById(R.id.linearlayout_document_section_top);
        XpathStack=pathStack.getX();
        final boolean[] flagPathStack = {true};
        pathStack.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(flagPathStack[0]) {
                    pathStack.setTranslationX(XpathStack + pathStack.getWidth() / 3f * 2);
                    flagPathStack[0] = false;
                }
            }
        });
        bt_stack1=(Button)root.findViewById(R.id.button_document_section_top_1);
        bt_stack2=(Button)root.findViewById(R.id.button_document_section_top_2);
        bt_stack3=(Button)root.findViewById(R.id.button_document_section_top_3);
    }
    private void initPopupWindow(){
        //folder
        folderSettingWindow =(LinearLayout) LayoutInflater.from(context).inflate(R.layout.popupwindow_document_item_folder,null);
        bt_deleteFolder =(Button) folderSettingWindow.findViewById(R.id.button_document_item_folder_delete);
        folderPopupWindow =new PopupWindow(folderSettingWindow,250,200);
        folderPopupWindow.setOutsideTouchable(true);
        //wav
        wavSettingWindow =(LinearLayout) LayoutInflater.from(context).inflate(R.layout.popupwindow_document_item_wav,null);
        bt_deleteWav =(Button) wavSettingWindow.findViewById(R.id.button_document_item_wav_delete);
        bt_identifyWav =(Button) wavSettingWindow.findViewById(R.id.button_document_item_wav_identify);
        wavPopupWindow =new PopupWindow(wavSettingWindow,250,400);
        wavPopupWindow.setOutsideTouchable(true);
    }
    private void initRecycler(){
        initFolder();
        initWavFile();
        recyclerView=(RecyclerView)root.findViewById(R.id.recyclerview_document);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
        recyclerView.setAdapter(folderAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }
    private void initFolder(){
        folderList=new ArrayList<>();
        folderList= FolderManager.initFolder(folderList);
        folderAdapter=new FolderAdapter(context,folderList);
    }
    private void initWavFile(){
        wavList=new ArrayList<>();
        //wavList= WavManager.initWavFile(wavList,userFile);
        wavAdapter=new WavAdapter(context,wavList);
    }



}
