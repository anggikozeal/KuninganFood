package com.laurensius_dede_suhardiman.foodmarketplace.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.laurensius_dede_suhardiman.foodmarketplace.R;
import com.laurensius_dede_suhardiman.foodmarketplace.RiwayatTransaksiPembelian;

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
                Intent intent = new Intent(getContext(),RiwayatTransaksiPembelian.class);
                startActivity(intent);
            }
        });
    }
}
