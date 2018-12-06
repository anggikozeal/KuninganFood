package com.laurensius_dede_suhardiman.foodmarketplace.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.laurensius_dede_suhardiman.foodmarketplace.RiwayatTransaksiPenjualan;
import com.laurensius_dede_suhardiman.foodmarketplace.appcontroller.AppController;
import com.laurensius_dede_suhardiman.foodmarketplace.model.Transaction;
import com.laurensius_dede_suhardiman.foodmarketplace.model.TransactionDetail;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.HolderTransaction> {
    List<Transaction> listTransaction;
    Context ctx;

    public OrderAdapter(List<Transaction> listTransaction, Context ctx){
        this.listTransaction = listTransaction;
        this.ctx = ctx;
    }

    @Override
    public HolderTransaction onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_order,viewGroup,false);
        HolderTransaction holderTransaction = new HolderTransaction(v);
        return holderTransaction;
    }

    @Override
    public void onBindViewHolder(HolderTransaction holderTransaction,final int i){
        RecyclerView.LayoutManager mLayoutManager;
        List<TransactionDetail> transactionDetailList;
        TransactionOrderAdapter transactionOrderAdapter = null;
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
        transactionOrderAdapter = new TransactionOrderAdapter(transactionDetailList,ctx);
        transactionOrderAdapter.notifyDataSetChanged();
        holderTransaction.rvProductKeranjang.setAdapter(transactionOrderAdapter);
        holderTransaction.btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestTransactionUpdateStatus(listTransaction.get(i).getId(),"ON_PROSES");
            }
        });
        
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


    public void requestTransactionUpdateStatus(String idTransaction,String newStatus){
        Random random = new Random();
        int rnd = random.nextInt(999999 - 99) + 99;
        String transac_update_status = ctx.getResources().getString(R.string.tag_request_transac_update_status);
        String url =ctx. getResources().getString(R.string.api)
                .concat(ctx.getResources().getString(R.string.endpoint_transac_update_status))
                .concat(idTransaction) //idTransaction
                .concat(ctx.getResources().getString(R.string.slash))
                .concat(newStatus) //val
                .concat(ctx.getResources().getString(R.string.slash))
                .concat(String.valueOf(rnd))
                .concat(ctx.getResources().getString(R.string.slash));
        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage(ctx.getResources().getString(R.string.progress_loading));
        pDialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        Log.d(ctx.getResources().getString(R.string.debug),response.toString());
                        Intent intent = new Intent(ctx,RiwayatTransaksiPenjualan.class);
                        intent.putExtra("tab","order");
                        ctx.startActivity(intent);
                        RiwayatTransaksiPenjualan.activity.finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        Toast.makeText(ctx,"Gagal Proses Pesanan. Silakan coba lagi!",Toast.LENGTH_LONG).show();
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjReq, transac_update_status);
    }

    public static class HolderTransaction extends  RecyclerView.ViewHolder{
        CardView cvKeranjang;
        TextView tvShopName,tvTotalBayar;
        RecyclerView rvProductKeranjang;
        Button btnProses;

        HolderTransaction(View itemView){
            super(itemView);
            cvKeranjang = (CardView) itemView.findViewById(R.id.cv_item_keranjang);
            tvShopName = (TextView)itemView.findViewById(R.id.tv_shop_name);
            rvProductKeranjang = (RecyclerView)itemView.findViewById(R.id.rv_product_keranjang);
            tvTotalBayar = (TextView)itemView.findViewById(R.id.tv_total_bayar);
            btnProses = (Button)itemView.findViewById(R.id.btn_proses);
        }
    }
}