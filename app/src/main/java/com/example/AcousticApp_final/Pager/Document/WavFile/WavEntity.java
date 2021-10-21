package com.example.AcousticApp_final.Pager.Document.WavFile;

import java.io.File;

/**
 * @author: wzt
 * @date: 2020/11/18
 */
public class WavEntity {
    public String wavID;
    public float size,time;
    public int h,m,s;
    public File wavFile;
    public WavEntity(String wavID,float size,int h,int m,int s,float time,File wavFile){
        this.wavID=wavID;
        this.size=size;
        this.h=h;
        this.m=m;
        this.s=s;
        this.time=time;
        this.wavFile=wavFile;
    }

}
