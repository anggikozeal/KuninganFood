package com.laurensius_dede_suhardiman.foodmarketplace.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.laurensius_dede_suhardiman.foodmarketplace.FoodMarketplace;
import com.laurensius_dede_suhardiman.foodmarketplace.R;
import com.laurensius_dede_suhardiman.foodmarketplace.RiwayatTransaksiPembelian;
import com.laurensius_dede_suhardiman.foodmarketplace.RiwayatTransaksiPenjualan;

public class FragmentRiwayat extends Fragment {

    private LinearLayout llRiwayatPembelian, llRiwayatPenjualan;

    public FragmentRiwayat() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflaterRiwayat =  inflater.inflate(R.layout.fragment_riwayat, container, false);
        llRiwayatPembelian = (LinearLayout)inflaterRiwayat.findViewById(R.id.ll_riwayat_pembelian);
        llRiwayatPenjualan = (LinearLayout)inflaterRiwayat.findViewById(R.id.ll_riwayat_penjualan);
        return inflaterRiwayat;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        llRiwayatPembelian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FoodMarketplace.currentUser != null){
                    Intent intent = new Intent(getContext(),RiwayatTransaksiPembelian.class);
                    intent.putExtra("tab","tagihan");
                    startActivity(intent);
                    FoodMarketplace.activity.finish();
                }else{
                    new AlertDialog.Builder(getContext())
                            .setTitle("Informasi")
                            .setMessage("Anda tidak dapat mengakses menu ini sebelum login!")
                            .setIcon(android.R.drawable.ic_menu_info_details)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }}).show().
                            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
                }
            }
        });
        llRiwayatPenjualan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FoodMarketplace.currentUser != null && FoodMarketplace.currentShop != null){
                    Intent intent = new Intent(getContext(),RiwayatTransaksiPenjualan.class);
                    intent.putExtra("tab","order");
                    startActivity(intent);
                    FoodMarketplace.activity.finish();
                }else{
                    new AlertDialog.Builder(getContext())
                            .setTitle("Informasi")
                            .setMessage("Anda hanya dapat mengakses halaman ini setela login atau setelah memiliki toko!")
                            .setIcon(android.R.drawable.ic_menu_info_details)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }}).show().
                            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
                }
            }
        });
    }
}
