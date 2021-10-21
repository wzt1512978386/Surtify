package com.example.AcousticApp_final.UI.Outcome;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.AcousticApp_final.R;

/**
 * @author: wzt
 * @date: 2020/11/19
 */
public class ViewZoomChart extends FrameLayout {
    //外部调用
    public Context context;
    //空间变量
    private float W,H;//框架长宽
    private float Wc,Hc;//chart长宽
    private float Wpad,Hpad;
    //绘制工具
    private Paint mPaint,gPaint,bPaint;//背景、网格、折线图

    //enum STATE{}
    public ViewZoomChart(@NonNull Context context) {
        super(context);this.context=context;setWillNotDraw(false);
    }
    public ViewZoomChart(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);this.context=context;setWillNotDraw(false);
    }
    private void init(){
        W=getWidth();
        H=getHeight();
        Wc=W-W/8;
        Hc=H-H/8 ;
        Wpad=(W-Wc)/2;
        Hpad=(H-Hc)/2;

        //框架
        setBackgroundColor(getResources().getColor(R.color.styleLevel_003));
        //绘制工具初始化
        mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);//绘制背景
        mPaint.setAntiAlias(true);
        mPaint.setColor(getResources().getColor(R.color.white));
        mPaint.setStyle(Paint.Style.FILL);

        bPaint=new Paint(Paint.ANTI_ALIAS_FLAG);//绘制折线图
        bPaint.setAntiAlias(true);
        bPaint.setColor(getResources().getColor(R.color.black));
        bPaint.setStyle(Paint.Style.STROKE);
        bPaint.setStrokeWidth(2);

        gPaint=new Paint(Paint.ANTI_ALIAS_FLAG);//绘制网格
        gPaint.setAntiAlias(true);
        gPaint.setColor(getResources().getColor(R.color.black));
        gPaint.setStyle(Paint.Style.STROKE);
        gPaint.setStrokeWidth(2);

    }



    enum STATE{NOTHING,DRAW}
    STATE state= STATE.NOTHING;

    private boolean initFlag=true;
    @Override
    protected void onDraw(Canvas canvas) {
        if(initFlag){
            init();
            initFlag=false;
        }
        switch (state){
            case NOTHING:
                drawCoor(canvas);
        }
        super.onDraw(canvas);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
        //return super.onInterceptTouchEvent(ev);
    }




    private int touchMode=0;
    private int scaleType=0;//0为X，1为Y
    private float Xd,Yd;//双点距离
    private float Xdc=0,Ydc=0;
    private float Xs,Ys;//点一开始的坐标
    private float XscalePre=1,YscalePre=1;
    private float Xscale=1,Yscale=1;
    private float Xpos=0,Ypos=0;
    private float XposPre=0,YposPre=0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                touchMode=1;
                Xs=event.getX();
                Ys=event.getY();
                Log.i("IN101", "down");
                return true;
            case MotionEvent.ACTION_MOVE:
                //Log.i("IN101", "move");
                if(touchMode==2){
                    if(scaleType==0){
                        float d=Math.abs(event.getX(0)-event.getX(1));
                        Xscale=XscalePre*d/Xd;
                        Xscale=Math.max(Xscale,1);

                    }
                    else if(scaleType==1){
                        float d=Math.abs(event.getY(0)-event.getY(1));
                        Yscale=YscalePre*d/Yd;
                        Yscale=Math.max(Yscale,1);
                    }
                }
                else if(touchMode==1){
                    float Xt=event.getX();
                    float Yt=event.getY();

                    Xpos=XposPre-Xs+Xt;
                    //if(Xpos>0||Xpos<(1-Xscale)*Wc)
                      //  Xpos=XposPre;
                    Xpos=Math.min(0,Math.max(Xpos,Wc*(1-Xscale)));
                    Ypos=YposPre-Ys+Yt;
                    Ypos=Math.min(0,Math.max(Ypos,Hc*(1-Yscale)));
                    //if(Ypos>0||Ypos<(1-Yscale)*Hc)
                        //Ypos=YposPre;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                touchMode=2;
                if(Math.abs(event.getX(0)-event.getX(1))>Math.abs(event.getY(0)-event.getY(1))) {
                    Xd=Math.abs(event.getX(0)-event.getX(1));
                    scaleType = 0;//X缩放
                }
                else {
                    Yd=Math.abs(event.getY(0)-event.getY(1));
                    scaleType = 1;//Y缩放
                }
                Xdc=(event.getX(0)+event.getX(1))/2;
                Ydc=(event.getY(0)+event.getY(1))/2;

                //Log.i("IN101", "two");
                break;
            case MotionEvent.ACTION_UP:
                touchMode=0;
                XscalePre=Xscale;
                YscalePre=Yscale;
                XposPre=Xpos;
                YposPre=Ypos;
                Xdc=Ydc=0;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                touchMode=1;
                break;
        }

        //return true;
        return super.onTouchEvent(event);
    }



    private int gridXnum=10,gridYnum=8;
    private void drawCoor(Canvas canvas){

        float Yper=Hc/gridYnum*Yscale;
        float Xper=Wc/gridXnum*Xscale;
        canvas.drawRect(Wpad,Hpad,W-Wpad,H-Hpad,mPaint);
        float xt=(1-Xscale/XscalePre)*(Xdc-Xpos);
        float yt=(1-Yscale/YscalePre)*(Ydc-Ypos);

        for(int i=0;i<gridXnum;i++){
            canvas.drawLine(rangeX(Xpos+Wpad+i*Xper+xt),Hpad,rangeX(Xpos+Wpad+i*Xper+xt),H-Hpad,gPaint);
        }
        for(int i=0;i<gridYnum;i++){
            canvas.drawLine(Wpad,rangeY(Ypos+Hpad+i*Yper+yt),W-Wpad,rangeY(Ypos+Hpad+i*Yper+yt),gPaint);
        }
        if(Xdc!=0&&Ydc!=0){
            canvas.drawCircle(Xdc,Ydc,4,gPaint);
        }
    }
    private float rangeX(float x){
        return Math.max(Math.min(x,W- Wpad),Wpad);
    }
    private float rangeY(float y){
        return Math.max(Math.min(y,H- Hpad),Hpad);
    }

}
