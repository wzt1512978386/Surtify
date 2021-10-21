package com.example.AcousticApp_final.UI.Recording;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.AcousticApp_final.App.MyApp;
import com.example.AcousticApp_final.R;

/**
 * @author: wzt
 * @date: 2020/11/17
 */
public class FrameRecording extends FrameLayout {
    //外部调用
    public Context context;

    //空间变量
    private float W,H;//控件尺寸
    private float Xcenter,Ycenter;//框架中心坐标
    private float Wb,Hb;//按钮尺寸


    //三个按钮
    private ImageButton[]button;
    private int []buttonId={R.id.imagebutton_recording_correct,R.id.imagebutton_recording_play,R.id.imagebutton_recording_wrong};
    //时间控件
    private TextView time;
    //信息框
    private LinearLayout section;
    //动画
    private ValueAnimator buttonAnimator;
    private ValueAnimator timeAnimator;
    private ValueAnimator sectionAnimator;

    public FrameRecording(@NonNull Context context) {
        super(context);this.context=context;setWillNotDraw(false);
    }
    public FrameRecording(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);this.context=context;setWillNotDraw(false);
    }
    private void init(){
        MyApp.frameRecording=this;
        W=getWidth();
        H=getHeight();
        Xcenter=W/2;
        Ycenter=H/2;

        initTime();
        initTimeUp();
        initButton();
        initButtonRoate();
        initSection();
        initSectionDown();
    }

    private float Wsection,Hsection;
    private float Xsection,Ysection;
    private void initSection(){
        section=(LinearLayout)findViewById(R.id.linearlayout_recording_section_bottom);
        Wsection=section.getWidth();
        Hsection=section.getHeight();
        Xsection=section.getX();
        Ysection=section.getY();
    }

    private float Wtime,Htime;//时间尺寸
    private float Xtime,Ytime;//时间位置
    private void initTime(){
        time=(TextView)findViewById(R.id.textview_recording_time);
        Wtime=time.getWidth();
        Htime=time.getHeight();
        Xtime=time.getX();
        Ytime=time.getTop();
        //Log.i("IN101","Xtime"+Xtime+"   Ytime"+Ytime);
    }
    private void initButton(){
        button=new ImageButton[3];
        for(int i=0;i<3;i++) {
            button[i] = (ImageButton) findViewById(buttonId[i]);
            Wb=button[i].getWidth();
            Hb=button[i].getHeight();
            //Log.i("IN101","Wb"+Wb);
            //button[i].setX(W/2-Wb/2);
            button[i].setX(-W/2-Wb);
            button[i].setY(H/2-Hb/2);
        }
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
    private void initButtonRoate() {
        final float[] angleB = {62, 90, 118};
        final float angleScale = (float) Math.PI / 180f;
        buttonAnimator = ValueAnimator.ofFloat(0, 1);
        buttonAnimator.setDuration(1500);
        buttonAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (float) animation.getAnimatedValue();
                for (int i = 0; i < 3; i++) {
                    float cos = (float) Math.cos(angleScale * (180 + val * angleB[i]));
                    float sin = (float) Math.sin(angleScale * (180 + val * angleB[i]));
                    button[i].setX(W / 2 + cos * (W / 2+Wb/2) - Wb / 2);
                    button[i].setY(H / 2 + sin * H / 4 - Hb / 2);
                }
                MyApp.pageRecording.bt_Control.setAlpha(1-val);

            }
        });
        buttonAnimator.setInterpolator(new BounceInterpolator());

    }
    private void initTimeUp(){
        timeAnimator=ValueAnimator.ofFloat(0,1);
        timeAnimator.setDuration(1500);
        timeAnimator.setInterpolator(new DecelerateInterpolator());
        timeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (float) animation.getAnimatedValue();
                time.setY(Ytime+(0-Ytime)*val);
                //time.setY(Ytime+(Ycenter-Ytime)*val);
                time.setScaleX(1+val*(W/2-Wtime)/Wtime);
                time.setScaleY(1+val*(W/2-Wtime)/Wtime);
            }
        });
        timeAnimator.setInterpolator(new BounceInterpolator());
    }
    private void initSectionDown(){
        sectionAnimator=ValueAnimator.ofFloat(0,1);
        sectionAnimator.setDuration(1500);
        sectionAnimator.setInterpolator(new DecelerateInterpolator());
        sectionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (float) animation.getAnimatedValue();
                section.setY(Ysection+Hsection*7/12*val);
            }
        });

    }

    public void startButtonRoate(){
        timeAnimator.start();
        buttonAnimator.start();
    }
    public void reverseButtonRoate(){
        //buttonAnimator.setInterpolator(new DecelerateInterpolator());
        buttonAnimator.reverse();
        timeAnimator.reverse();
    }
    public void startSectionDown(){
        section.setY(Ysection+Hsection*7/12);
        //sectionAnimator.setInterpolator(new DecelerateInterpolator());
        //sectionAnimator.start();
    }
    public void reverseSectionDown(){
        sectionAnimator.setInterpolator(new AccelerateInterpolator());
        sectionAnimator.reverse();
    }

}
