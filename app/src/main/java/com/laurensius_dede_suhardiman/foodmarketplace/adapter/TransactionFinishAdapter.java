package com.laurensius_dede_suhardiman.foodmarketplace.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.laurensius_dede_suhardiman.foodmarketplace.R;
import com.laurensius_dede_suhardiman.foodmarketplace.model.TransactionDetail;

import java.util.List;

public class TransactionFinishAdapter extends RecyclerView.Adapter<TransactionFinishAdapter.HolderTransactionDetail> {
    List<TransactionDetail> listTransactionDetail;
    Context ctx;

    public TransactionFinishAdapter(List<TransactionDetail> listTransactionDetail, Context ctx){
        this.listTransactionDetail = listTransactionDetail;
        this.ctx = ctx;
    }

    @Override
    public HolderTransactionDetail onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_product_tagihan,viewGroup,false);
        HolderTransactionDetail holderTransactionDetail = new HolderTransactionDetail(v);
        return holderTransactionDetail;
    }

    @Override
    public void onBindViewHolder(HolderTransactionDetail holderTransactionDetail,final int i){
        if(!listTransactionDetail.get(i).getProduct().getImage().equals("")){
            byte[] imageBytes = Base64.decode(listTransactionDetail.get(i).getProduct().getImage(), Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holderTransactionDetail.ivProductImage.setImageBitmap(decodedImage);
        }
        holderTransactionDetail.tvProductName.setText(listTransactionDetail.get(i).getProduct().getName());
        holderTransactionDetail.tvProductPrice.setText("Harga " + listTransactionDetail.get(i).getProduct().getPrice());
        holderTransactionDetail.tvProductDiscount.setText("Diskon " + listTransactionDetail.get(i).getProduct().getDiscount());
        holderTransactionDetail.etQty.setText(listTransactionDetail.get(i).getQty());
    }

    @Override
    public int getItemCount(){
        return listTransactionDetail.size();
    }

    public TransactionDetail getItem(int position){
        return listTransactionDetail.get(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class HolderTransactionDetail extends  RecyclerView.ViewHolder{
        CardView cvItemProductKeranjang;
        ImageView ivProductImage,ivDelete;
        TextView tvProductName,tvProductPrice,tvProductDiscount;
        TextView tvMinus, tvPlus;
        EditText etQty;

        HolderTransactionDetail(View itemView){
            super(itemView);
            cvItemProductKeranjang = (CardView) itemView.findViewById(R.id.cv_item_product_keranjang);
            ivProductImage = (ImageView)itemView.findViewById(R.id.iv_product_image);
            tvProductName = (TextView)itemView.findViewById(R.id.tv_product_name); //
            tvProductPrice = (TextView)itemView.findViewById(R.id.tv_product_price); //
            tvProductDiscount = (TextView)itemView.findViewById(R.id.tv_product_discount); //
            tvMinus = (TextView)itemView.findViewById(R.id.tv_minus);
            tvPlus = (TextView)itemView.findViewById(R.id.tv_plus);
            etQty = (EditText)itemView.findViewById(R.id.et_qty);
            ivDelete = (ImageView)itemView.findViewById(R.id.iv_delete);
        }
    }
}