package com.laurensius_dede_suhardiman.foodmarketplace.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.laurensius_dede_suhardiman.foodmarketplace.R;
import com.laurensius_dede_suhardiman.foodmarketplace.appcontroller.AppController;
import com.laurensius_dede_suhardiman.foodmarketplace.model.Transaction;
import com.laurensius_dede_suhardiman.foodmarketplace.model.TransactionDetail;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

public class KonfirmasiAdapter extends RecyclerView.Adapter<KonfirmasiAdapter.HolderTransaction> {
    List<Transaction> listTransaction;
    Context ctx;

    public KonfirmasiAdapter(List<Transaction> listTransaction, Context ctx){
        this.listTransaction = listTransaction;
        this.ctx = ctx;
    }

    @Override
    public HolderTransaction onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_konfirmasi,viewGroup,false);
        HolderTransaction holderTransaction = new HolderTransaction(v);
        return holderTransaction;
    }

    @Override
    public void onBindViewHolder(HolderTransaction holderTransaction,final int i){
        RecyclerView.LayoutManager mLayoutManager;
        List<TransactionDetail> transactionDetailList;
        TransactionKonfirmasiAdapter transactionKonfirmasiAdapter = null;
        transactionDetailList = listTransaction.get(i).getTransactionDetailList();

        holderTransaction.tvShopName.setText(listTransaction.get(i).getShop().getShopName());
        holderTransaction.tvTotalBayar.setText("0");

        Double[] subTotal = new Double[listTransaction.get(i).getTransactionDetailList().size()];
        Double total = 0.0;
        for(int x=0;x<listTransaction.get(i).getTransactionDetailList().size();x++){
            Double harga = Double.parseDouble(listTransaction.get(i).getTransactionDetailList().get(x).getProduct().getPrice());
            Double diskon = Double.parseDouble(listTransaction.get(i).getTransactionDetailList().get(x).getProduct().getDiscount());
            Double qty = Double.parseDouble(listTransaction.get(i).getTransactionDetailList().get(x).getQty());
            subTotal[x] = (harga - (harga * (diskon/100))) * qty;
            total = total + subTotal[x];
        }
        DecimalFormat df = new DecimalFormat("#########0.00");

        holderTransaction.tvTotalBayar.setText("IDR " + String.valueOf(df.format(total)));

        holderTransaction.rvProductKeranjang.setAdapter(null);
        holderTransaction.rvProductKeranjang.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(ctx);
        holderTransaction.rvProductKeranjang.setLayoutManager(mLayoutManager);
        transactionKonfirmasiAdapter = new TransactionKonfirmasiAdapter(transactionDetailList,ctx);
        transactionKonfirmasiAdapter.notifyDataSetChanged();
        holderTransaction.rvProductKeranjang.setAdapter(transactionKonfirmasiAdapter);
        
    }

    @Override
    public int getItemCount(){
        return listTransaction.size();
    }

    public Transaction getItem(int position){
        return listTransaction.get(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }


    public static class HolderTransaction extends  RecyclerView.ViewHolder{
        CardView cvKeranjang;
        TextView tvShopName,tvTotalBayar,etKonfirmasi;
        RecyclerView rvProductKeranjang;

        HolderTransaction(View itemView){
            super(itemView);
            cvKeranjang = (CardView) itemView.findViewById(R.id.cv_item_keranjang);
            tvShopName = (TextView)itemView.findViewById(R.id.tv_shop_name);
            rvProductKeranjang = (RecyclerView)itemView.findViewById(R.id.rv_product_keranjang);
            tvTotalBayar = (TextView)itemView.findViewById(R.id.tv_total_bayar);
        }
    }
}