package com.example.AcousticApp_final.Proccess;

import android.content.Context;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

/**
 * @author: wzt
 * @date: 2020/12/1
 */
public class PythonManager {
    private Python py;
    private PyObject obj;
    public PythonManager(Context context){
        initPython(context);
        py=Python.getInstance();
    }
    private void initPython(Context context){
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(context));
        }
    }
    private void doPython(String module,String key, Object... args){
        obj=py.getModule(module).callAttr(key,args);
    }
    public float [][][]getFloat3(String module,String key, Object... args){
        doPython(module,key, args);
        float [][][]result=obj.toJava(float[][][].class);
        return result;
    }
    public float []getFloat1(String module,String key, Object... args){
        doPython(module,key, args);
        return obj.toJava(float[].class);
    }

}
