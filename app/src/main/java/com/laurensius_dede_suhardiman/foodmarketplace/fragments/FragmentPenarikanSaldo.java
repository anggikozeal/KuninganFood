package com.laurensius_dede_suhardiman.foodmarketplace.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.laurensius_dede_suhardiman.foodmarketplace.FoodMarketplace;
import com.laurensius_dede_suhardiman.foodmarketplace.Login;
import com.laurensius_dede_suhardiman.foodmarketplace.PenarikanSaldo;
import com.laurensius_dede_suhardiman.foodmarketplace.R;
import com.laurensius_dede_suhardiman.foodmarketplace.adapter.DrawdownAdapter;
import com.laurensius_dede_suhardiman.foodmarketplace.adapter.TransactionHistoryAdapter;
import com.laurensius_dede_suhardiman.foodmarketplace.appcontroller.AppController;
import com.laurensius_dede_suhardiman.foodmarketplace.model.Drawdown;
import com.laurensius_dede_suhardiman.foodmarketplace.model.Transaction;
import com.laurensius_dede_suhardiman.foodmarketplace.model.TransactionHistory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FragmentPenarikanSaldo extends Fragment {

    private LinearLayout llFailed,llSuccess,llNoData;
    
    private LinearLayout llCekSaldo;
    private TextView tvSaldoTersedia;

    private LinearLayout llFormPenarikan;
    private EditText etPenarikan;
    private Button btnPenarikan;


    RecyclerView.LayoutManager mLayoutManager;

    List<TransactionHistory> listTransactionHistory = new ArrayList<>();
    List<Drawdown> listDrawdown = new ArrayList<>();

    DrawdownAdapter drawdownnAdapter = null;
    TransactionHistoryAdapter transactionHistoryAdapter = null;

    private RecyclerView rvRiwayatTransaksi;
    private RecyclerView rvRiwayatPencairan;


    private int riwayatPenjualan = 1;
    private int cekSaldo = 2;
    private int formPenarikan = 3;
    private int riwayatPenarikan = 4;

    private int stage;

    private String idShop;

    private double totalPenjualan;
    private double totalPenarikan;
    private double saldoTersedia;

    private int ctrOnRequest;

    public FragmentPenarikanSaldo() { }

    public static FragmentPenarikanSaldo newInstance(int sectionNumber) {
        FragmentPenarikanSaldo fragment = new FragmentPenarikanSaldo();
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
        View inflaterPernarikanSaldo = inflater.inflate(R.layout.fragment_penarikan_saldo, container, false);
        llFailed = (LinearLayout)inflaterPernarikanSaldo.findViewById(R.id.ll_failed);
        llNoData = (LinearLayout)inflaterPernarikanSaldo.findViewById(R.id.ll_no_data);
        llSuccess = (LinearLayout)inflaterPernarikanSaldo.findViewById(R.id.ll_success);

        rvRiwayatTransaksi = (RecyclerView)inflaterPernarikanSaldo.findViewById(R.id.rv_riwayat_transaksi);

        llCekSaldo = (LinearLayout)inflaterPernarikanSaldo.findViewById(R.id.ll_cek_saldo);
        tvSaldoTersedia = (TextView)inflaterPernarikanSaldo.findViewById(R.id.tv_saldo_tersedia);

        llFormPenarikan = (LinearLayout)inflaterPernarikanSaldo.findViewById(R.id.ll_form_penarikan);
        etPenarikan =(EditText)inflaterPernarikanSaldo.findViewById(R.id.et_penarikan);
        btnPenarikan = (Button)inflaterPernarikanSaldo.findViewById(R.id.btn_penarikan);

        rvRiwayatPencairan = (RecyclerView)inflaterPernarikanSaldo.findViewById(R.id.rv_riwayat_pencairan);
        return inflaterPernarikanSaldo;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        stage = getArguments().getInt("section_number");
        idShop = FoodMarketplace.currentShop.getId();

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


        btnPenarikan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etPenarikan.getText().toString().equals("") || etPenarikan.getText() == null ){
                    new AlertDialog.Builder(getContext())
                            .setTitle("Informasi")
                            .setMessage("Silakan masukan nominal yang ingin Anda cairkan!")
                            .setIcon(android.R.drawable.ic_menu_info_details)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }}).show().
                            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
                }else{
                    if(saldoTersedia < 500000 || ctrOnRequest > 0 || (saldoTersedia < Double.parseDouble(etPenarikan.getText().toString()))){
                        new AlertDialog.Builder(getContext())
                                .setTitle("Informasi")
                                .setMessage("Tidak dapat melakukan pencairan! Pastikan saldo memenuhi dan nilai transaksi Anda sudah lebih dari Rp 500.000,00 dan tidak ada request pencairan dengan status ON_REQUEST! ")
                                .setIcon(android.R.drawable.ic_menu_info_details)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
                                    }}).show().
                                getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
                    }else
                     if(saldoTersedia > 500000 && ctrOnRequest == 0){
                        new AlertDialog.Builder(getContext())
                                .setTitle("Konfirmasi")
                                .setMessage("Request pencairan?")
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
                                        requestPencairan();
                                    }}).show().
                                getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
                    }
                }
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
        listDrawdown.clear();
        rvRiwayatPencairan.setAdapter(null);
        rvRiwayatPencairan.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        rvRiwayatPencairan.setLayoutManager(mLayoutManager);
        drawdownnAdapter = new DrawdownAdapter(listDrawdown,getActivity());
        drawdownnAdapter.notifyDataSetChanged();
        rvRiwayatPencairan.setAdapter(drawdownnAdapter);


        listTransactionHistory.clear();
        rvRiwayatTransaksi.setAdapter(null);
        rvRiwayatTransaksi.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        rvRiwayatTransaksi.setLayoutManager(mLayoutManager);
        transactionHistoryAdapter = new TransactionHistoryAdapter(listTransactionHistory,getActivity());
        transactionHistoryAdapter.notifyDataSetChanged();
        rvRiwayatTransaksi.setAdapter(transactionHistoryAdapter);

        requestDrawdownByShop();

        if(stage==riwayatPenjualan) {
            rvRiwayatTransaksi.setVisibility(View.VISIBLE);
            llCekSaldo.setVisibility(View.GONE);
            llFormPenarikan.setVisibility(View.GONE);
            rvRiwayatPencairan.setVisibility(View.GONE);
        }else
        if(stage==cekSaldo){
            rvRiwayatTransaksi.setVisibility(View.GONE);
            llCekSaldo.setVisibility(View.VISIBLE);
            llFormPenarikan.setVisibility(View.GONE);
            rvRiwayatPencairan.setVisibility(View.GONE);
        }else
        if(stage==formPenarikan){
            rvRiwayatTransaksi.setVisibility(View.GONE);
            llCekSaldo.setVisibility(View.GONE);
            llFormPenarikan.setVisibility(View.VISIBLE);
            rvRiwayatPencairan.setVisibility(View.GONE);
        }else
        if(stage==riwayatPenarikan){
            rvRiwayatTransaksi.setVisibility(View.GONE);
            llCekSaldo.setVisibility(View.GONE);
            llFormPenarikan.setVisibility(View.GONE);
            rvRiwayatPencairan.setVisibility(View.VISIBLE);
        }
    }

    public void requestDrawdownByShop(){
        Random random = new Random();
        int rnd = random.nextInt(999999 - 99) + 99;
        String drawdown_by_shop = getResources().getString(R.string.tag_request_drawdown_by_shop);
        String url = getResources().getString(R.string.api)
                .concat(getResources().getString(R.string.endpoint_drawdown_by_shop))
                .concat(idShop) //val
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
                        Log.d(getResources().getString(R.string.debug),String.valueOf(response));
                        pDialog.dismiss();
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
        AppController.getInstance().addToRequestQueue(jsonObjReq, drawdown_by_shop);
    }

    public void parseData(JSONObject responseJsonObj){
        saldoTersedia = 0;
        totalPenjualan = 0;
        totalPenarikan = 0;
        try{
            String severity = responseJsonObj.getString(getResources().getString(R.string.json_key_severity));
            JSONObject content = responseJsonObj.getJSONObject(getResources().getString(R.string.json_key_content));
            if(severity.equals(getResources().getString(R.string.success))){
                Log.d(getResources().getString(R.string.debug),"Sukses load data");
                JSONArray jsonArrayDrawdown = content.getJSONArray("drawdown");
                JSONArray jsonArrayTransaction = content.getJSONArray("transaction");

                if (jsonArrayTransaction.length() > 0){
                    for(int x=0;x<jsonArrayTransaction.length();x++){
                        JSONObject jsonObjectTransaction = jsonArrayTransaction.getJSONObject(x);
                        totalPenjualan = totalPenjualan + Double.parseDouble(jsonObjectTransaction.getJSONObject("history").getString("total"));
                        listTransactionHistory.add(new TransactionHistory(
                            jsonObjectTransaction.getJSONObject("history").getString("id"),
                            jsonObjectTransaction.getJSONObject("history").getString("id_transaction"),
                            jsonObjectTransaction.getJSONObject("history").getString("total"),
                            jsonObjectTransaction.getJSONObject("history").getString("datetime")
                        ));
                    }
                }

                if(jsonArrayDrawdown.length() > 0){
                    for(int x=0;x<jsonArrayDrawdown.length();x++){
                        Log.d(getResources().getString(R.string.debug),String.valueOf(x));
                        JSONObject objectDrawdown = jsonArrayDrawdown.getJSONObject(x);
                        listDrawdown.add(new Drawdown(
                                objectDrawdown.getString("id"),
                                objectDrawdown.getString("id_shop"),
                                objectDrawdown.getString("total"),
                                objectDrawdown.getString("status"),
                                objectDrawdown.getString("datetime_request"),
                                objectDrawdown.getString("datetime_appove")
                        ));

                        if(objectDrawdown.getString("status").equals("ON_APPROVE")){
                            totalPenarikan = totalPenarikan + + Double.parseDouble(objectDrawdown.getString("total"));
                        }else{
                            ctrOnRequest++;
                        }
                    }
                    saldoTersedia = totalPenjualan - totalPenarikan;
                    tvSaldoTersedia.setText("Rp  " + String.valueOf(saldoTersedia));
                    llNoData.setVisibility(View.GONE);
                    llFailed.setVisibility(View.GONE);
                    llSuccess.setVisibility(View.VISIBLE);
                }else {
                    saldoTersedia = totalPenjualan - totalPenarikan;
                    tvSaldoTersedia.setText("Rp  " + String.valueOf(saldoTersedia));
                    llNoData.setVisibility(View.GONE);
                    llFailed.setVisibility(View.GONE);
                    llSuccess.setVisibility(View.VISIBLE);
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
        drawdownnAdapter.notifyDataSetChanged();
    }

    public void requestPencairan(){
        Random random = new Random();
        int rnd = random.nextInt(999999 - 99) + 99;
        String insert_pencairan = getResources().getString(R.string.tag_request_drawdown_insert);
        String url = getResources().getString(R.string.api)
                .concat(getResources().getString(R.string.endpoint_drawdown_insert))
                .concat(String.valueOf(rnd))
                .concat(getResources().getString(R.string.slash));
        final ProgressDialog pDialog = new ProgressDialog(getContext());
        pDialog.setMessage(getResources().getString(R.string.progress_loading));
        pDialog.show();
        final Map<String, String> params = new HashMap<String, String>();
        params.put("id_shop", FoodMarketplace.currentShop.getId());
        params.put("total", etPenarikan.getText().toString());
        JSONObject parameter = new JSONObject(params);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,url, parameter,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        Log.d(getResources().getString(R.string.debug),response.toString());
                        try{
                            if(response.getString(getResources().getString(R.string.json_key_severity)).equals("success")){
                                new AlertDialog.Builder(getContext())
                                        .setTitle("Sukses")
                                        .setMessage("Request pencairan hasil penjualan senilai Rp. " + etPenarikan.getText().toString() + " berhasil dikirim!")
                                        .setIcon(android.R.drawable.ic_dialog_info)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                Intent intent = new Intent(getContext(),PenarikanSaldo.class);
                                                startActivity(intent);
                                                PenarikanSaldo.activity.finish();
                                            }}).show().
                                        getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
                            }  else{
                                new AlertDialog.Builder(getContext())
                                        .setTitle("Gagal")
                                        .setMessage("Request pencairan hasil penjualan gagal! Silakan coba lagi!")
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                            }}).show().
                                        getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
                            }
                        }catch (JSONException e){
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Gagal")
                                    .setMessage("Request pencairan hasil penjualan gagal! Silakan coba lagi!")
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
                        String jsonError;
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            jsonError = new String(networkResponse.data);
                            Log.d(getResources().getString(R.string.debug),jsonError);
                        }else{
                            jsonError = "response nya null";
                            Log.d(getResources().getString(R.string.debug),jsonError);
                        }
                        pDialog.dismiss();
                        new AlertDialog.Builder(getContext())
                                .setTitle("Whooops . . .")
                                .setMessage("Something went wrong. Please try again! " + jsonError )
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                    }}).show().
                                getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjReq, insert_pencairan);
    }
}
