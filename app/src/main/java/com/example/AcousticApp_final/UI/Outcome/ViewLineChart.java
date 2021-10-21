package com.example.AcousticApp_final.UI.Outcome;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.AcousticApp_final.App.MyApp;
import com.example.AcousticApp_final.Config.ConfigFile;
import com.example.AcousticApp_final.R;
import com.example.AcousticApp_final.Utils.UtilCal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author: wzt
 * @date: 2020/12/2
 */
public class ViewLineChart extends FrameLayout {
    //外部调用
    public Context context;
    //空间长度
    private float Wf, Hf;//框架长宽
    private float Wc,Hc;//chart长宽
    private float WpadL,WpadR, HpadB,HpadT;//边框
    private float SL;//刻度线长度
    //变量
    private int XScaleNum=8;

    //数据
    private float[]dataX;
    private float[]dataY;

    //绘制工具
    private Paint cPaint,gPaint, lPaint;//中间center、网格grid、折线图line
    private Path lPath;
    private TextPaint tPaint;

    //enum STATE{}
    public ViewLineChart(@NonNull Context context) {
        super(context);this.context=context;setWillNotDraw(false);
    }
    public ViewLineChart(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);this.context=context;setWillNotDraw(false);
    }



    private void init(){
        MyApp.viewLineChart=this;
        Wf =getWidth();
        Hf =getHeight();
        Wc= Wf - Wf /7;
        Hc= Hf - Hf /8 ;
        WpadL =(Wf -Wc)*7/8;
        WpadR=(Wf -Wc)*1/8;
        HpadB =HpadT=(Hf -Hc)/2;
        SL=15;
        //框架

        //绘制工具初始化
        cPaint =new Paint(Paint.ANTI_ALIAS_FLAG);//绘制背景
        cPaint.setAntiAlias(true);



        lPaint =new Paint(Paint.ANTI_ALIAS_FLAG);//绘制折线图
        lPaint.setAntiAlias(true);
        lPaint.setColor(getResources().getColor(R.color.black));
        lPaint.setStyle(Paint.Style.STROKE);
        lPaint.setStrokeWidth(2);
        lPath=new Path();

        gPaint=new Paint(Paint.ANTI_ALIAS_FLAG);//绘制网格
        gPaint.setAntiAlias(true);
        gPaint.setColor(getResources().getColor(R.color.styleLevel_003));
        gPaint.setPathEffect(new DashPathEffect(new float[]{20f,20f}, 0));
        gPaint.setStyle(Paint.Style.STROKE);
        gPaint.setStrokeWidth(2);

        //文字
        tPaint=new TextPaint();
        tPaint.setColor(getResources().getColor(R.color.black));
        //tPaint.setStrokeWidth(10);
        tPaint.setTextSize(30);


    }



    enum STATE{NOTHING,DRAW_WITH_XY,SHOW_FORM_IMAGE}
    STATE state= STATE.NOTHING;

    private boolean initFlag=true;//是否初始化
    private boolean drawCoorFlag=true;//是否绘制坐标
    @Override
    protected void onDraw(Canvas canvas) {
        if(initFlag){
            init();
            initFlag=false;
        }
        switch (state){
            case NOTHING:
                if(drawCoorFlag){
                    drawCoorFlag=false;
                    Bitmap mbitmap = Bitmap.createBitmap((int)Wf, (int)Hf, Bitmap.Config.ARGB_8888);
                    Canvas mCanvas = new Canvas(mbitmap);
                    drawCoor(mCanvas);
                    setBackground(new BitmapDrawable(mbitmap));
                }
                break;
            case DRAW_WITH_XY:
                drawLine_XY(canvas);
                break;
            case SHOW_FORM_IMAGE:
                break;
        }
        super.onDraw(canvas);

    }






    private void drawCoor(Canvas canvas){

        cPaint.setStyle(Paint.Style.FILL);
        //绘制框架背景颜色
        cPaint.setColor(getResources().getColor(R.color.styleLevel_004));
        canvas.drawRect(0,0,Wf,Hf,cPaint);
        //绘制中间空白区域
        cPaint.setColor(getResources().getColor(R.color.white));
        canvas.drawRect(WpadL, HpadT, Wf-WpadR, Hf - HpadB, cPaint);
        //绘制框架黑线
        cPaint.setColor(getResources().getColor(R.color.black));
        cPaint.setStyle(Paint.Style.STROKE);
        cPaint.setStrokeWidth(3);
        canvas.drawLine(WpadL, HpadT,WpadL, Hf-HpadB,cPaint);
        canvas.drawLine(WpadL, Hf-HpadB,Wf-WpadR, Hf-HpadB,cPaint);
    }
    private void drawLine_XY(Canvas canvas){
        //计算Y轴刻度
        float Ymax=UtilCal.getMaxInFloat(dataY);//数据最大值
        float Ymin=UtilCal.getMinInFloat(dataY);//数据9值
        float Yscale=UtilCal.getScaleLength(Ymax, Ymin, 9, 4);//Y轴刻度
        float YmaxShow=(float) Math.floor(Ymax/Yscale)*Yscale+Yscale;//显示范围最大值
        float YminShow=(float) Math.floor(Ymin/Yscale)*Yscale;//显示范围最小值
        if(Math.abs(YminShow-Ymin)<Yscale*0.2)//靠太近就增加距离
            YminShow-=Yscale;
        if(Math.abs(YmaxShow-Ymax)<Yscale*0.2)
            YmaxShow+=Yscale;
        int YscaleNum=(int)((YmaxShow-YminShow)/Yscale+1);//刻度个数
        float Yzoom=Hc/(YmaxShow-YminShow);
        //计算X轴刻度
        float Xmax=dataX[dataX.length-1];//数据最大值
        float Xmin=dataX[0];//数据最小值
        float Xscale=UtilCal.getScaleLength(Xmax, Xmin, 9, 4);//Y轴刻度
        float XmaxShow=(float) Math.floor(Xmax/Xscale)*Xscale+Xscale;//显示范围最大值
        float XminShow=(float) Math.floor(Xmin/Xscale)*Xscale;//显示范围最小值
        if(Math.abs(XminShow-Xmin)<Xscale*0.2)//靠太近就增加距离
            XminShow-=Xscale;
        if(Math.abs(XmaxShow-Xmax)<Xscale*0.2)
            XmaxShow+=Xscale;
        int XscaleNum=(int)((XmaxShow-XminShow)/Xscale+1);//刻度个数
        float Xzoom=Wc/(XmaxShow-XminShow);

        float Xstart= WpadL;//起始位置
        float Ystart= HpadB;

        cPaint.setColor(getResources().getColor(R.color.black));
        for(int i=0;i<XscaleNum;i++) {
            //刻度
            canvas.drawText(UtilCal.F2SFN (XminShow + i * Xscale),  Xstart+i * Xscale * Xzoom-9*Float.toString(Xscale).length(), Hf - (Ystart - SL-30), tPaint);
            //刻度线
            canvas.drawLine(Xstart + i * Xscale * Xzoom,Hf - (Ystart),Xstart + i * Xscale * Xzoom,Hf - (Ystart - SL),cPaint);
            //网格线
            if(i==0||i==XscaleNum-1)
                continue;
            canvas.drawLine(Xstart + i * Xscale * Xzoom,Hf - (Ystart),Xstart + i * Xscale * Xzoom,HpadT,gPaint);
        }
        for(int i=0;i<YscaleNum;i++) {
            //刻度
            canvas.drawText(UtilCal.F2SFN( YminShow + i * Yscale), 0, Hf - (Ystart + i * Yscale * Yzoom), tPaint);
            //刻度线
            canvas.drawLine(Xstart - SL, Hf - (Ystart + i * Yscale * Yzoom), Xstart , Hf - (Ystart + i * Yscale * Yzoom), cPaint);
            //网格线
            if(i==0||i==YscaleNum-1)
                continue;
            canvas.drawLine(Wf-WpadR, Hf - (Ystart + i * Yscale * Yzoom), Xstart , Hf - (Ystart + i * Yscale * Yzoom), gPaint);
        }

        lPath.reset();
        lPath.moveTo(Xstart+(dataX[0]-XminShow)*Xzoom,Hf-(Ystart+(dataY[0]-YminShow)*Yzoom));
        for(int i=0;i<dataY.length;i++){
            lPath.lineTo(Xstart+(dataX[i]-XminShow)*Xzoom,Hf-(Ystart+(dataY[i]-YminShow)*Yzoom));
        }
        canvas.drawPath(lPath,lPaint);



    }
    public void StoreImageWithXY(float []X,float []Y,String imageName){
        //state=STATE.DRAW_WITH_XY;
        this.dataX=X;
        this.dataY=Y;

        Bitmap mBitmap = Bitmap.createBitmap((int)Wf, (int)Hf, Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(mBitmap);
        drawCoor(mCanvas);
        drawLine_XY(mCanvas);

        File imageFile = new File(ConfigFile.getCachePath()+"/"+imageName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 50, fos);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void showImage(String imageName) {
        Bitmap mbitmap = null;
        String imagePath=ConfigFile.getCachePath()+"/"+imageName;
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(imagePath));
            mbitmap = BitmapFactory.decodeStream(bis);
            bis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setBackground(new BitmapDrawable(mbitmap));
    }


}
