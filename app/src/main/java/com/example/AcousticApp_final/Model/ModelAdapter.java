package com.example.AcousticApp_final.Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AcousticApp_final.R;

import java.util.List;

/**
 * @author: wzt
 * @date: 2020/12/20
 */
public class ModelAdapter extends RecyclerView.Adapter<ModelAdapter.ModelHolder> {
    //
    private Context context;
    private List<String> modelList;

    public ModelAdapter(Context context, List<String> modelList){
        this.context=context;
        this.modelList=modelList;
    }
    @NonNull
    @Override
    public ModelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView= LayoutInflater.from(context).inflate(R.layout.item_setting_model,parent,false);
        return new ModelHolder(convertView);
    }
    @Override
    public void onBindViewHolder(@NonNull ModelHolder holder, int position) {
        String modelName=modelList.get(position);
        holder.modelName.setText(modelName);
        holder.itemView.setTag(R.id.tag_pos,position);
    }
    @Override
    public int getItemCount() {
        return modelList==null?0:modelList.size();
    }
    class ModelHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //外部调用
        public View itemView;
        public TextView modelName;
        public ModelHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView=itemView;
            modelName=(TextView)itemView.findViewById(R.id.textview_setting_item_model);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if(modelListener==null||itemView==null||itemView.getTag(R.id.tag_pos)==null)
                return;
            modelListener.onClick((int)itemView.getTag(R.id.tag_pos));
        }
    }
    public interface ModelListener{
        public void onClick(int pos);
    }
    private ModelListener modelListener=null;
    public void setModelListener(ModelListener modelListener){
        this.modelListener=modelListener;
    }
}
