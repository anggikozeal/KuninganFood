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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.laurensius_dede_suhardiman.foodmarketplace.FoodMarketplace;
import com.laurensius_dede_suhardiman.foodmarketplace.R;
import com.laurensius_dede_suhardiman.foodmarketplace.RiwayatTransaksiPenjualan;
import com.laurensius_dede_suhardiman.foodmarketplace.adapter.NotifikasiAdapter;
import com.laurensius_dede_suhardiman.foodmarketplace.adapter.NotifikasiAdapter;
import com.laurensius_dede_suhardiman.foodmarketplace.adapter.NotifikasiAdapter;
import com.laurensius_dede_suhardiman.foodmarketplace.appcontroller.AppController;
import com.laurensius_dede_suhardiman.foodmarketplace.model.Notifikasi;
import com.laurensius_dede_suhardiman.foodmarketplace.model.Product;
import com.laurensius_dede_suhardiman.foodmarketplace.model.Shop;
import com.laurensius_dede_suhardiman.foodmarketplace.model.Transaction;
import com.laurensius_dede_suhardiman.foodmarketplace.model.Notifikasi;
import com.laurensius_dede_suhardiman.foodmarketplace.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FragmentNotifikasi extends Fragment {

    private LinearLayout llFailed,llSuccess,llNoData;

    RecyclerView.LayoutManager mLayoutManager;
    List<Notifikasi> listNotifikasi = new ArrayList<>();
    private RecyclerView rvNotifikasi;
    private NotifikasiAdapter notifikasiAdapter = null;

    public FragmentNotifikasi() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflaterNotifikasi = inflater.inflate(R.layout.fragment_notifikasi, container, false);
        llFailed = (LinearLayout)inflaterNotifikasi.findViewById(R.id.ll_failed);
        llNoData = (LinearLayout)inflaterNotifikasi.findViewById(R.id.ll_no_data);
        llSuccess = (LinearLayout)inflaterNotifikasi.findViewById(R.id.ll_success);
        rvNotifikasi = (RecyclerView)inflaterNotifikasi.findViewById(R.id.rv_notifikasi);
        return inflaterNotifikasi;
        
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        llNoData.setVisibility(View.GONE);
        llFailed.setVisibility(View.GONE);
        llSuccess.setVisibility(View.GONE);

        if(FoodMarketplace.currentUser != null){
            rvNotifikasi.setAdapter(null);
            rvNotifikasi.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getContext());
            rvNotifikasi.setLayoutManager(mLayoutManager);
            notifikasiAdapter = new NotifikasiAdapter(listNotifikasi,getContext());
            notifikasiAdapter.notifyDataSetChanged();
            rvNotifikasi.setAdapter(notifikasiAdapter);

            requestNotifikasi();
        }else{
            new AlertDialog.Builder(getContext())
                    .setTitle("Informasi")
                    .setMessage("Anda hanya dapat mengakses halaman ini setelah login")
                    .setIcon(android.R.drawable.ic_menu_info_details)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }}).show().
                    getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));

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

    public void requestNotifikasi(){
        Random random = new Random();
        int rnd = random.nextInt(999999 - 99) + 99;
        String transac_keranjang = getResources().getString(R.string.tag_request_notification_by_user);
        String url = getResources().getString(R.string.api)
                .concat(getResources().getString(R.string.endpoint_notification_by_user))
                .concat(FoodMarketplace.currentUser.getId()) //val
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
                JSONArray jsonArrayNotification = content.getJSONArray("notification");
                if(jsonArrayNotification.length() > 0){
                    for(int x=0;x<jsonArrayNotification.length();x++){
                        JSONObject objNotification = jsonArrayNotification.getJSONObject(x);
                        listNotifikasi.add(new Notifikasi(
                                objNotification.getString("id"),
                                objNotification.getString("id_user"),
                                objNotification.getString("title"),
                                objNotification.getString("message"),
                                objNotification.getString("datetime")
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
        notifikasiAdapter.notifyDataSetChanged();
    }

}
