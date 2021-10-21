package com.example.AcousticApp_final;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.AcousticApp_final.App.MyApp;
import com.example.AcousticApp_final.BroadCast.MyBroadCastReceiver;
import com.example.AcousticApp_final.Config.ConfigPager;
import com.example.AcousticApp_final.Pager.PaperAdapter;
import com.example.AcousticApp_final.Proccess.PythonManager;
import com.example.AcousticApp_final.Utils.UtilFile;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    //外部调用
    public  Activity activity;
    public  Context context;

    //控件定义
    public ViewPager viewPager;//页面
    private TabLayout tabLayout;//菜单栏
    private PagerAdapter pagerAdapter;//页面适配器
    private View []tabButton;//菜单栏按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity=this;
        context=this;
        MyApp.context=context;
        MyApp.mainActivity=this;
        MyApp.requestPermission(activity);//权限申请
        MyApp.pythonManager=new PythonManager(this);//python工具初始化
        UtilFile.initFile();//文件初始化
        initPager();
        initTab();
        initBroadcast();

    }
    private void initBroadcast(){
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("com.INFO");
        intentFilter.addAction("com.LOGI");
        intentFilter.addAction("com.LOGW");
        intentFilter.addAction("com.TRASH");
        MyBroadCastReceiver myBroadcastReceiver=new MyBroadCastReceiver();
        registerReceiver(myBroadcastReceiver,intentFilter);
    }
    private void initPager(){
        viewPager=findViewById(R.id.viewpager_main);
        pagerAdapter=new PaperAdapter(this,getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(ConfigPager.PAGER_NUM);//设置缓存页面数！！！,如果过少会导致页面被清理掉
        viewPager.setCurrentItem(ConfigPager.PAGER_INDEX);//设置
    }
    private void initTab(){
        //定义菜单栏按钮名称
        //final String []title={"家具","文件","","输出","设置"};
        final String []title={"Furniture","Document","","Outcome","Setting"};
        //定义菜单栏按钮图标
        final Integer []Rid={
                R.drawable.p_button_main_tab_furniture,
                R.drawable.p_button_main_tab_document,0,
                R.drawable.p_button_main_tab_outcome,
                R.drawable.p_button_main_tab_setting};

        tabLayout=(TabLayout)findViewById(R.id.tablayout_main);
        tabLayout.setupWithViewPager(viewPager);
        tabButton=new View[ConfigPager.PAGER_NUM];
        for(int i=0;i<ConfigPager.PAGER_NUM;i++) {
            if(i==ConfigPager.PAGER_INDEX)
                continue;
            tabButton[i]= LayoutInflater.from(this).inflate(R.layout.item_main_tab,null);
            tabButton[i].setAlpha(0.5F);
            ImageView imageViews=(ImageView)tabButton[i].findViewById(R.id.iv_tab);
            imageViews.setImageResource(Rid[i]);
            TextView textView=(TextView)tabButton[i].findViewById(R.id.tv_tab);
            textView.setText(title[i]);
            tabLayout.getTabAt(i).setCustomView(tabButton[i]);
        }
        tabLayout.getTabAt(ConfigPager.PAGER_INDEX).select();

        final ImageButton ib_tabm=(ImageButton) findViewById(R.id.imagebutton_main_tab_recording);
        ib_tabm.setY(ib_tabm.getY() + 12);//一开始让它下降
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()!=ConfigPager.PAGER_INDEX)
                    tabButton[tab.getPosition()].setAlpha(1.0f);
                else {
                    ib_tabm.setY(ib_tabm.getY() + 12);

                    //StaticUtil.viewWave.start();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if(tab.getPosition()!=ConfigPager.PAGER_INDEX)
                    tabButton[tab.getPosition()].setAlpha(0.5f);
                else {
                    ib_tabm.setY(ib_tabm.getY() - 12);

                    //StaticUtil.viewWave.stop();
                }
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        ib_tabm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(ConfigPager.PAGER_INDEX);
            }
        });
    }

}