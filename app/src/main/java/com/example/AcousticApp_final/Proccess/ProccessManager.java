package com.example.AcousticApp_final.Proccess;

import android.util.Log;

import com.chaquo.python.Kwarg;
import com.example.AcousticApp_final.App.MyApp;
import com.example.AcousticApp_final.Config.ConfigModel;
import com.example.AcousticApp_final.Utils.UtilSys;

import java.io.File;

/**
 * @author: wzt
 * @date: 2020/12/1
 */
public class ProccessManager {
    public static int type=1;
    public static float th1=0.7f;
    public static float th2=0.8f;
    public static long timeP1,timeP2;
    //第一种数据处理方法
    public static int testForResult1(File fileTest){
        PythonManager PM=MyApp.pythonManager;
        if(MyApp.testFlag==0) {
            UtilSys.sendInfo("开始数据处理（1）");
            UtilSys.LOG_I("开始识别~");
        }
        float [][][]result=PM.getFloat3("DataPreprocessing","seg_data",new Kwarg("wavfile",fileTest.getPath()));
        if(result.length==1&&result[0].length==1&&result[0][0].length==1&&result[0][0][0]==0) {
            if(MyApp.testFlag==0)   UtilSys.sendInfo("至少需要敲打三次哦>_<");
            return -1;
        }
        if(result.length>3){
            if(MyApp.testFlag==0)   UtilSys.sendInfo("哎呀*_*,敲打了"+result.length+"次，帮你取前三次哦~");
        }
        float []result2=new float[3*result[0].length*result[0][0].length];
        int index=0;

        for(int i=0;i<3;i++){
            for(int j=0;j<result[0].length;j++){
                for(int k=0;k<result[0][0].length;k++){
                    result2[index]=result[i][j][k];
                    index++;
                }
            }
        }
        if(MyApp.testFlag==0) {
            UtilSys.sendInfo("数据处理完毕！");
            UtilSys.LOG_I("数据处理完毕！");
        }
        timeP1=System.currentTimeMillis();
        float []dd=MyApp.classifier.getPredit(result2);

        if(MyApp.testFlag==0) {
            UtilSys.sendInfo("模型结果出来了！");
            UtilSys.LOG_I("模型结果出来了！");
        }

        timeP2=System.currentTimeMillis();
        float max=dd[0];
        int maxId=0;
        for(int i=0;i<dd.length;i++){
            if(max<dd[i]){
                max=dd[i];
                maxId=i;
            }
            //UtilSys.LOG_I("材料"+i+" : "+pro[i]);
            if(MyApp.testFlag==0)   UtilSys.LOG_I("材料["+ConfigModel.material[i]+"] : "+dd[i]);
        }
        if(MyApp.testFlag==0) {
            int finalMaxId = maxId;
            MyApp.handler.post(new Runnable() {
                @Override
                public void run() {
                    MyApp.pageOutcome.result.setText(ConfigModel.material[finalMaxId]);
                }
            });


            UtilSys.sendInfo("开始绘制结果~");
            UtilSys.LOG_I("开始绘制结果~");


            for (int i = 0; i < 3; i++) {
                float[] pX = PM.getFloat1("DataPreprocessing", "read_data", new Kwarg("i", i * 2));
                float[] pY = PM.getFloat1("DataPreprocessing", "read_data", new Kwarg("i", i * 2 + 1));
                //pX= UtilCal.ArraySampling(pX,10000);
                //pY=UtilCal.ArraySampling(pY,10000);
                MyApp.viewLineChart.StoreImageWithXY(pX, pY, i + ".png");
            }


            UtilSys.sendInfo("结果已保存至cache");
            UtilSys.LOG_I("结果已保存至cache");
            MyApp.viewLineChart.showImage("0.png");
            //Python py=Python.getInstance();
//根据i的值寻找相应的x和y，如第一个图的x为i=0，y为i=1
            //PyObject obj2=py.getModule("DataPreprocessing").callAttr("read_data",new Kwarg("i",0));
            //float []p1X=obj2.toJava(float[].class);
            Log.i("IN101", "获得数据");
        }
        return maxId;
    }

