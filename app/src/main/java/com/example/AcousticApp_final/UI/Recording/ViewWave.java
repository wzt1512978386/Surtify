package com.example.AcousticApp_final.UI.Recording;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.AcousticApp_final.App.MyApp;
import com.example.AcousticApp_final.Config.ConfigAudio;
import com.example.AcousticApp_final.Config.ConfigView;
import com.example.AcousticApp_final.R;
import com.example.AcousticApp_final.Utils.MyList;

/**
 * @author: wzt
 * @date: 2020/11/17
 */
public class ViewWave extends View {
    //外部调用
    public Context context;

    public ViewWave(Context context) {
        super(context);
        this.context=context;
    }
    public ViewWave(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
    }

    //长度变量定义
    private float W,H;
    //绘画工具
    private Paint mPaint;
    private Paint bPaint;
    private Paint tPaint;
    private final int []colorID={R.color.styleLevel_001,R.color.styleLevel_003};
    //private final int []colorID={R.color.white,R.color.black};
    private int colorIdex=0;

    //数据工具
    private MyList myList;


    //初始化
    private boolean initFlag=true;
    private void init(){
        MyApp.viewWave=this;//绑定
        W=getWidth();
        H=getHeight();
        //绘制工具初始化
        mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);//绘制背景
        mPaint.setAntiAlias(true);
        mPaint.setColor(getResources().getColor(R.color.styleLevel_003));
        mPaint.setStyle(Paint.Style.FILL);

        bPaint=new Paint(Paint.ANTI_ALIAS_FLAG);//绘制折线图
        bPaint.setAntiAlias(true);
        bPaint.setColor(getResources().getColor(R.color.styleLevel_001));
        bPaint.setStyle(Paint.Style.STROKE);
        bPaint.setStrokeWidth(2);

        tPaint=new Paint(Paint.ANTI_ALIAS_FLAG);//绘制折线图
        tPaint.setAntiAlias(true);
        tPaint.setColor(getResources().getColor(R.color.styleLevel_005));
        tPaint.setStyle(Paint.Style.STROKE);
        tPaint.setStrokeWidth(40);


        //数据工具初始化
        myList=new MyList(ConfigView.Wave.DATA_NUM);
    }

    //状态
    enum STATE{NOTHING,DRAW}
    STATE state=STATE.NOTHING;
    private boolean drawBGFlag=true;
    @Override
    protected void onDraw(Canvas canvas) {
        if (initFlag){
            init();
            initFlag=false;
        }
        super.onDraw(canvas);
        switch (state){
            case NOTHING:
                if(drawBGFlag){
                    drawBGFlag=false;
                    Bitmap mbitmap = Bitmap.createBitmap((int)W, (int)H, Bitmap.Config.ARGB_8888);
                    Canvas mCanvas = new Canvas(mbitmap);
                    drawBG(mCanvas);
                    setBackground(new BitmapDrawable(mbitmap));
                }
                break;
            case DRAW:
                drawRealTime(canvas);
                break;
        }
    }
    //draw background
    private void drawBG(Canvas canvas){
        float Xc=W/2,Yc=H/2;//center
        float Wi=W/2,Yi=H/2;//inside
        float r= Math.min(Wi,Yi)/2;
        canvas.drawCircle(Xc,Yc,r,mPaint);
    }
    private void drawRealTime(Canvas canvas){
        float Xc=W/2,Yc=H/2;//center
        float Wi=W/2,Yi=H/2;//inside
        float r= Math.min(Wi,Yi)/2;
        float anglePer=360f/ConfigView.Wave.DATA_NUM;
        float angleStart=90f;
        float angleScale=(float) Math.PI/180f;
        float Yscale=0.01f;
        float Xp=Xc,Yp=Yc-r,Rp=0;
        //canvas.drawLine();
        Log.i("IN101","qq "+myList.getEnd());
        int Istart=myList.getEnd();
        float cosI=(float) Math.cos((anglePer*Istart-angleStart)*angleScale);
        float sinI=(float) Math.sin((anglePer*Istart-angleStart)*angleScale);
        canvas.drawCircle(Xc+cosI*r,Yc+sinI*r,r/20,tPaint);
        //canvas.drawLine(Xc,Yc,Xc+cos*r/2,Yc*sin*r/2,tPaint);
        for(int i=0;i<myList.getMaxLen();i++){
            float cos=(float) Math.cos((anglePer*i-angleStart)*angleScale);
            float sin=(float) Math.sin((anglePer*i-angleStart)*angleScale);
            float Rt=myList.getAbsolute(i)*Yscale*Math.min((Istart-i+myList.getMaxLen())%myList.getMaxLen(),1000)/1000;
            float Xt=Xc+cos*(r+Rt);
            float Yt=Yc+sin*(r+Rt);

            if(Rt*Rp<0){
                float p_=Math.abs(Rp)/Math.abs(Rt-Rp);
                float t1=1-p_;
                float Xm=Xt*t1+Xp*p_;
                float Ym=Yt*t1+Yp*p_;
                if(Rp<0)
                    colorIdex=0;
                else
                    colorIdex=1;
                bPaint.setColor(getResources().getColor(colorID[colorIdex]));
                canvas.drawLine(Xp,Yp,Xm,Ym,bPaint);
                colorIdex=(colorIdex+1)%2;
                bPaint.setColor(getResources().getColor(colorID[colorIdex]));
                canvas.drawLine(Xm,Ym,Xt,Yt,bPaint);
            }
            else {
                if(Rp<0)
                    colorIdex=0;
                else
                    colorIdex=1;
                bPaint.setColor(getResources().getColor(colorID[colorIdex]));
                canvas.drawLine(Xp, Yp, Xt, Yt, bPaint);
            }
            Xp=Xt;Yp=Yt;Rp=Rt;
        }
        //Log.i("IN101"," "+myList.getStart());

    }

    public void setData(byte []buf,int end) {
        if (buf == null)
            return;
        int channel = ConfigAudio.getChannelAttr();
        //buf_end = end / (2 * channel * ConfigView.Wave.SLICE);
        //BLData_len = buf_end * 8;
        for (int i = 0; i < end; i += channel * 2 * ConfigView.Wave.SLICE) {
            short temp = (short) ((buf[i] & 0x000000FF) | (((int) buf[i + 1]) << 8));
            myList.forcePush(temp);
        }
        state=STATE.DRAW;
        //drawState = DrawState.RealTimeBrokenLine;
        invalidate();//能重新调用onDraw再绘制Canvas
    }

    public void setClear(){
        state=STATE.NOTHING;
        myList.clear();
        invalidate();

    }
}
