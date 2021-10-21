package com.example.AcousticApp_final.Pager.Document.WavFile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AcousticApp_final.R;

import java.util.List;

/**
 * @author: lenovo
 * @date: 2020/11/14
 */
public class WavAdapter extends RecyclerView.Adapter<WavAdapter.WavHolder> {
    //外部调用
    public int settingIndex;
    private Context context;
    private List<WavEntity> wavList;
    public WavAdapter(Context context, List<WavEntity> list){
        this.context=context;
        this.wavList=list;
    }
    @NonNull
    @Override
    public WavHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView= LayoutInflater.from(context).inflate(R.layout.item_document_wav,parent,false);
        return new WavHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull WavHolder holder, int position) {
        WavEntity wavEntity=wavList.get(position);
        holder.wavID.setText(wavEntity.wavID);
        holder.wavInfo.setText(
                String.format(context.getString(R.string.wav_format),
                        wavEntity.size,wavEntity.h,wavEntity.m,wavEntity.s,wavEntity.time));
        holder.itemView.setTag(R.id.tag_pos,position);
    }

    @Override
    public int getItemCount() {
        return wavList==null?0:wavList.size();
    }

    class WavHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView wavID;
        public TextView wavInfo;
        //设置按钮
        public ImageButton wavSetting;

        public WavHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            //控件获取
            wavID=(TextView)itemView.findViewById(R.id.textview_document_item_wavID);
            wavInfo=(TextView)itemView.findViewById(R.id.textview_document_item_wavinfo);
            wavSetting=(ImageButton)itemView.findViewById(R.id.imagebutton_document_item_wav_setting);
            wavSetting.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(wavListener==null||itemView==null||itemView.getTag(R.id.tag_pos)==null)
                return;
            switch (v.getId()){
                //直接整个item点击
                case R.id.linearlayout_document_item_wav:
                    wavListener.onClick((Integer)itemView.getTag(R.id.tag_pos));
                    break;
                //点击setting
                case R.id.imagebutton_document_item_wav_setting:
                    //folderListener.onClickSetting((Integer)v.getTag(R.id.tag_pos));
                    settingIndex =(Integer)itemView.getTag(R.id.tag_pos);
                    wavListener.onClickSetting(v);
                    break;
            }
        }
    }
    public interface WavListener{
        public void onClick(int pos);
        public void onClickSetting(View v);

    }
    private WavListener wavListener;
    public void setWavListener(WavListener wavListener){
        this.wavListener=wavListener;
    }
}
