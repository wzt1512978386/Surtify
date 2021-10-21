package com.example.AcousticApp_final.Pager.Furniture;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AcousticApp_final.R;

import java.util.List;

/**
 * @author: wzt
 * @date: 2021/3/18
 */
public class FurnitureAdapter extends RecyclerView.Adapter<FurnitureAdapter.FurnitureHolder> {
    //外部调用
    public int settingIndex;
    private Context context;
    private List<FurnitureEntity> furnitureList;
    public FurnitureAdapter(Context context, List<FurnitureEntity> list){
        this.context=context;
        this.furnitureList=list;
    }
    @NonNull
    @Override
    public FurnitureAdapter.FurnitureHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView= LayoutInflater.from(context).inflate(R.layout.item_furniture,parent,false);
        return new FurnitureAdapter.FurnitureHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull FurnitureAdapter.FurnitureHolder holder, int position) {
        FurnitureEntity furnitureEntity=furnitureList.get(position);
        holder.furName.setText(furnitureEntity.name);
        holder.furImage.setImageResource(furnitureEntity.imageId);
        holder.itemView.setTag(R.id.tag_pos,position);
    }

    @Override
    public int getItemCount() {
        return furnitureList==null?0:furnitureList.size();
    }

    class FurnitureHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View itemView;
        public ImageButton furImage;
        public TextView furName;
        public ImageView iteminfo;
        //设置按钮

        //public PopupWindow folderWindow;
        //public Button folderDelete;

        public FurnitureHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView=itemView;

            //控件获取
            furImage=(ImageButton)itemView.findViewById(R.id.imagebutton_furniture_item);
            iteminfo=(ImageView)itemView.findViewById(R.id.imageview_furniture_item_connectinfo) ;
            furName=(TextView)itemView.findViewById(R.id.textview_furniture_item);

            furImage.setOnClickListener(this);
            //folderWindow=new P
        }

        @Override
        public void onClick(View v) {
            if(furnitureListener==null||itemView==null||itemView.getTag(R.id.tag_pos)==null)
                return;
            furnitureListener.onClick((Integer)itemView.getTag(R.id.tag_pos),iteminfo);



        }
    }
    public interface FurnitureListener{
        public void onClick(int pos, ImageView connectinfo);
        //public void onClickSetting(int pos);

    }
    private FurnitureListener furnitureListener=null;
    public void setFurnitureListener(FurnitureListener furnitureListener){
        this.furnitureListener=furnitureListener;
    }
}

