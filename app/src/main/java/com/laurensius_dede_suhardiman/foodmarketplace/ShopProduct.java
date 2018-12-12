package com.laurensius_dede_suhardiman.foodmarketplace;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.laurensius_dede_suhardiman.foodmarketplace.adapter.ProductAdapter;
import com.laurensius_dede_suhardiman.foodmarketplace.appcontroller.AppController;
import com.laurensius_dede_suhardiman.foodmarketplace.customlistener.CustomListener;
import com.laurensius_dede_suhardiman.foodmarketplace.model.Product;
import com.laurensius_dede_suhardiman.foodmarketplace.model.Shop;
import com.laurensius_dede_suhardiman.foodmarketplace.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShopProduct extends AppCompatActivity {

    private LinearLayout llFailed,llSuccess,llNoData;
    private EditText etCari;
    private Button btnCari;

    RecyclerView.LayoutManager mLayoutManager;
    List<Product> listProduct = new ArrayList<>();
    private RecyclerView rvProduct;
    private ProductAdapter productAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_product);
        llFailed = (LinearLayout)findViewById(R.id.ll_failed);
        llNoData = (LinearLayout)findViewById(R.id.ll_no_data);
        llSuccess = (LinearLayout)findViewById(R.id.ll_success);
        etCari = (EditText)findViewById(R.id.et_cari);
        btnCari = (Button)findViewById(R.id.btn_cari);
        rvProduct= (RecyclerView)findViewById(R.id.rv_product);

        rvProduct.addOnItemTouchListener(new CustomListener(ShopProduct.this, new CustomListener.OnItemClickListener() {
            @Override
            public void onItemClick(View childVew, int childAdapterPosition) {
                Product product = productAdapter.getItem(childAdapterPosition);
                Intent intent = new Intent(ShopProduct.this,EditProduct.class);
                intent.putExtra("productObject", product);
                startActivity(intent);

            }
        }));


        rvProduct.setAdapter(null);
        rvProduct.setHasFixedSize(true);
        rvProduct.setLayoutManager(new GridLayoutManager(ShopProduct.this,2));
        productAdapter = new ProductAdapter(listProduct,ShopProduct.this);
        productAdapter.notifyDataSetChanged();
        rvProduct.setAdapter(productAdapter);
        requestProductSearchByShop(FoodMarketplace.currentShop.getId(),"system_all");

        btnCari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etCari.getText().toString().equals("") || etCari.getText().toString() == null){
                    requestProductSearchByShop(FoodMarketplace.currentShop.getId(),"system_all");
                }else{
                    requestProductSearchByShop(FoodMarketplace.currentShop.getId(),etCari.getText().toString());
                }
            }
        });

    }

    public void requestProductSearchByShop(String idShop, String val){
        Random random = new Random();
        int rnd = random.nextInt(999999 - 99) + 99;
        String product_by_shop = getResources().getString(R.string.tag_request_product_by_shop);
        String url = getResources().getString(R.string.api)
                .concat(getResources().getString(R.string.endpoint_product_by_shop))
                .concat(idShop) //limit
                .concat(getResources().getString(R.string.slash))
                .concat(val) //limit
                .concat(getResources().getString(R.string.slash))
                .concat(String.valueOf(rnd))
                .concat(getResources().getString(R.string.slash));
        final ProgressDialog pDialog = new ProgressDialog(ShopProduct.this);
        pDialog.setMessage(getResources().getString(R.string.progress_loading));
        pDialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        Log.d(getResources().getString(R.string.debug),response.toString());
                        //Toast.makeText(getContext(),response.toString(),Toast.LENGTH_LONG).show();
                        parseData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        Toast.makeText(ShopProduct.this,"Response Error",Toast.LENGTH_LONG).show();
                        llNoData.setVisibility(View.GONE);
                        llFailed.setVisibility(View.VISIBLE);
                        llSuccess.setVisibility(View.GONE);
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjReq, product_by_shop);
    }

    public void parseData(JSONObject responseJsonObj){
        try{
            String severity = responseJsonObj.getString(getResources().getString(R.string.json_key_severity));
            JSONObject content = responseJsonObj.getJSONObject(getResources().getString(R.string.json_key_content));
            if(severity.equals(getResources().getString(R.string.success))){
                JSONArray jsonArrayProduct = content.getJSONArray(getResources().getString(R.string.json_key_product));
                if(jsonArrayProduct.length() > 0){
                    listProduct.clear();
                    productAdapter.notifyDataSetChanged();
                    for(int x=0;x<jsonArrayProduct.length();x++){
                        JSONObject objProduct = jsonArrayProduct.getJSONObject(x);
                        JSONObject objShop = objProduct.getJSONObject(getResources().getString(R.string.json_key_shop));
                        JSONObject objUser = objShop.getJSONObject(getResources().getString(R.string.json_key_user));
                        listProduct.add(new Product(
                                objProduct.getString(getResources().getString(R.string.json_key_id)),
                                objProduct.getString(getResources().getString(R.string.json_key_id_shop)),
                                objProduct.getString(getResources().getString(R.string.json_key_name)),
                                objProduct.getString(getResources().getString(R.string.json_key_category)),
                                objProduct.getString(getResources().getString(R.string.json_key_status)),
                                objProduct.getString(getResources().getString(R.string.json_key_price)),
                                objProduct.getString(getResources().getString(R.string.json_key_discount)),
                                objProduct.getString(getResources().getString(R.string.json_key_description)),
                                objProduct.getString(getResources().getString(R.string.json_key_rating)),
                                objProduct.getString(getResources().getString(R.string.json_key_image)),
                                new Shop(
                                        objShop.getString(getResources().getString(R.string.json_key_id)),
                                        objShop.getString(getResources().getString(R.string.json_key_id_user)),
                                        objShop.getString(getResources().getString(R.string.json_key_shop_name)),
                                        objShop.getString(getResources().getString(R.string.json_key_address)),
                                        new User(
                                                objUser.getString(getResources().getString(R.string.json_key_id)),
                                                objUser.getString(getResources().getString(R.string.json_key_username)),
                                                objUser.getString(getResources().getString(R.string.json_key_password)),
                                                objUser.getString(getResources().getString(R.string.json_key_full_name)),
                                                objUser.getString(getResources().getString(R.string.json_key_address)),
                                                objUser.getString(getResources().getString(R.string.json_key_phone)),
                                                objUser.getString(getResources().getString(R.string.json_key_last_login))
                                        )
                                )
                        ));
                    }
                    llNoData.setVisibility(View.GONE);
                    llFailed.setVisibility(View.GONE);
                    llSuccess.setVisibility(View.VISIBLE);
                }else {
                    llNoData.setVisibility(View.VISIBLE);
                    llFailed.setVisibility(View.GONE);
                    llSuccess.setVisibility(View.GONE);
                }
            }else{
                llNoData.setVisibility(View.GONE);
                llFailed.setVisibility(View.VISIBLE);
                llSuccess.setVisibility(View.GONE);
            }
        }catch (JSONException e){
            llNoData.setVisibility(View.GONE);
            llFailed.setVisibility(View.VISIBLE);
            llSuccess.setVisibility(View.GONE);
        }
        productAdapter.notifyDataSetChanged();
    }
}
