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
import android.widget.ImageView;
import android.widget.TextView;

import com.laurensius_dede_suhardiman.foodmarketplace.R;
import com.laurensius_dede_suhardiman.foodmarketplace.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.HolderProduct> {
    List<Product> listProduct;
    Context ctx;

    public ProductAdapter(List<Product> listProduct,Context ctx){
        this.listProduct = listProduct;
        this.ctx = ctx;
    }

    @Override
    public HolderProduct onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_product,viewGroup,false);
        HolderProduct holderProduct = new HolderProduct(v);
        return holderProduct;
    }

    @Override
    public void onBindViewHolder(HolderProduct holderProduct,final int i){
        if(!listProduct.get(i).getImage().equals("")){
            byte[] imageBytes = Base64.decode(listProduct.get(i).getImage(), Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holderProduct.ivProductImage.setImageBitmap(decodedImage);
        }
        holderProduct.tvProductName.setText(listProduct.get(i).getName());
        holderProduct.tvProductPrice.setText(ctx.getResources().getString(R.string.label_price).concat(listProduct.get(i).getPrice()));
        holderProduct.tvProductRating.setText(ctx.getResources().getString(R.string.label_rating).concat(listProduct.get(i).getRating()));
        holderProduct.tvShopName.setText(listProduct.get(i).getShop().getShopName());
    }

    @Override
    public int getItemCount(){
        return listProduct.size();
    }

    public Product getItem(int position){
        return listProduct.get(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class HolderProduct extends  RecyclerView.ViewHolder{
        CardView cvProduct;
        ImageView ivProductImage;
        TextView tvProductName,tvShopName,tvProductPrice,tvProductRating;

        HolderProduct(View itemView){
            super(itemView);
            cvProduct = (CardView) itemView.findViewById(R.id.cv_item_product);
            ivProductImage = (ImageView)itemView.findViewById(R.id.iv_product_image);
            tvProductName = (TextView)itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = (TextView)itemView.findViewById(R.id.tv_product_price);
            tvProductRating= (TextView)itemView.findViewById(R.id.tv_product_rating);
            tvShopName = (TextView)itemView.findViewById(R.id.tv_shop_name);

        }
    }
}