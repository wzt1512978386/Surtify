package com.example.AcousticApp_final.Pager.Outcome;

import android.view.View;

import com.example.AcousticApp_final.App.MyApp;

/**
 * @author: wzt
 * @date: 2020/12/2
 */
public class EventsOutcome {
    private PageOutcome PO;
    public EventsOutcome(PageOutcome pageOutcome){ this.PO=pageOutcome; }
    public void setEvent(){setOnClick();}
    private void setOnClick(){
        //是否转换为final
        PO.bt_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(true)
                    return;
                if(PO.curImageId%2==0){
                    PO.curImageId++;
                    MyApp.viewLineChart.showImage(PO.curImageId+".png");
                    PO.bt_switch.setText("final touch("+(PO.curImageId/2)+") sound");
                }
                else{
                    PO.curImageId--;
                    MyApp.viewLineChart.showImage(PO.curImageId+".png");
                    PO.bt_switch.setText("touch("+(PO.curImageId/2)+") sound");
                }
            }
        });
        //上一个touch
        PO.bt_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idTemp=PO.curImageId-1;
                if(idTemp<0)
                    idTemp=2;
                PO.curImageId=idTemp;
                MyApp.viewLineChart.showImage(idTemp+".png");
                PO.bt_switch.setText("touch("+(idTemp)+") sound");
                /*
                int idTemp=PO.curImageId/2-1;
                if(idTemp<0)
                    idTemp=2;
                PO.curImageId=idTemp*2;
                MyApp.viewLineChart.showImage(idTemp*2+".png");
                PO.bt_switch.setText("touch("+(idTemp)+") sound");
                 */
            }
        });
        //下一个touch
        PO.bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idTemp=PO.curImageId+1;
                if(idTemp>2)
                    idTemp=0;
                PO.curImageId=idTemp;
                MyApp.viewLineChart.showImage(idTemp+".png");
                PO.bt_switch.setText("touch("+(idTemp)+") sound");
                /*
                int idTemp=PO.curImageId/2+1;
                if(idTemp>2)
                    idTemp=0;
                PO.curImageId=idTemp*2;
                MyApp.viewLineChart.showImage(idTemp*2+".png");
                PO.bt_switch.setText("touch("+(idTemp)+") sound");
                 */
            }
        });
    }
}
