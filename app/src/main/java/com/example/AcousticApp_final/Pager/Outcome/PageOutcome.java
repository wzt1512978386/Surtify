package com.example.AcousticApp_final.Pager.Outcome;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.AcousticApp_final.App.MyApp;
import com.example.AcousticApp_final.R;


public class PageOutcome extends Fragment {
    //外部调用
    public Activity activity;
    public Context context;
    public View root;

    //控件定义
    protected Button bt_switch,bt_last,bt_next;
    public TextView result;
    //图片显示id
    protected int curImageId=2;

    //事件
    EventsOutcome eventsOutcome;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.pager_outcome, container, false);//设置对应布局
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.activity=getActivity();
        this.context=getContext();
        MyApp.pageOutcome=this;

        bt_switch=(Button)root.findViewById(R.id.button_outcome_switch);
        bt_last=(Button)root.findViewById(R.id.button_outcome_lastone);
        bt_next=(Button)root.findViewById(R.id.button_outcome_nextone);

         eventsOutcome=new EventsOutcome(this);
         eventsOutcome.setEvent();

         //结果
        result=(TextView)root.findViewById(R.id.textview_outcome_result);
    }


}
