package com.example.AcousticApp_final.UI.Outcome;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.AcousticApp_final.App.MyApp;

/**
 * @author: wzt
 * @date: 2020/11/17
 */
public class FrameOutcome extends FrameLayout {
    //外部调用
    public Context context;

    //空间变量
    private float W,H;


    public FrameOutcome(@NonNull Context context) {
        super(context);this.context=context;setWillNotDraw(false);
    }
    public FrameOutcome(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);this.context=context;setWillNotDraw(false);
    }
    private void init(){
        MyApp.frameOutcome=this;
        W=getWidth();
        H=getHeight();
    }

    private boolean initFlag=true;
    @Override
    protected void onDraw(Canvas canvas) {
        if(initFlag){
            init();
            initFlag=false;
        }
        super.onDraw(canvas);
    }





}
