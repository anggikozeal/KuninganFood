package com.laurensius_dede_suhardiman.foodmarketplace.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.laurensius_dede_suhardiman.foodmarketplace.R;
import com.laurensius_dede_suhardiman.foodmarketplace.model.Drawdown;

import java.util.List;
public class DrawdownAdapter extends RecyclerView.Adapter<DrawdownAdapter.HolderDrawdown> {
    List<Drawdown> listDrawdown;
    Context ctx;

    public DrawdownAdapter(List<Drawdown> listDrawdown, Context ctx){
        this.listDrawdown = listDrawdown;
        this.ctx = ctx;
    }

    @Override
    public HolderDrawdown onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_drawdown,viewGroup,false);
        HolderDrawdown holderDrawdown = new HolderDrawdown(v);
        return holderDrawdown;
    }

    @Override
    public void onBindViewHolder(HolderDrawdown holderDrawdown,final int i){
        holderDrawdown.tvId.setText(listDrawdown.get(i).getId());
        if(listDrawdown.get(i).getStatus().equals("ON_REQUEST")){
            holderDrawdown.tvDatetime.setText(listDrawdown.get(i).getDatetimeRequest());
        }else{
            holderDrawdown.tvDatetime.setText(listDrawdown.get(i).getDatetimeApprove());
        }
        holderDrawdown.tvStatus.setText(listDrawdown.get(i).getStatus());
        holderDrawdown.tvNominal.setText(listDrawdown.get(i).getTotal());
    }

    @Override
    public int getItemCount(){
        return listDrawdown.size();
    }

    public Drawdown getItem(int position){
        return listDrawdown.get(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class HolderDrawdown extends  RecyclerView.ViewHolder{
        CardView cvDrawdown;
        TextView tvId,tvDatetime,tvStatus,tvNominal;

        HolderDrawdown(View itemView){
            super(itemView);
            cvDrawdown = (CardView) itemView.findViewById(R.id.cv_item_drawdown);
            tvId= (TextView)itemView.findViewById(R.id.tv_id);
            tvDatetime= (TextView)itemView.findViewById(R.id.tv_datetime);
            tvStatus = (TextView)itemView.findViewById(R.id.tv_status);
            tvNominal = (TextView)itemView.findViewById(R.id.tv_nominal);
        }
    }
}