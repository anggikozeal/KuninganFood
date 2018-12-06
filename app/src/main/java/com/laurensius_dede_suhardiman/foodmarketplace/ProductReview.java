package com.laurensius_dede_suhardiman.foodmarketplace;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.laurensius_dede_suhardiman.foodmarketplace.appcontroller.AppController;
import com.laurensius_dede_suhardiman.foodmarketplace.model.Product;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ProductReview extends AppCompatActivity {

    private ImageView ivProductImage;
    private TextView tvProductName, tvProductPrice, tvProductStatus;
    private EditText etReview;
    private Button btnKirimReview;
    RatingBar rbRating;

    private Product productIntent;
    Intent intent;

    float ratingValue = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_review);
        intent = getIntent();
        productIntent = (Product)intent.getSerializableExtra("productObject");

        ivProductImage = (ImageView)findViewById(R.id.iv_product_image);
        tvProductName = (TextView)findViewById(R.id.tv_product_name);
        tvProductPrice = (TextView)findViewById(R.id.tv_product_price);
        tvProductStatus = (TextView)findViewById(R.id.tv_product_status);
        rbRating = (RatingBar)findViewById(R.id.rb_rating);
        etReview = (EditText)findViewById(R.id.et_review);
        btnKirimReview = (Button)findViewById(R.id.btn_review);

        rbRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingValue = rating;
            }
        });

        btnKirimReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInput();
            }
        });

        dataToView(productIntent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProductReview.this,DetailProduct.class);
        intent.putExtra("productObject", productIntent);
        startActivity(intent);
        finish();
    }

    public void dataToView(Product product){
        if(!product.getImage().equals("")){
            byte[] imageBytes = Base64.decode(product.getImage(), Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            ivProductImage.setImageBitmap(decodedImage);
        }
        tvProductName.setText(product.getName());
        tvProductPrice.setText("Harga : Rp. " + product.getPrice());
        tvProductStatus.setText("Status barang : " + product.getStatus());
    }

    public void validateInput(){
        if(etReview.getText().toString().equals("")){
            new AlertDialog.Builder(ProductReview.this)
                    .setTitle("Whooops . . .")
                    .setMessage("Silakan isi kolom penilaian!")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }}).show().
                    getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));

        }else
        if(FoodMarketplace.currentUser.getId().equals(productIntent.getShop().getIdUser())){
            new AlertDialog.Builder(ProductReview.this)
                    .setTitle("Whooops . . .")
                    .setMessage("Anda tidak dapat meberikan ulasan pada produk sendiri!")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }}).show().
                    getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));

        }else
        if(FoodMarketplace.currentUser == null){
            new AlertDialog.Builder(ProductReview.this)
                    .setTitle("Whooops . . .")
                    .setMessage("Anda tidak dapat memberikan ulasan, silakan login terlebih dahulu!")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }}).show().
                    getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));

        }
        else{
            requestReview(
                    productIntent.getId(),
                    FoodMarketplace.currentUser.getId(),
                    String.valueOf(rbRating.getRating()),
                    etReview.getText().toString()
            );
        }
    }

    public void requestReview(String idProduct,String idUser,String rating,String review){
        Random random = new Random();
        int rnd = random.nextInt(999999 - 99) + 99;
        String request_review = getResources().getString(R.string.tag_request_review_insert);
        String url = getResources().getString(R.string.api)
                .concat(getResources().getString(R.string.endpoint_review_insert))
                .concat(String.valueOf(rnd))
                .concat(getResources().getString(R.string.slash));
        final ProgressDialog pDialog = new ProgressDialog(ProductReview.this);
        pDialog.setMessage(getResources().getString(R.string.progress_loading));
        pDialog.show();
        final Map<String, String> params = new HashMap<String, String>();
        params.put("id_product", idProduct);
        params.put("id_user", idUser);
        params.put("rating", rating);
        params.put("review", review);
        JSONObject parameter = new JSONObject(params);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,url, parameter,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        Log.d(getResources().getString(R.string.debug),response.toString());
                        try{
                            String severity = response.getString("severity");
                            if(severity.equals("success")){
                                new AlertDialog.Builder(ProductReview.this)
                                        .setTitle("Informasi")
                                        .setMessage("Review berhasil disimpan!")
                                        .setIcon(android.R.drawable.ic_dialog_info)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                Intent intent = new Intent(ProductReview.this,DetailProduct.class);
                                                intent.putExtra("productObject", productIntent);
                                                startActivity(intent);
                                                finish();
                                            }}).show().
                                        getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
                            } else{
                                new AlertDialog.Builder(ProductReview.this)
                                        .setTitle("Whooops . . .")
                                        .setMessage("Tidak dapat menyimpan review, silakan coba lagi!")
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {

                                            }}).show().
                                        getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
                            }
                        }catch (JSONException e){
                            new AlertDialog.Builder(ProductReview.this)
                                    .setTitle("Whooops . . .")
                                    .setMessage("Terjadi kesalahan! Tidak dapat menyimpan review, silakan coba lagi!")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {

                                        }}).show().
                                    getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        new AlertDialog.Builder(ProductReview.this)
                                .setTitle("Whooops . . .")
                                .setMessage("Something went wrong. Please try again!")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                    }}).show().
                                getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjReq, request_review );
    }
}
