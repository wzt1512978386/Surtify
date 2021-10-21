package com.example.AcousticApp_final.Pager.Document.Folder;

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
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderHolder> {
    //外部调用
    public int settingIndex;
    private Context context;
    private List<FolderEntity> folderList;
    public FolderAdapter(Context context, List<FolderEntity> list){
        this.context=context;
        this.folderList=list;
    }
    @NonNull
    @Override
    public FolderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView= LayoutInflater.from(context).inflate(R.layout.item_document_folder,parent,false);
        return new FolderHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderHolder holder, int position) {
        FolderEntity folderEntity=folderList.get(position);
        holder.userID.setText(folderEntity.userID);
        holder.userInfo.setText(
                String.format(context.getString(R.string.folder_format),
                        folderEntity.fileNum,folderEntity.y,folderEntity.m,folderEntity.d,folderEntity.remarks));
        holder.itemView.setTag(R.id.tag_pos,position);
    }

    @Override
    public int getItemCount() {
        return folderList==null?0:folderList.size();
    }

    class FolderHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View itemView;
        public TextView userID;
        public TextView userInfo;
        //设置按钮
        public ImageButton folderSetting;
        //public PopupWindow folderWindow;
        //public Button folderDelete;

        public FolderHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView=itemView;
            itemView.setOnClickListener(this);
            //控件获取
            userID=(TextView)itemView.findViewById(R.id.textview_document_item_folder_userID);
            userInfo=(TextView)itemView.findViewById(R.id.textview_document_item_folder_userinfo);
            folderSetting=(ImageButton)itemView.findViewById(R.id.imagebutton_document_item_folder_setting);
            folderSetting.setOnClickListener(this);
            //folderWindow=new P
        }

        @Override
        public void onClick(View v) {
            if(folderListener==null||itemView==null||itemView.getTag(R.id.tag_pos)==null)
                return;
            switch (v.getId()){
                case R.id.linearlayout_document_item_folder:
                    folderListener.onClick((Integer)itemView.getTag(R.id.tag_pos));
                    break;
                case R.id.imagebutton_document_item_folder_setting:
                    //folderListener.onClickSetting((Integer)v.getTag(R.id.tag_pos));
                    settingIndex =(Integer)itemView.getTag(R.id.tag_pos);
                    folderListener.onClickSetting(v);
                    break;
            }


        }
    }
    public interface FolderListener{
        public void onClick(int pos);
        //public void onClickSetting(int pos);
        public void onClickSetting(View v);
    }
    private FolderListener folderListener=null;
    public void setFolderListener(FolderListener folderListener){
        this.folderListener=folderListener;
    }
}
