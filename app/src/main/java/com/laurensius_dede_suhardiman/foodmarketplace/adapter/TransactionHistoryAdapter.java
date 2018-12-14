package com.laurensius_dede_suhardiman.foodmarketplace.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.laurensius_dede_suhardiman.foodmarketplace.R;
import com.laurensius_dede_suhardiman.foodmarketplace.model.TransactionHistory;

import java.util.List;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.HolderTransactionHistory> {
    List<TransactionHistory> listTransactionHistory;
    Context ctx;

    public TransactionHistoryAdapter(List<TransactionHistory> listTransactionHistory, Context ctx){
        this.listTransactionHistory = listTransactionHistory;
        this.ctx = ctx;
    }

    @Override
    public HolderTransactionHistory onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_transaction_history,viewGroup,false);
        HolderTransactionHistory holderTransactionHistory = new HolderTransactionHistory(v);
        return holderTransactionHistory;
    }

    @Override
    public void onBindViewHolder(HolderTransactionHistory holderTransactionHistory,final int i){
        holderTransactionHistory.tvId.setText(listTransactionHistory.get(i).getId());
        holderTransactionHistory.tvDatetime.setText(listTransactionHistory.get(i).getDatetime());
        holderTransactionHistory.tvNominal.setText(listTransactionHistory.get(i).getTotal());
    }

    @Override
    public int getItemCount(){
        return listTransactionHistory.size();
    }

    public TransactionHistory getItem(int position){
        return listTransactionHistory.get(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class HolderTransactionHistory extends  RecyclerView.ViewHolder{
        CardView cvTransactionHistory;
        TextView tvId,tvDatetime,tvStatus,tvNominal;

        HolderTransactionHistory(View itemView){
            super(itemView);
            cvTransactionHistory = (CardView) itemView.findViewById(R.id.cv_item_history);
            tvId= (TextView)itemView.findViewById(R.id.tv_id);
            tvDatetime= (TextView)itemView.findViewById(R.id.tv_datetime);
            tvNominal = (TextView)itemView.findViewById(R.id.tv_nominal);
        }
    }
}