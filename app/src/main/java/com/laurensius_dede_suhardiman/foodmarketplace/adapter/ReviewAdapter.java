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
import com.laurensius_dede_suhardiman.foodmarketplace.model.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.HolderReview> {
    List<Review> listReview;
    Context ctx;

    public ReviewAdapter(List<Review> listReview, Context ctx){
        this.listReview = listReview;
        this.ctx = ctx;
    }

    @Override
    public HolderReview onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_review,viewGroup,false);
        HolderReview holderReview = new HolderReview(v);
        return holderReview;
    }

    @Override
    public void onBindViewHolder(HolderReview holderReview,final int i){
        holderReview.tvUserName.setText(listReview.get(i).getUser().getFullName());
        holderReview.tvProductRating.setText("Rating : " + listReview.get(i).getRating());
        holderReview.tvProductReview.setText("\"" +listReview.get(i).getReview() + "\"");
    }

    @Override
    public int getItemCount(){
        return listReview.size();
    }

    public Review getItem(int position){
        return listReview.get(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class HolderReview extends  RecyclerView.ViewHolder{
        CardView cvReview;
        TextView tvUserName,tvProductRating,tvProductReview;

        HolderReview(View itemView){
            super(itemView);
            cvReview = (CardView) itemView.findViewById(R.id.cv_item_review);
            tvUserName = (TextView)itemView.findViewById(R.id.tv_user_name);
            tvProductRating = (TextView)itemView.findViewById(R.id.tv_product_rating);
            tvProductReview = (TextView)itemView.findViewById(R.id.tv_product_review);
        }
    }
}