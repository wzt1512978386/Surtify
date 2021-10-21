package com.example.AcousticApp_final.Model;

import android.util.Log;

import com.example.AcousticApp_final.App.MyApp;
import com.example.AcousticApp_final.Config.ConfigModel;
import com.example.AcousticApp_final.Utils.UtilFile;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.nio.FloatBuffer;

//分类器
public class Classifier {
    Module model;
    private float []predit;
    private String curModel;
    //读取模型 模型名称
    public Classifier(){
        curModel=ConfigModel.model[0];

        model = Module.load(UtilFile.assetFilePath(MyApp.mainActivity,curModel));
    }
    public void UpdateClassifier(String newModel){
        if(newModel.equals(curModel))
            return;
        curModel=newModel;
        model = Module.load(UtilFile.assetFilePath(MyApp.mainActivity,curModel));
    }
    //预测 包括输入和输出
    private   void predit(float []data)
    {
        //float []data=new float[2091];

        //int ah=17,aw=41;
        // int ah=65,aw=81;
        int ah=33,aw=41;


        int length= 3 * ah * aw;
        Log.i("IN101","ss"+String.valueOf(data.length));
        final FloatBuffer floatBuffer = Tensor.allocateFloatBuffer(length);
        InputToFloatBuffer(data, length, floatBuffer, 0);

        long sharp[]={1,3,ah,aw};

        //long sharp[]={3,17,41};
        Tensor tensor= Tensor.fromBlob(data,sharp);
        IValue inputdata= IValue.from(tensor);
        Tensor output=model.forward(inputdata).toTensor();
        predit=output.getDataAsFloatArray();
    }
    private static void checkOutBufferCapacity(
            FloatBuffer outBuffer, int outBufferOffset, int length) {
        if (outBufferOffset + 1 * 1 * length > outBuffer.capacity()) {
            throw new IllegalStateException("Buffer underflow");
        }
    }
    private static void InputToFloatBuffer(
            final float[] array,
            final int length,
            final FloatBuffer outBuffer,
            final int outBufferOffset) {
        checkOutBufferCapacity(outBuffer, outBufferOffset, length);

        final int Count = 1 * 1 * length;
        for (int i = 0; i < Count; i++) {
            float u=array[i];
            outBuffer.put(outBufferOffset + i, u);
        }
    }
    public float[]getPredit(float []data){
        predit(data);
        return predit;
    }

}
