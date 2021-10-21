package com.example.AcousticApp_final.BroadCast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.AcousticApp_final.App.MyApp;

/**
 * @author: wzt
 * @date: 2020/11/20
 */
public class MyBroadCastReceiver extends BroadcastReceiver {
    String info,str;
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            case "com.INFO":
                info=intent.getStringExtra("info");
                Toast.makeText(context,info,Toast.LENGTH_SHORT).show();
                break;
            case "com.LOGI":
                info=intent.getStringExtra("info");
                MyApp.pageSetting.terminal.append("\nI: "+info);
                MyApp.pageSetting.scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                //str= MyApp.pageSetting.terminal.getText().toString();
                //str+="\n"+"I: "+info;
                //MyApp.pageSetting.terminal.setText(str);
                break;
            case "com.LOGW":
                info=intent.getStringExtra("info");
                MyApp.pageSetting.terminal.append("\nW: "+info);
                MyApp.pageSetting.scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                //str=MyApp.pageSetting.terminal.getText().toString();
                //str+="\n"+"W: "+info;
                //MyApp.pageSetting.terminal.setText(str);
            case "com.TRASH":
                MyApp.pageSetting.terminal.setText("~Acoustic:");
            default:break;
        }
    }
}
