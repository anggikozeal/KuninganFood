package com.laurensius_dede_suhardiman.foodmarketplace.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.laurensius_dede_suhardiman.foodmarketplace.FoodMarketplace;
import com.laurensius_dede_suhardiman.foodmarketplace.R;
import com.laurensius_dede_suhardiman.foodmarketplace.adapter.KonfirmasiAdapter;
import com.laurensius_dede_suhardiman.foodmarketplace.adapter.ProsesAdapter;
import com.laurensius_dede_suhardiman.foodmarketplace.adapter.TagihanAdapter;
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

public class FragmentTransaksiPembelian extends Fragment {

    private LinearLayout llFailed,llSuccess,llNoData;

    RecyclerView.LayoutManager mLayoutManager;
    List<Transaction> listTransaction = new ArrayList<>();
    private RecyclerView rvTransaksiPembelian;
    private TagihanAdapter tagihanAdapter = null;
    private KonfirmasiAdapter konfirmasiAdapter = null;
    private ProsesAdapter prosesAdapter = null;

    private int onTagihan = 1;
    private int onKonfirmasi = 2;
    private int onProses = 3;
    private int onFinish = 4;

    private int stage;


    public FragmentTransaksiPembelian() { }

    public static FragmentTransaksiPembelian newInstance(int sectionNumber) {
        FragmentTransaksiPembelian fragment = new FragmentTransaksiPembelian();
        Bundle args = new Bundle();
        args.putInt("section_number", sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflaterTransaksiPembelian = inflater.inflate(R.layout.fragment_transaksi_pembelian, container, false);
        llFailed = (LinearLayout)inflaterTransaksiPembelian.findViewById(R.id.ll_failed);
        llNoData = (LinearLayout)inflaterTransaksiPembelian.findViewById(R.id.ll_no_data);
        llSuccess = (LinearLayout)inflaterTransaksiPembelian.findViewById(R.id.ll_success);
        rvTransaksiPembelian = (RecyclerView)inflaterTransaksiPembelian.findViewById(R.id.rv_transaksi);
        return inflaterTransaksiPembelian;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        stage = getArguments().getInt("section_number");

        llNoData.setVisibility(View.GONE);
        llFailed.setVisibility(View.GONE);
        llSuccess.setVisibility(View.GONE);

        dataToView();

        llFailed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataToView();
            }
        });
        

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void dataToView(){
        if(stage==onTagihan){
            listTransaction.clear();
            requestTransactionStage("ON_TAGIHAN");
            rvTransaksiPembelian.setAdapter(null);
            rvTransaksiPembelian.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getContext());
            rvTransaksiPembelian.setLayoutManager(mLayoutManager);
            tagihanAdapter = new TagihanAdapter(listTransaction,getActivity());
            tagihanAdapter.notifyDataSetChanged();
            rvTransaksiPembelian.setAdapter(tagihanAdapter);
        }else
        if(stage==onKonfirmasi){
            listTransaction.clear();
            requestTransactionStage("ON_KONFIRMASI");
            rvTransaksiPembelian.setAdapter(null);
            rvTransaksiPembelian.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getContext());
            rvTransaksiPembelian.setLayoutManager(mLayoutManager);
            konfirmasiAdapter = new KonfirmasiAdapter(listTransaction,getActivity());
            konfirmasiAdapter.notifyDataSetChanged();
            rvTransaksiPembelian.setAdapter(konfirmasiAdapter);
        }else
        if(stage==onProses){
            listTransaction.clear();
            requestTransactionStage("ON_PROSES");
            rvTransaksiPembelian.setAdapter(null);
            rvTransaksiPembelian.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getContext());
            rvTransaksiPembelian.setLayoutManager(mLayoutManager);
            prosesAdapter = new ProsesAdapter(listTransaction,getActivity());
            prosesAdapter.notifyDataSetChanged();
            rvTransaksiPembelian.setAdapter(prosesAdapter);
        }else
        if(stage==onFinish){
            listTransaction.clear();
            requestTransactionStage("ON_FINISH");
            rvTransaksiPembelian.setAdapter(null);
            rvTransaksiPembelian.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getContext());
            rvTransaksiPembelian.setLayoutManager(mLayoutManager);
            tagihanAdapter = new TagihanAdapter(listTransaction,getActivity());
            tagihanAdapter.notifyDataSetChanged();
            rvTransaksiPembelian.setAdapter(tagihanAdapter);
        }
    }

    public void requestTransactionStage(String levelStage){
        Random random = new Random();
        int rnd = random.nextInt(999999 - 99) + 99;
        String transac_by_status = getResources().getString(R.string.tag_request_transac_by_status);
        String url = getResources().getString(R.string.api)
                .concat(getResources().getString(R.string.endpoint_transac))
                .concat("id_user") //key
                .concat(getResources().getString(R.string.slash))
                .concat(FoodMarketplace.currentUser.getId()) //val
                .concat(getResources().getString(R.string.slash))
                .concat(levelStage) //status
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
        AppController.getInstance().addToRequestQueue(jsonObjReq, transac_by_status);
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
        if(stage==onTagihan){
            tagihanAdapter.notifyDataSetChanged();
        }else
        if(stage==onKonfirmasi){
            konfirmasiAdapter.notifyDataSetChanged();
        }else
        if(stage==onProses){
            prosesAdapter.notifyDataSetChanged();
        }else
        if(stage==onFinish){

        }

    }
}
