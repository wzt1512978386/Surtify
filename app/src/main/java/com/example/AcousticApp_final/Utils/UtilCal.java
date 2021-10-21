package com.example.AcousticApp_final.Utils;

/**
 * @author: wzt
 * @date: 2020/12/2
 */
public class UtilCal {
    public static float getMaxInFloat(float []data){
        float result=data[0];
        for(int i=0;i<data.length;i++) {
            if (result < data[i]) {
                result = data[i];
            }
        }
        return result;
    }
    public static float getMinInFloat(float []data){
        float result=data[0];
        for(int i=0;i<data.length;i++) {
            if (result > data[i]) {
                result = data[i];
            }
        }
        return result;
    }
    public static int getDecDigits(float A){
        int digits=20;
        while(true){
            if(Math.pow(10,digits)>A)
                digits--;
            else
                break;
        }
        return digits;
    }
    //根据两个数，求他们的最佳刻度，一般取20、10、5、2这样
    public static float getScaleLength(float Amax,float Amin,int Nmax,int Nmin){
        float Adif=Amax-Amin;
        int AdifDig=getDecDigits(Adif);
        double scaleLen=Math.pow(10, AdifDig);
        //int []zoom={2,5,10,20,50,100,200,500,1000};
        //int idx=0;
        /*
        do {
            if (Adif / scaleLen > Nmax)
                scaleLen *= zoom[idx++];
            else if (Adif / scaleLen < Nmin)
                scaleLen /= zoom[idx++];
        } while (!(Adif / scaleLen > Nmin) || !(Adif / scaleLen < Nmax));*/
        if (Adif/scaleLen>=14)
            scaleLen*=5;
        else if(Adif/scaleLen>=7)
            scaleLen*=2;
        else if(Adif/scaleLen>=4)
            scaleLen*=1;
        else if(Adif/scaleLen>=2)
            scaleLen/=2;
        else
            scaleLen/=5;
        return (float) scaleLen;
    }
    //数组抽样
    public static float[] ArraySampling(float[]data,int num){

        if(num>data.length)
            return data;

        float[] result = new float[num];
        int per=result.length/num;
        for(int i=0;i<num;i++)
            result[i]=data[per*i];
        return result;
    }
    //科学计数法显示   Float2ScientificNotation
    public static String F2SFN(float num){
        String str = String.format("%E", num);//获取直接格式化结果
        str = str.replace("E-0", "E-");//将E-0N处理为E-N
        //处理结果
        String temp = str.substring(0,str.indexOf(".")+1);//前3位，因为最多只有一个小数
        //精确到小数点后两位
        String f = String.format("%.1f", Float.parseFloat(temp));
        str = f+str.substring(str.indexOf("E"),str.indexOf("E")+3);
        return str;
    }


}
