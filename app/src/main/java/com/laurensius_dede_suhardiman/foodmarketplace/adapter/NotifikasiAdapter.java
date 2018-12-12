package com.laurensius_dede_suhardiman.foodmarketplace.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.laurensius_dede_suhardiman.foodmarketplace.R;
import com.laurensius_dede_suhardiman.foodmarketplace.model.Notifikasi;

import java.util.List;

public class NotifikasiAdapter extends RecyclerView.Adapter<NotifikasiAdapter.HolderNotifikasi> {
    List<Notifikasi> listNotifikasi;
    Context ctx;

    public NotifikasiAdapter(List<Notifikasi> listNotifikasi, Context ctx){
        this.listNotifikasi = listNotifikasi;
        this.ctx = ctx;
    }

    @Override
    public HolderNotifikasi onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notifikasi,viewGroup,false);
        HolderNotifikasi holderNotifikasi = new HolderNotifikasi(v);
        return holderNotifikasi;
    }

    @Override
    public void onBindViewHolder(HolderNotifikasi holderNotifikasi,final int i){
        holderNotifikasi.tvTitle.setText(listNotifikasi.get(i).getTitle());
        holderNotifikasi.tvMessage.setText(listNotifikasi.get(i).getMessage());
    }

    @Override
    public int getItemCount(){
        return listNotifikasi.size();
    }

    public Notifikasi getItem(int position){
        return listNotifikasi.get(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class HolderNotifikasi extends  RecyclerView.ViewHolder{
        CardView cvNotifikasi;
        TextView tvTitle,tvMessage;

        HolderNotifikasi(View itemView){
            super(itemView);
            cvNotifikasi = (CardView) itemView.findViewById(R.id.cv_item_notifikasi);
            tvTitle= (TextView)itemView.findViewById(R.id.tv_title);
            tvMessage= (TextView)itemView.findViewById(R.id.tv_message);
        }
    }
}