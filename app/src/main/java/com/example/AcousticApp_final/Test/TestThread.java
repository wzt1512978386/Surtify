package com.example.AcousticApp_final.Test;

import android.util.Log;

import com.example.AcousticApp_final.Config.ConfigFile;
import com.example.AcousticApp_final.Config.ConfigModel;
import com.example.AcousticApp_final.Proccess.ProccessManager;
import com.example.AcousticApp_final.Utils.UtilSys;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author: wzt
 * @date: 2021/1/23
 */
public class TestThread {
    private static String testPath;
    private static String resultPath;
    private static int num=0;
    private static long time1;
    private static long time2;
    private static long timePsum;

    private static void sortFile(File []files){
        List fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isFile())
                    return -1;
                if (o1.isFile() && o2.isDirectory())
                    return 1;
                return o1.getName().compareTo(o2.getName());
            }
        });
    }
    private static void matTest(File matFile,File txtFolder){
        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
        */
                //将该材料没测试完的结果删除
                File []RText=txtFolder.listFiles();
                for(File tf:RText){
                    if(tf.length()==0)
                        tf.delete();
                }
                //lastTxt表示最新得到的测试结果文本
                RText=txtFolder.listFiles();
                sortFile(RText);
                String lastTxt="000";
                if(RText.length>0)
                    lastTxt=RText[RText.length-1].getName().split("\\.")[0];

                //获取排序好的音频文件
                File wavFolder=matFile;
                File []wavFile=wavFolder.listFiles();
                sortFile(wavFile);


                assert wavFile != null;
                FileWriter fw=null; //文本写入流

                String preCat="";   //上一个音频对应的材料

                int sum=0,correct=0;
                for(File wf:wavFile){
                    //String wfmat=wf.getName().split("-")[1];//当前对应材料
                    String wfid=wf.getName().split("-")[2];//当前组合
                    //跳转到最新测试位置进行测试
                    if(wfid.compareTo(lastTxt)<=0)
                        continue;
                    //判断是否测试完一个组合
                    if(!wfid.equals(preCat)){
                        if(fw!=null) {
                            try {
                                fw.write("sum:     "+sum+"\n");
                                fw.write("correct: "+correct+"\n");
                                fw.write("rate:    "+(correct*1.0f/sum)+"\n");
                                sum=0;
                                correct=0;
                                fw.close();
                                //MyApp.pageSetting.terminal.setText("~Acoustic:");
                                UtilSys.trash();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            fw=null;
                        }
                    }
                    //新建测试结果文件来储存测试结果
                    if(fw==null){
                        preCat=wfid;
                        File txtFile=new File(txtFolder.getAbsolutePath()+"/"+wfid+".txt");
                        try {
                            fw=new FileWriter(txtFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    //进行测试得出结果
                    int ans= ProccessManager.testForResult2(wf);
                    if(ans==-1)
                        ans= ProccessManager.testForResult1(wf);


                    //统计时间
                    if(num==0){
                        time1=System.currentTimeMillis();
                        timePsum=0;
                    }
                    num++;
                    timePsum+=(ProccessManager.timeP2-ProccessManager.timeP1);
                    if(num%50==0) {
                        time2=System.currentTimeMillis();
                        Log.i("timetest",  num+"  time2:"+time2+"   timePsum:"+timePsum);
                    }
                    if(num==1000){
                        Log.i("timetest","num:"+num+"  总time:"+((float)(time2-time1)/num)+"    -----------------------");
                        Log.i("timetest","num:"+num+"  模型time:"+((float)(timePsum)/num)+"    -----------------------");
                        num=0;
                    }



                    try {
                        String ansStr="";
                        if(ans==-1) {
                            ansStr="- " + wf.getName();
                        }
                        else {
                            sum++;
                            String mat=wf.getName().split("-")[1];//当前材料
                            //判断材料是否识别正确
                            int C=0;
                            if(mat.equals(ConfigModel.material[ans])) {
                                C = 1;
                                correct++;
                            }

                            //ansStr=C+" "+wf.getName()+" "+ConfigModel.material[ans];
                            //额外加多计算条数
                            ansStr=C+" "+wf.getName()+" "+ConfigModel.material[ans]+"   "+num;
                        }
                        fw.write(ansStr+"\n");
                        Log.i("IN110",ansStr);
                        UtilSys.LOG_I(ansStr);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if(fw!=null)
                        fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            //}
        //}).start();
    }
    public static void startTest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                testPath= ConfigFile.getTestPath();
                resultPath=ConfigFile.getResultPath();

                File userFolder=new File(testPath);
                if(!userFolder.exists())
                    return;
                File[] userFile =userFolder.listFiles();
                assert userFile != null;
                for(File userF:userFile){
                    //if(!userF.getName().equals("曹婷"))
                    //    continue;

                    File []matFolder=userF.listFiles();

                    assert matFolder != null;
                    for(File matF:matFolder){
                        //if(!matF.getName().equals("ct-3240"))
                           // continue;
                        //if(!matF.getName().split("-")[1].equals("3240"))
                         //   continue;

                        //String txtPath=(resultPath+"/"+userF.getName()+"/"+matF.getName())+".txt";
                        String txtPath=(resultPath+"/"+userF.getName()+"/"+matF.getName());
                        File txtFile=new File(txtPath);
                        if(!txtFile.exists()){
                            txtFile.mkdirs();
                        }
                        matTest(matF,txtFile);
                    }
                }
            }
        }).start();


    }




}
