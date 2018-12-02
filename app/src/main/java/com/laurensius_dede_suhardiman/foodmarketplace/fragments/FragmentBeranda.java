package com.laurensius_dede_suhardiman.foodmarketplace.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.laurensius_dede_suhardiman.foodmarketplace.DetailProduct;
import com.laurensius_dede_suhardiman.foodmarketplace.R;
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


public class FragmentBeranda extends Fragment {

    private LinearLayout llFailed,llSuccess,llNoData;
    private EditText etCari;
    private Button btnCari;

    RecyclerView.LayoutManager mLayoutManager;
    List<Product> listProduct = new ArrayList<>();
    private RecyclerView rvProductLatest;
    private ProductAdapter productAdapter = null;

    public FragmentBeranda() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflaterBeranda = inflater.inflate(R.layout.fragment_beranda, container, false);
        llFailed = (LinearLayout)inflaterBeranda.findViewById(R.id.ll_failed);
        llNoData = (LinearLayout)inflaterBeranda.findViewById(R.id.ll_no_data);
        llSuccess = (LinearLayout)inflaterBeranda.findViewById(R.id.ll_success);
        etCari = (EditText)inflaterBeranda.findViewById(R.id.et_cari);
        btnCari = (Button)inflaterBeranda.findViewById(R.id.btn_cari);
        rvProductLatest = (RecyclerView)inflaterBeranda.findViewById(R.id.rv_product_latest);
        return inflaterBeranda;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        llNoData.setVisibility(View.GONE);
        llFailed.setVisibility(View.GONE);
        llSuccess.setVisibility(View.GONE);
        rvProductLatest.addOnItemTouchListener(new CustomListener(getActivity(), new CustomListener.OnItemClickListener() {
            @Override
            public void onItemClick(View childVew, int childAdapterPosition) {
                Product product = productAdapter.getItem(childAdapterPosition);
                Intent intent = new Intent(getActivity(),DetailProduct.class);
                intent.putExtra("productObject", product);
                startActivity(intent);
            }
        }));

        rvProductLatest.setAdapter(null);
        rvProductLatest.setHasFixedSize(true);
        rvProductLatest.setLayoutManager(new GridLayoutManager(getContext(),2));
        productAdapter = new ProductAdapter(listProduct,getContext());
        productAdapter.notifyDataSetChanged();
        rvProductLatest.setAdapter(productAdapter);
        requestProductLatest();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void requestProductLatest(){
        Random random = new Random();
        int rnd = random.nextInt(999999 - 99) + 99;
        String product_latest = getResources().getString(R.string.tag_request_product_latest);
        String url = getResources().getString(R.string.api)
                .concat(getResources().getString(R.string.endpoint_product_latest))
                .concat("10") //limit
                .concat(getResources().getString(R.string.slash))
                .concat(String.valueOf(rnd))
                .concat(getResources().getString(R.string.slash));
        final ProgressDialog pDialog = new ProgressDialog(getContext());
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
                        Toast.makeText(getContext(),"Response Error",Toast.LENGTH_LONG).show();
                        llNoData.setVisibility(View.GONE);
                        llFailed.setVisibility(View.VISIBLE);
                        llSuccess.setVisibility(View.GONE);
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjReq, product_latest);
    }

    public void parseData(JSONObject responseJsonObj){
        try{
            String severity = responseJsonObj.getString(getResources().getString(R.string.json_key_severity));
            JSONObject content = responseJsonObj.getJSONObject(getResources().getString(R.string.json_key_content));
            if(severity.equals(getResources().getString(R.string.success))){
                JSONArray jsonArrayProduct = content.getJSONArray(getResources().getString(R.string.json_key_product));
                if(jsonArrayProduct.length() > 0){
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