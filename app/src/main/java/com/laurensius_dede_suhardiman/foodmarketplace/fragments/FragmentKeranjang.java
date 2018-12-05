package com.laurensius_dede_suhardiman.foodmarketplace.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
import com.laurensius_dede_suhardiman.foodmarketplace.FoodMarketplace;
import com.laurensius_dede_suhardiman.foodmarketplace.R;
import com.laurensius_dede_suhardiman.foodmarketplace.adapter.KeranjangAdapter;
import com.laurensius_dede_suhardiman.foodmarketplace.adapter.ProductAdapter;
import com.laurensius_dede_suhardiman.foodmarketplace.appcontroller.AppController;
import com.laurensius_dede_suhardiman.foodmarketplace.model.Product;
import com.laurensius_dede_suhardiman.foodmarketplace.model.Shop;
import com.laurensius_dede_suhardiman.foodmarketplace.model.Transaction;
import com.laurensius_dede_suhardiman.foodmarketplace.model.TransactionDetail;
import com.laurensius_dede_suhardiman.foodmarketplace.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FragmentKeranjang extends Fragment {

    private LinearLayout llFailed,llSuccess,llNoData;

    RecyclerView.LayoutManager mLayoutManager;
    List<Transaction> listTransaction = new ArrayList<>();
    private RecyclerView rvKeranjang;
    private KeranjangAdapter keranjangAdapter = null;

    public FragmentKeranjang() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflaterKeranjang = inflater.inflate(R.layout.fragment_keranjang, container, false);
        llFailed = (LinearLayout)inflaterKeranjang.findViewById(R.id.ll_failed);
        llNoData = (LinearLayout)inflaterKeranjang.findViewById(R.id.ll_no_data);
        llSuccess = (LinearLayout)inflaterKeranjang.findViewById(R.id.ll_success);
        rvKeranjang = (RecyclerView)inflaterKeranjang.findViewById(R.id.rv_keranjang);
        return inflaterKeranjang;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        llNoData.setVisibility(View.GONE);
        llFailed.setVisibility(View.GONE);
        llSuccess.setVisibility(View.GONE);

        if(FoodMarketplace.currentUser != null){
            rvKeranjang.setAdapter(null);
            rvKeranjang.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getContext());
            rvKeranjang.setLayoutManager(mLayoutManager);
            keranjangAdapter = new KeranjangAdapter(listTransaction,getContext());
            keranjangAdapter.notifyDataSetChanged();
            rvKeranjang.setAdapter(keranjangAdapter);
            requestTransactionKeranjang();
        }else {
            llNoData.setVisibility(View.VISIBLE);
            llFailed.setVisibility(View.GONE);
            llSuccess.setVisibility(View.GONE);
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void requestTransactionKeranjang(){
        Random random = new Random();
        int rnd = random.nextInt(999999 - 99) + 99;
        String transac_keranjang = getResources().getString(R.string.tag_request_transac_keranjang);
        String url = getResources().getString(R.string.api)
                .concat(getResources().getString(R.string.endpoint_transac))
                .concat("id_user") //key
                .concat(getResources().getString(R.string.slash))
                .concat(FoodMarketplace.currentUser.getId()) //val
                .concat(getResources().getString(R.string.slash))
                .concat("ON_KERANJANG") //status
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
        AppController.getInstance().addToRequestQueue(jsonObjReq, transac_keranjang);
    }

    public void parseData(JSONObject responseJsonObj){
        try{
            String severity = responseJsonObj.getString(getResources().getString(R.string.json_key_severity));
            JSONObject content = responseJsonObj.getJSONObject(getResources().getString(R.string.json_key_content));
            if(severity.equals(getResources().getString(R.string.success))){
                JSONArray jsonArrayTransaction = content.getJSONArray(getResources().getString(R.string.json_key_transaction));
                List<TransactionDetail> transactionDetailList;
                if(jsonArrayTransaction.length() > 0){
                    for(int x=0;x<jsonArrayTransaction.length();x++){
                        JSONObject objTransaction = jsonArrayTransaction.getJSONObject(x);
                        JSONObject objShop = objTransaction.getJSONObject(getResources().getString(R.string.json_key_shop));
                        JSONObject objUser = objShop.getJSONObject(getResources().getString(R.string.json_key_user));
                        JSONArray arrayTransactionDetail = objTransaction.getJSONArray(getResources().getString(R.string.json_key_product));
                        transactionDetailList = new ArrayList<>();
                        if(arrayTransactionDetail.length() > 0){
                            for(int y=0;y<arrayTransactionDetail.length();y++){
                                JSONObject objProduct = arrayTransactionDetail.getJSONObject(y);
                                transactionDetailList.add(new TransactionDetail(
                                    objProduct.getString(getResources().getString(R.string.json_key_id)),
                                    objProduct.getString(getResources().getString(R.string.json_key_id_transaction)),
                                    objProduct.getString(getResources().getString(R.string.json_key_qty)),
                                    objProduct.getString(getResources().getString(R.string.json_key_note)),
                                    new Product(
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
                                        null
                                    )
                                ));
                            }
                        }
                        ///---------------------------------

                        listTransaction.add(
                                new Transaction(
                                    objTransaction.getString(getResources().getString(R.string.json_key_id)),
                                    objTransaction.getString(getResources().getString(R.string.json_key_id_shop)),
                                    objTransaction.getString(getResources().getString(R.string.json_key_id_user)),
                                    objTransaction.getString(getResources().getString(R.string.json_key_status)),
                                    objTransaction.getString(getResources().getString(R.string.json_key_image)),
                                    objTransaction.getString(getResources().getString(R.string.json_key_datetime)),
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
                                    ),
                                    transactionDetailList
                                )
                        );
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
        keranjangAdapter.notifyDataSetChanged();
    }
}
