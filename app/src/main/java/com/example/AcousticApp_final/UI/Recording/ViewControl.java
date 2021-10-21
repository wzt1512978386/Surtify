package com.example.AcousticApp_final.UI.Recording;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.AcousticApp_final.R;

/**
 * @author: wzt
 * @date: 2020/11/17
 */
public class ViewControl extends View {
    //外部调用
    public Context context;

    public ViewControl(Context context) {
        super(context);
        this.context=context;
    }
    public ViewControl(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
    }

    //长度变量定义
    private float W,H;
    //绘画工具
    private Paint mPaint;
    //初始化
    private boolean initFlag=true;
    private void init(){
        W=getWidth();
        H=getHeight();
        mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        //mPaint.setColor(getResources().getColor(R.color.styleLevel_004));
        mPaint.setColor(getResources().getColor(R.color.white));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(30);
        //mPaint.setAlpha(128);
    }

    //状态
    public enum STATE{STOP,RECORD}
    STATE state=STATE.RECORD;
    @Override
    protected void onDraw(Canvas canvas) {
        if (initFlag){
            init();
            initFlag=false;
        }
        super.onDraw(canvas);
        switch (state){
            case RECORD:
                drawRECORD(canvas);
                break;
            case STOP:
                drawPAUSE(canvas);
                break;
        }
    }

    private void drawRECORD(Canvas canvas){
        float Xc=W/2,Yc=H/2;//center
        float Wi=W/2,Yi=H/2;//inside
        float r= Math.min(Wi,Yi)/2;
        canvas.drawCircle(Xc,Yc,r,mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(Xc,Yc,r/4,mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
    }
    private void drawPAUSE(Canvas canvas){
        float Xc=W/2,Yc=H/2;//center
        float Wi=W/2,Yi=H/2;//inside
        float r= Math.min(Wi,Yi)/2;
        canvas.drawCircle(Xc,Yc,r,mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(new RectF(Xc-r/3,Yc-r/3,Xc+r/3,Yc+r/3),mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
    }
    public void changeState(){
        if (state==STATE.RECORD)
            state=STATE.STOP;
        else
            state=STATE.RECORD;
        invalidate();
    }

}
