package com.laurensius_dede_suhardiman.foodmarketplace;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.laurensius_dede_suhardiman.foodmarketplace.adapter.ReviewAdapter;
import com.laurensius_dede_suhardiman.foodmarketplace.adapter.TransactionOrderAdapter;
import com.laurensius_dede_suhardiman.foodmarketplace.appcontroller.AppController;
import com.laurensius_dede_suhardiman.foodmarketplace.model.Product;
import com.laurensius_dede_suhardiman.foodmarketplace.model.Review;
import com.laurensius_dede_suhardiman.foodmarketplace.model.Shop;
import com.laurensius_dede_suhardiman.foodmarketplace.model.TransactionDetail;
import com.laurensius_dede_suhardiman.foodmarketplace.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DetailProduct extends AppCompatActivity {

    private LinearLayout llReview;
    private ImageView ivProductImage;
    private TextView tvProductName, tvProductPrice, tvProductRating, tvProductStatus, tvShopName, tvShopAddress, tvProductDescription;
    private Button btnKeranjang, btnReview;
    private RecyclerView rvProductReview;
    RecyclerView.LayoutManager mLayoutManager;
    List<Review> listReview = new ArrayList<>();
    ReviewAdapter reviewAdapter = null;


    private RatingBar rbRating;
    private EditText etQty, etNote;
    private Product productIntent;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);
        llReview = (LinearLayout)findViewById(R.id.ll_review);
        llReview.setVisibility(View.GONE);

        ivProductImage = (ImageView)findViewById(R.id.iv_product_image);
        tvProductName = (TextView)findViewById(R.id.tv_product_name);
        tvProductPrice = (TextView)findViewById(R.id.tv_product_price);
        tvProductRating = (TextView)findViewById(R.id.tv_product_rating);
        rbRating = (RatingBar)findViewById(R.id.rb_rating);
        tvProductStatus = (TextView)findViewById(R.id.tv_product_status);
        tvProductDescription = (TextView)findViewById(R.id.tv_product_description);
        tvShopName = (TextView)findViewById(R.id.tv_shop_name);
        tvShopAddress = (TextView)findViewById(R.id.tv_shop_address);
        btnKeranjang = (Button)findViewById(R.id.btn_keranjang);
        etQty = (EditText)findViewById(R.id.et_qty);
        etNote = (EditText)findViewById(R.id.et_note);


        intent = getIntent();
        productIntent = (Product)intent.getSerializableExtra("productObject");
        dataToView(productIntent);


        btnReview = (Button)findViewById(R.id.btn_review);
        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailProduct.this,ProductReview.class);
                intent.putExtra("productObject", productIntent);
                startActivity(intent);
                finish();
            }
        });

        btnKeranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateSession();
            }
        });
        
        rvProductReview = (RecyclerView)findViewById(R.id.rv_product_review);
        rvProductReview.setAdapter(null);
        rvProductReview.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(DetailProduct.this);
        rvProductReview.setLayoutManager(mLayoutManager);
        reviewAdapter = new ReviewAdapter(listReview,DetailProduct.this);
        reviewAdapter.notifyDataSetChanged();
        rvProductReview.setAdapter(reviewAdapter);

        requestReview();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void dataToView(Product product){
        tvProductName.setText(product.getName());
        tvProductPrice.setText("Harga : Rp. " + product.getPrice());
        tvProductRating.setText(product.getRating());
        tvProductStatus.setText("Status barang : " + product.getStatus());
        tvProductDescription.setText(product.getDescription());
        tvShopName.setText(product.getShop().getShopName());
        tvShopAddress.setText(product.getShop().getAddress());
        if(!product.getImage().equals("")){
            byte[] imageBytes = Base64.decode(product.getImage(), Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            ivProductImage.setImageBitmap(decodedImage);
        }
        rbRating.setRating(Float.parseFloat(product.getRating()));
        rbRating.setEnabled(false);
    }

    public void validateSession(){
        if(FoodMarketplace.currentUser != null){
            Toast.makeText(DetailProduct.this,"Proses Pemesanan",Toast.LENGTH_LONG).show();
            if(productIntent.getStatus().equals("Tersedia")){
                if(productIntent.getShop().getIdUser().equals(FoodMarketplace.currentUser.getId())){
                    new AlertDialog.Builder(DetailProduct.this)
                            .setTitle("Maaf . . .")
                            .setMessage("Anda tidak dapat melakukan pemesanan produk dari toko sendiri. Terima kasih!")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(getResources().getString(R.string.dialog_ya), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }})
                            .show();
                }else{
                    if(!etQty.getText().toString().equals("") && Integer.parseInt(etQty.getText().toString()) > 0){
                        new AlertDialog.Builder(DetailProduct.this)
                                .setTitle("Konfirmasi")
                                .setMessage("Masukkan barang ke keranjang belanja?")
                                .setIcon(android.R.drawable.ic_input_add)
                                .setPositiveButton(getResources().getString(R.string.dialog_ya), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
//                                      public void requestTransactionInsert(String idShop, String idUser, String idProduct, String qty, String price, String discount, String note){
                                        requestTransactionInsert(
                                                productIntent.getIdShop(),
                                                FoodMarketplace.currentUser.getId(),
                                                productIntent.getId(),
                                                etQty.getText().toString(),
                                                productIntent.getPrice(),
                                                productIntent.getDiscount(),
                                                etNote.getText().toString()
                                        );
                                    }})
                                .setNegativeButton(getResources().getString(R.string.dialog_tidak), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        FoodMarketplace.activity.finish();
                                        Intent intent = new Intent(DetailProduct.this,Register.class);
                                        startActivity(intent);
                                        finish();
                                    }})
                                .show();
                    }else{
                        new AlertDialog.Builder(DetailProduct.this)
                                .setTitle("Peringatan")
                                .setMessage("Silakan masukkan jumlah pemesanan minimal 1")
                                .setIcon(android.R.drawable.ic_input_add)
                                .setPositiveButton(getResources().getString(R.string.dialog_ya), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        //input ke keranjang
                                    }})
                                .show();

                    }
                }
            }else{
                new AlertDialog.Builder(DetailProduct.this)
                        .setTitle("Maaf . . .")
                        .setMessage("Anda tidak dapat melakukan pemesanan, produk tidak tersedia untuk saat ini. Terima kasih!")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(getResources().getString(R.string.dialog_ya), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }})
                        .show();
            }

        }else{
            new AlertDialog.Builder(DetailProduct.this)
                    .setTitle("Maaf . . .")
                    .setMessage("Anda tidak dapat melakukan pemesanan, silakan login atau lakukan registrasi agar dapat melakukan pemesanan. Terima kasih!")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(getResources().getString(R.string.alert_login), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            FoodMarketplace.activity.finish();
                            Intent intent = new Intent(DetailProduct.this,Login.class);
                            startActivity(intent);
                            finish();
                        }})
                    .setNegativeButton(getResources().getString(R.string.alert_register), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            FoodMarketplace.activity.finish();
                            Intent intent = new Intent(DetailProduct.this,Register.class);
                            startActivity(intent);
                            finish();
                        }})
                    .show();
        }
    }

    public void requestTransactionInsert(String idShop, String idUser, String idProduct, String qty, String price, String discount, String note){
        Random random = new Random();
        int rnd = random.nextInt(999999 - 99) + 99;
        String transactionInsert = getResources().getString(R.string.tag_request_transac_insert);
        String url = getResources().getString(R.string.api)
                .concat(getResources().getString(R.string.endpoint_transac_insert))
                .concat(String.valueOf(rnd))
                .concat(getResources().getString(R.string.slash));
        final ProgressDialog pDialog = new ProgressDialog(DetailProduct.this);
        pDialog.setMessage(getResources().getString(R.string.progress_loading));
        pDialog.show();
        final Map<String, String> params = new HashMap<String, String>();
        params.put("id_shop", idShop);
        params.put("id_user", idUser);
        params.put("id_product", idProduct);
        params.put("qty", qty);
        params.put("price", price);
        params.put("discount", discount);
        params.put("note", note);
        JSONObject parameter = new JSONObject(params);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,url, parameter,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        Log.d(getResources().getString(R.string.debug),response.toString());
                        parseData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            String jsonError = new String(networkResponse.data);
                            Log.d(getResources().getString(R.string.debug),jsonError.toString());
                        }
                        new AlertDialog.Builder(DetailProduct.this)
                                .setTitle("Whooops . . .")
                                .setMessage("Barang gagal ditambahkan ke keranjang!" + error)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                    }}).show().
                                getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjReq, transactionInsert);
    }

    public void parseData(JSONObject responseJsonObj){
        Log.d(getResources().getString(R.string.debug),responseJsonObj.toString());
        try{
            String severity = responseJsonObj.getString(getResources().getString(R.string.json_key_severity));
            final JSONObject content = responseJsonObj.getJSONObject(getResources().getString(R.string.json_key_content));
            if(severity.equals(getResources().getString(R.string.success))){
                new AlertDialog.Builder(DetailProduct.this)
                        .setTitle("Sukses")
                        .setMessage(responseJsonObj.getString("message"))
                        .setIcon(android.R.drawable.ic_menu_info_details)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();

                            }}).show().
                        getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));

            }else{
                new AlertDialog.Builder(DetailProduct.this)
                        .setTitle("Whoops . . .")
                        .setMessage(responseJsonObj.getString("message"))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }}).show().
                        getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
            }
        }catch (JSONException e){
            new AlertDialog.Builder(DetailProduct.this)
                    .setTitle("Whooops . . .")
                    .setMessage("Terjadi kendala saat terhubung ke server! Silakan coba lagi!")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }}).show().
                    getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
        }
    }


    //review
    public void requestReview(){
        Random random = new Random();
        int rnd = random.nextInt(999999 - 99) + 99;
        String product_latest = getResources().getString(R.string.tag_request_review);
        String url = getResources().getString(R.string.api)
                .concat(getResources().getString(R.string.endpoint_review))
                .concat("id_product") //limit
                .concat(getResources().getString(R.string.slash))
                .concat(productIntent.getId())
                .concat(getResources().getString(R.string.slash))
                .concat(String.valueOf(rnd))
                .concat(getResources().getString(R.string.slash));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(getResources().getString(R.string.debug),response.toString());
                        parseDataReview(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DetailProduct.this,"Load Review Error",Toast.LENGTH_LONG).show();
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjReq, product_latest);
    }

    public void parseDataReview(JSONObject responseJsonObj){
        try{
            String severity = responseJsonObj.getString(getResources().getString(R.string.json_key_severity));
            JSONObject content = responseJsonObj.getJSONObject(getResources().getString(R.string.json_key_content));
            if(severity.equals(getResources().getString(R.string.success))){
                JSONArray jsonArrayReview = content.getJSONArray("review");
                if(jsonArrayReview.length() > 0){
                    llReview.setVisibility(View.VISIBLE);
                    for(int x=0;x<jsonArrayReview.length();x++){
                        listReview.add(new Review(
                                jsonArrayReview.getJSONObject(x).getString("id"),
                                jsonArrayReview.getJSONObject(x).getString("id_product"),
                                jsonArrayReview.getJSONObject(x).getString("id_user"),
                                jsonArrayReview.getJSONObject(x).getString("rating"),
                                jsonArrayReview.getJSONObject(x).getString("review"),
                                new Product(
                                        jsonArrayReview.getJSONObject(x).getJSONObject("product").getString(getResources().getString(R.string.json_key_id)),
                                        jsonArrayReview.getJSONObject(x).getJSONObject("product").getString(getResources().getString(R.string.json_key_id_shop)),
                                        jsonArrayReview.getJSONObject(x).getJSONObject("product").getString(getResources().getString(R.string.json_key_name)),
                                        jsonArrayReview.getJSONObject(x).getJSONObject("product").getString(getResources().getString(R.string.json_key_category)),
                                        jsonArrayReview.getJSONObject(x).getJSONObject("product").getString(getResources().getString(R.string.json_key_status)),
                                        jsonArrayReview.getJSONObject(x).getJSONObject("product").getString(getResources().getString(R.string.json_key_price)),
                                        jsonArrayReview.getJSONObject(x).getJSONObject("product").getString(getResources().getString(R.string.json_key_discount)),
                                        jsonArrayReview.getJSONObject(x).getJSONObject("product").getString(getResources().getString(R.string.json_key_description)),
                                        jsonArrayReview.getJSONObject(x).getJSONObject("product").getString(getResources().getString(R.string.json_key_rating)),
                                        jsonArrayReview.getJSONObject(x).getJSONObject("product").getString(getResources().getString(R.string.json_key_image)),
                                        null
                                ),
                                new User(
                                        jsonArrayReview.getJSONObject(x).getJSONObject("user").getString("id"),
                                        jsonArrayReview.getJSONObject(x).getJSONObject("user").getString("username"),
                                        jsonArrayReview.getJSONObject(x).getJSONObject("user").getString("password"),
                                        jsonArrayReview.getJSONObject(x).getJSONObject("user").getString("full_name"),
                                        jsonArrayReview.getJSONObject(x).getJSONObject("user").getString("address"),
                                        jsonArrayReview.getJSONObject(x).getJSONObject("user").getString("phone"),
                                        jsonArrayReview.getJSONObject(x).getJSONObject("user").getString("last_login")
                                )

                        ));
                    }

                }


            }
        }catch (JSONException e){
            Toast.makeText(DetailProduct.this,"Load Review Error ",Toast.LENGTH_LONG).show();
        }
        reviewAdapter.notifyDataSetChanged();
    }
}
