package com.laurensius_dede_suhardiman.foodmarketplace.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.laurensius_dede_suhardiman.foodmarketplace.BukaToko;
import com.laurensius_dede_suhardiman.foodmarketplace.FoodMarketplace;
import com.laurensius_dede_suhardiman.foodmarketplace.Login;
import com.laurensius_dede_suhardiman.foodmarketplace.R;
import com.laurensius_dede_suhardiman.foodmarketplace.Register;
import com.laurensius_dede_suhardiman.foodmarketplace.ServiceNotification;
import com.laurensius_dede_suhardiman.foodmarketplace.ShopProduct;
import com.laurensius_dede_suhardiman.foodmarketplace.AddProduct;

public class FragmentAkun extends Fragment {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editorPreferences;

    private LinearLayout llNoSession, llSession;
    private LinearLayout llMenuKelolaAkun, llMenuLogout;
    private LinearLayout llMenuKelolaToko, llMenuTambahDagangan, llMenuKelolaDagangan ;

    private Button btnLogin,btnRegister;

    private TextView tvFullName, tvUsername;

    public FragmentAkun() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflaterAkun =  inflater.inflate(R.layout.fragment_akun, container, false);

        tvFullName = (TextView)inflaterAkun.findViewById(R.id.tv_full_name);
        tvUsername= (TextView)inflaterAkun.findViewById(R.id.tv_username);

        llNoSession = (LinearLayout)inflaterAkun.findViewById(R.id.ll_no_session);
        btnRegister = (Button)inflaterAkun.findViewById(R.id.btn_register);
        btnLogin = (Button)inflaterAkun.findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Login.class);
                startActivity(intent);
                FoodMarketplace.activity.finish();
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Register.class);
                startActivity(intent);
                FoodMarketplace.activity.finish();
            }
        });

        llSession = (LinearLayout)inflaterAkun.findViewById(R.id.ll_session);
        tvFullName = (TextView)inflaterAkun.findViewById(R.id.tv_full_name);
        tvUsername = (TextView)inflaterAkun.findViewById(R.id.tv_username);
        llMenuKelolaAkun = (LinearLayout)inflaterAkun.findViewById(R.id.ll_menu_kelola_akun);
        llMenuLogout = (LinearLayout)inflaterAkun.findViewById(R.id.ll_menu_logout);

        llMenuKelolaToko = (LinearLayout)inflaterAkun.findViewById(R.id.ll_menu_kelola_toko);
        llMenuTambahDagangan =  (LinearLayout)inflaterAkun.findViewById(R.id.ll_menu_tambah_dagangan);
        llMenuKelolaDagangan =  (LinearLayout)inflaterAkun.findViewById(R.id.ll_menu_kelola_dagangan);

        return inflaterAkun;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        if(FoodMarketplace.currentUser != null){
            llNoSession.setVisibility(View.GONE);
            llSession.setVisibility(View.VISIBLE);
            tvFullName.setText(FoodMarketplace.currentUser.getFullName());
            tvUsername.setText(FoodMarketplace.currentUser.getUsername());
        }else{
            llNoSession.setVisibility(View.VISIBLE);
            llSession.setVisibility(View.GONE);
        }

        llMenuKelolaAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        llMenuLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            new AlertDialog.Builder(getContext())
                .setTitle("Konfirmasi")
                .setMessage("Apakah anda akan keluar dari sesi pada aplikasi?")
                .setIcon(android.R.drawable.ic_menu_info_details)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.sharedpreferences), 0);
                        editorPreferences = sharedPreferences.edit();
                        editorPreferences.clear();
                        editorPreferences.commit();
                        FoodMarketplace.currentUser = null;
                        FoodMarketplace.currentShop = null;
                        FoodMarketplace.activity.stopService(new Intent(getContext(), ServiceNotification.class));
                        Intent intent = new Intent(getContext(),FoodMarketplace.class);
                        startActivity(intent);
                        FoodMarketplace.activity.finish();
                    }}).show().
                getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
            }
        });

        //
        llMenuKelolaToko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FoodMarketplace.currentShop != null){
                    new AlertDialog.Builder(getContext())
                            .setTitle("Peringatan")
                            .setMessage("Anda sudah memiliki toko, silakan unggah produk Anda!")
                            .setIcon(android.R.drawable.ic_menu_info_details)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }}).show().
                            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
                }else{
                    new AlertDialog.Builder(getContext())
                            .setTitle("Konfirmasi")
                            .setMessage("Buka toko untuk mulai berjualan?")
                            .setIcon(android.R.drawable.ic_menu_info_details)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(getContext(),BukaToko.class);
                                    intent.putExtra("idUser",FoodMarketplace.currentUser.getId());
                                    startActivity(intent);
                                    FoodMarketplace.activity.finish();
                                }}).show().
                            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
                }
            }
        });

        //
        llMenuTambahDagangan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FoodMarketplace.currentShop != null){
                    Intent intent = new Intent(getContext(),AddProduct.class);
                    intent.putExtra("idShop",FoodMarketplace.currentShop.getId());
                    startActivity(intent);
                }else{
                    new AlertDialog.Builder(getContext())
                            .setTitle("Konfirmasi")
                            .setMessage("Buka toko untuk mulai berjualan?")
                            .setIcon(android.R.drawable.ic_menu_info_details)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();

                                }}).show().
                            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
                }
            }
        });

        //
        llMenuKelolaDagangan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FoodMarketplace.currentShop != null){
                    Intent intent = new Intent(getContext(),ShopProduct.class);
                    intent.putExtra("idShop",FoodMarketplace.currentShop.getId());
                    startActivity(intent);
                }else{
                    new AlertDialog.Builder(getContext())
                            .setTitle("Konfirmasi")
                            .setMessage("Buka toko untuk mulai berjualan?")
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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
