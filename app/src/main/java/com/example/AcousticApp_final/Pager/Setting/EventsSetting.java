package com.example.AcousticApp_final.Pager.Setting;

import android.view.View;

import com.example.AcousticApp_final.App.MyApp;
import com.example.AcousticApp_final.Model.ModelAdapter;
import com.example.AcousticApp_final.Proccess.ProccessManager;
import com.example.AcousticApp_final.Test.TestThread;
import com.example.AcousticApp_final.Utils.UtilSys;

/**
 * @author: wzt
 * @date: 2020/12/2
 */
public class EventsSetting {
    private PageSetting PS;
    public  void setEvents(PageSetting pageSetting){
        PS=pageSetting;
        setOnClick();
    }
    private void setOnClick(){
        PS.bt_trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PS.terminal.setText("~Acoustic:");
            }
        });
        //开始测试数据
        PS.bt_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.testFlag=1;
                TestThread.startTest();
            }
        });
        //切换模型
        PS.bt_model.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PS.trainPopWindow.showAsDropDown(v);
            }
        });
        //模型适配事件触发
        PS.modelAdapter.setModelListener(new ModelAdapter.ModelListener() {
            @Override
            public void onClick(int pos) {
                String modelName=PS.modelList.get(pos);
                PS.bt_model.setText(modelName);
                //Toast.makeText(PS.context,PS.modelList.get(pos), Toast.LENGTH_SHORT).show();
                UtilSys.LOG_I("更换模型为："+modelName);
                MyApp.classifier.UpdateClassifier(modelName);
                PS.trainPopWindow.dismiss();
            }
        });
        PS.bt_proccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ProccessManager.type==1) {
                    ProccessManager.type = 2;
                    PS.bt_proccess.setText("proccess_2");
                    UtilSys.LOG_I("切换处理方法为2(谱熵法)");
                    UtilSys.sendInfo("切换处理方法为2(谱熵法)");
                }
                else {
                    ProccessManager.type = 1;
                    PS.bt_proccess.setText("proccess_1");
                    UtilSys.LOG_I("切换处理方法为1");
                    UtilSys.sendInfo("切换处理方法为1");
                }
            }
        });
        PS.bt_th.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_th1=PS.et_th1.getText().toString().trim();
                String str_th2=PS.et_th2.getText().toString().trim();
                if (str_th1.isEmpty()||str_th1.isEmpty()){
                    UtilSys.LOG_W("还没有输入！使用默认参数 th1=0.7 th2=0.8");
                    UtilSys.sendInfo("无输入！！");
                    ProccessManager.th1=0.7f;
                    ProccessManager.th2=0.8f;
                    return;
                }
                float th1,th2;
                try {
                    th1=Float.parseFloat(str_th1);
                    th2=Float.parseFloat(str_th2);
                }catch (NumberFormatException e){
                    UtilSys.LOG_W("输入非float型！！");
                    UtilSys.LOG_W("th1 和 th2 更新失败");
                    UtilSys.sendInfo("请输入float型！！");
                    return;
                }
                ProccessManager.th1=th1;
                ProccessManager.th2=th2;
                UtilSys.LOG_I("th1="+th1+"  th2="+th2);
                UtilSys.sendInfo("th1 和 th2 更新成功~~");
            }
        });
    }
}
