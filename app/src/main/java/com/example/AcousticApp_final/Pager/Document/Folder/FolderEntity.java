package com.example.AcousticApp_final.Pager.Document.Folder;

import java.io.File;

/**
 * @author: wzt
 * @date: 2020/11/18
 */
public class FolderEntity {
    public String userID;
    public String remarks;
    public int y,m,d;
    public int fileNum;
    public File folderFile;
    public FolderEntity(String userID,String remarks,int y,int m,int d,int fileNum,File folderFile){
        this.userID=userID;
        this.remarks=remarks;
        this.y=y;
        this.m=m;
        this.d=d;
        this.fileNum=fileNum;
        this.folderFile=folderFile;
    }
}
