package com.example.AcousticApp_final.Utils;

public class MyList {
    private short []list;
    private int maxLen;
    private int start;
    private int end;
    private int len;
    public MyList(int maxLen){
        this.maxLen=maxLen;
        list=new short[maxLen];
        start=0;
        end=0;
        len=0;
    }
    //获得允许最长长度
    public int getMaxLen(){return maxLen;}
    //当前长度
    public int getLen(){return len;}
    //当前start位置
    public int getEnd(){return end;}
    //弹出头元素
    public boolean pop(){
        if(len>=1) {
            len--;
            list[start]=0;
            start = (start + 1) % maxLen;
            return true;
        }
        else
            return false;
    }
    //向后推入元素
    public boolean push(short x){
        if(len<maxLen) {
            list[(start + len) % maxLen] = x;
            len++;
            end=(end+1)%maxLen;
            return true;
        }
        else
            return false;
    }
    public boolean forcePush(short x){
        if(len<maxLen){
            list[(start+len)%maxLen]=x;
            len++;
            end=(end+1)%maxLen;
            return true;
        }
        else {
            list[start] = x;
            start = (start + 1) % maxLen;
            end = (end + 1) % maxLen;
            return false;
        }
    }
    //返回索引元素
    public short get(int i){
        if(i>=0&&i<maxLen){
            return list[(start+i)%maxLen];
        }
        else
            return -1;
    }

    //返回绝对位置
    public short getAbsolute(int i){
        if(i>=0&&i<maxLen)
            return list[i];
        return -1;
    }
    public boolean setAbsolute(int i,short x){
        if(i>=0&&i<maxLen) {
            list[i]=x;
            return true;
        }
        return false;
    }
    public void clear(){
        for(int i=0;i<list.length;i++)
            list[i]=0;
        start=end=len=0;


    }
}


