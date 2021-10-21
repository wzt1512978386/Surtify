package com.example.AcousticApp_final.Pager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import com.example.AcousticApp_final.Config.ConfigPager;
import com.example.AcousticApp_final.Pager.Document.PageDocument;
import com.example.AcousticApp_final.Pager.Furniture.PageFurniture;
import com.example.AcousticApp_final.Pager.Outcome.PageOutcome;
import com.example.AcousticApp_final.Pager.Recording.PageRecording;
import com.example.AcousticApp_final.Pager.Setting.PageSetting;

import java.util.ArrayList;
import java.util.List;

//页面的适配器
public class PaperAdapter extends FragmentPagerAdapter {
    //外部调用
    private List<Fragment>list=new ArrayList();//页面为Fragment，故列表类型为Fragment

    public PaperAdapter(Context context, @NonNull FragmentManager fm) {
        super(fm);
        //按顺序定义五个页面
        PageFurniture pageFurniture=new PageFurniture();
        PageDocument pageDocument=new PageDocument();
        PageRecording pageRecording=new PageRecording();
        PageOutcome pageOutcome=new PageOutcome();
        PageSetting pageSetting=new PageSetting();


        //放进适配器列表
        list.add(pageFurniture);
        list.add(pageDocument);
        list.add(pageRecording);
        list.add(pageOutcome);
        list.add(pageSetting);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return ConfigPager.PAGER_NUM;
    }
}
