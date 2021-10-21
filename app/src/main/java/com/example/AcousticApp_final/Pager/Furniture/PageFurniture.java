package com.example.AcousticApp_final.Pager.Furniture;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AcousticApp_final.App.MyApp;
import com.example.AcousticApp_final.R;

import java.util.ArrayList;
import java.util.List;


public class PageFurniture extends Fragment {
    //外部调用
    public Activity activity;
    public Context context;
    public View root;
    //家具列表
    List<FurnitureEntity> furnitureList;
    TextView connectinfo;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.pager_furniture, container, false);//设置对应布局
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.activity=getActivity();
        this.context=getContext();
        MyApp.pageFurniture=this;
        initFurniture();
    }
    private void initFurniture(){
        connectinfo=(TextView)root.findViewById(R.id.textview_furniture_connectinfo);
        furnitureList=new ArrayList<>();
        /*
        furnitureList.add(new FurnitureEntity("空调",R.drawable.p_image_furniture_airconditioning));
        furnitureList.add(new FurnitureEntity("吊灯",R.drawable.p_image_furniture_chandelier));
        furnitureList.add(new FurnitureEntity("电脑",R.drawable.p_image_furniture_computer));
        furnitureList.add(new FurnitureEntity("风扇",R.drawable.p_image_furniture_fan));
        furnitureList.add(new FurnitureEntity("油烟机",R.drawable.p_image_furniture_rangehood));
        furnitureList.add(new FurnitureEntity("台灯",R.drawable.p_image_furniture_tablelamp));
        furnitureList.add(new FurnitureEntity("电视",R.drawable.p_image_furniture_tv));
        furnitureList.add(new FurnitureEntity("洗衣机",R.drawable.p_image_furniture_washing));
        furnitureList.add(new FurnitureEntity("摄像头",R.drawable.p_image_furniture_webcam));
        */
        furnitureList.add(new FurnitureEntity("Air conditioner",R.drawable.p_image_furniture_airconditioning));
        furnitureList.add(new FurnitureEntity("Pendent lamp",R.drawable.p_image_furniture_chandelier));
        furnitureList.add(new FurnitureEntity("Computer",R.drawable.p_image_furniture_computer));
        furnitureList.add(new FurnitureEntity("Electric fan",R.drawable.p_image_furniture_fan));
        furnitureList.add(new FurnitureEntity("Lampblack machine",R.drawable.p_image_furniture_rangehood));
        furnitureList.add(new FurnitureEntity("Desk lamp",R.drawable.p_image_furniture_tablelamp));
        furnitureList.add(new FurnitureEntity("Television",R.drawable.p_image_furniture_tv));
        //furnitureList.add(new FurnitureEntity("Washing machine",R.drawable.p_image_furniture_washing));
        furnitureList.add(new FurnitureEntity("Camera",R.drawable.p_image_furniture_webcam));

        furnitureList.add(new FurnitureEntity("",R.drawable.p_null));
        furnitureList.add(new FurnitureEntity("",R.drawable.p_null));
        furnitureList.add(new FurnitureEntity("",R.drawable.p_null));
        furnitureList.add(new FurnitureEntity("",R.drawable.p_null));

        furnitureList.add(new FurnitureEntity("Bluetooth",R.drawable.p_image_furniture_bluetooth));
        furnitureList.add(new FurnitureEntity("Music",R.drawable.p_image_furniture_music2));
        furnitureList.add(new FurnitureEntity("Dictionary",R.drawable.p_image_furniture_dictionary));
        furnitureList.add(new FurnitureEntity("Alarm clock",R.drawable.p_image_furniture_clock));

        RecyclerView recyclerView=(RecyclerView)root.findViewById(R.id.recyclerview_furniture);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(context,4);
        FurnitureAdapter furnitureAdapter=new FurnitureAdapter(context,furnitureList);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(furnitureAdapter);
        furnitureAdapter.setFurnitureListener(new FurnitureAdapter.FurnitureListener() {
            @Override
            public void onClick(int pos, ImageView iteminfo) {
                if(furnitureList.get(pos).name.isEmpty())
                    return;
                iteminfo.setImageResource(R.drawable.p_button_furniture_wait);
                iteminfo.setVisibility(View.VISIBLE);
                connectinfo.setText(furnitureList.get(pos).name+"\n is connecting...");
                MyApp.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iteminfo.setImageResource(R.drawable.p_button_furniture_done);
                        connectinfo.setText(furnitureList.get(pos).name+"\n connected successfully!");
                    }
                }, 2000);
            }
        });

    }


}