    //第二种数据处理方法
    public static int testForResult2(File fileTest) {
        PythonManager PM = MyApp.pythonManager;

        if(MyApp.testFlag==0) {
            UtilSys.sendInfo("开始数据处理（2）");
            UtilSys.LOG_I("开始识别~");
        }

        float[][][] result = PM.getFloat3("DataPreprocessing2", "seg_data", new Kwarg("wavfile", fileTest.getPath()), new Kwarg("th1", th1), new Kwarg("th2", th2));
        if (result.length == 1 && result[0].length == 1 && result[0][0].length == 1 && result[0][0][0] == 0) {
            if(MyApp.testFlag==0)   UtilSys.sendInfo("至少需要敲打三次哦>_<");
            return -1;
        } else if (result.length == 1 && result[0].length == 1 && result[0][0].length == 1 && result[0][0][0] == 1) {
            if(MyApp.testFlag==0)   UtilSys.sendInfo("该音频传入谱熵法进行计算发生了错误？？");
            return -1;
        }
        if (result.length > 3) {
            if(MyApp.testFlag==0)   UtilSys.sendInfo("哎呀*_*,敲打了" + result.length + "次，帮你取前三次哦~");
        }

        float[] result2 = new float[3 * result[0].length * result[0][0].length];
        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < result[0].length; j++) {
                for (int k = 0; k < result[0][0].length; k++) {
                    result2[index] = result[i][j][k];
                    index++;
                }
            }
        }
        timeP1 = System.currentTimeMillis();

        if(MyApp.testFlag==0) {
            UtilSys.sendInfo("数据处理完毕！");
            UtilSys.LOG_I("数据处理完毕！");
        }

        float[] dd = MyApp.classifier.getPredit(result2);

        if(MyApp.testFlag==0) {
            UtilSys.sendInfo("模型结果出来了！");
            UtilSys.LOG_I("模型结果出来了！");
        }

        timeP2 = System.currentTimeMillis();
        //输出概率
        float max = dd[0];
        int maxId = 0;
        for (int i = 0; i < dd.length; i++) {
            if (max < dd[i]) {
                max = dd[i];
                maxId = i;
            }
            if(MyApp.testFlag==0)   UtilSys.LOG_I("材料[" + ConfigModel.material[i] + "] : " + dd[i]);
        }

        if(MyApp.testFlag==0) {
            int finalMaxId = maxId;
            MyApp.handler.post(new Runnable() {
                @Override
                public void run() {
                    MyApp.pageOutcome.result.setText(ConfigModel.material[finalMaxId]);
                }
            });


            UtilSys.sendInfo("开始绘制结果~");
            UtilSys.LOG_I("开始绘制结果~");


            for (int i = 0; i < 3; i++) {
                float[] pX = PM.getFloat1("DataPreprocessing2", "read_data", new Kwarg("i", i * 2));
                float[] pY = PM.getFloat1("DataPreprocessing2", "read_data", new Kwarg("i", i * 2 + 1));
                //pX= UtilCal.ArraySampling(pX,10000);
                //pY=UtilCal.ArraySampling(pY,10000);
                MyApp.viewLineChart.StoreImageWithXY(pX, pY, i + ".png");
            }


            UtilSys.sendInfo("结果已保存至cache");
            UtilSys.LOG_I("结果已保存至cache");
            MyApp.viewLineChart.showImage("0.png");
            //Python py=Python.getInstance();
//根据i的值寻找相应的x和y，如第一个图的x为i=0，y为i=1
            //PyObject obj2=py.getModule("DataPreprocessing").callAttr("read_data",new Kwarg("i",0));
            //float []p1X=obj2.toJava(float[].class);
            Log.i("IN101", "获得数据");
        }
        return maxId;
    }

}