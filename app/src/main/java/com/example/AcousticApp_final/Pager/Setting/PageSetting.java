package com.example.AcousticApp_final.Pager.Setting;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AcousticApp_final.App.MyApp;
import com.example.AcousticApp_final.Config.ConfigModel;
import com.example.AcousticApp_final.Model.ModelAdapter;
import com.example.AcousticApp_final.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PageSetting extends Fragment {
    //外部调用
    public Activity activity;
    public Context context;
    public View root;

    //控件
    public TextView terminal;
    public ScrollView scrollView;
    public ImageButton bt_trash;

    //事件定义
    private EventsSetting eventsSetting;
    //模型选择
    public Button bt_model;
    private LinearLayout trainLinearLayout;
    protected PopupWindow trainPopWindow;
    protected ModelAdapter modelAdapter;
    protected List<String> modelList;
    //修改th
    protected Button bt_th;
    protected EditText et_th1,et_th2;
    //处理方法选择
    protected Button bt_proccess;

    //测试数据
    protected Button bt_test;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.pager_setting, container, false);//设置对应布局
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.activity=getActivity();
        this.context=getContext();
        MyApp.pageSetting=this;
        //获取控件
        terminal=(TextView)root.findViewById(R.id.textview_setting_terminal);
        scrollView=(ScrollView)root.findViewById(R.id.scrollview_setting_terminal);
        bt_trash=(ImageButton)root.findViewById(R.id.imagebutton_setting_terminal_trash);
        initModelChoose();
        initProccessChoice();
        initThChange();
        initTest();
        //事件设置
        eventsSetting=new EventsSetting();
        eventsSetting.setEvents(this);

    }
    private void initModelChoose(){
        bt_model=(Button)root.findViewById(R.id.button_setting_model);
        bt_model.setText(ConfigModel.model[0]);
        trainLinearLayout=(LinearLayout)LayoutInflater.from(context).inflate(R.layout.popupwindow_recording_train,null);

        trainPopWindow=new PopupWindow(trainLinearLayout,1200,450);
        trainPopWindow.setOutsideTouchable(true);

        modelList = new ArrayList<>(Arrays.asList(ConfigModel.model));

        modelAdapter=new ModelAdapter(context,modelList);
        RecyclerView recyclerView=trainLinearLayout.findViewById(R.id.recyclerview_model);
        recyclerView.setAdapter(modelAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }
    private void initProccessChoice(){
        bt_proccess=(Button)root.findViewById(R.id.button_setting_proccess);
    }
    private void initThChange(){
        bt_th=(Button)root.findViewById(R.id.button_setting_th);
        et_th1=(EditText)root.findViewById(R.id.edittext_setting_th1);
        et_th2=(EditText)root.findViewById(R.id.edittext_setting_th2);
    }
    private void initTest(){
        bt_test=(Button)root.findViewById(R.id.button_setting_test);
    }



}
