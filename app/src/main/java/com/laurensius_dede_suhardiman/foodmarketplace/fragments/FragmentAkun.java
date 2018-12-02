package com.laurensius_dede_suhardiman.foodmarketplace.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.vision.text.Line;
import com.laurensius_dede_suhardiman.foodmarketplace.FoodMarketplace;
import com.laurensius_dede_suhardiman.foodmarketplace.R;

public class FragmentAkun extends Fragment {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editorPreferences;

    private LinearLayout llNoSession, llSession;
    private LinearLayout llMenuKelolaAkun, llMenuLogout;
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
        llNoSession = (LinearLayout)inflaterAkun.findViewById(R.id.ll_no_session);
        btnRegister = (Button)inflaterAkun.findViewById(R.id.btn_register);
        btnLogin = (Button)inflaterAkun.findViewById(R.id.btn_login);

        llSession = (LinearLayout)inflaterAkun.findViewById(R.id.ll_session);
        tvFullName = (TextView)inflaterAkun.findViewById(R.id.tv_full_name);
        tvUsername = (TextView)inflaterAkun.findViewById(R.id.tv_username);
        llMenuKelolaAkun = (LinearLayout)inflaterAkun.findViewById(R.id.ll_menu_kelola_akun);
        llMenuLogout = (LinearLayout)inflaterAkun.findViewById(R.id.ll_menu_logout);
        return inflaterAkun;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        if(FoodMarketplace.currentUser != null){
            llNoSession.setVisibility(View.GONE);
            llSession.setVisibility(View.VISIBLE);
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
                sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.sharedpreferences), 0);
                editorPreferences = sharedPreferences.edit();
                editorPreferences.clear();
                editorPreferences.commit();
                FoodMarketplace.currentUser = null;
                Intent intent = new Intent(getContext(),FoodMarketplace.class);
                startActivity(intent);
                FoodMarketplace.activity.finish();
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