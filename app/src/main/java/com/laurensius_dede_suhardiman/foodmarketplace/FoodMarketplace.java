package com.laurensius_dede_suhardiman.foodmarketplace;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.laurensius_dede_suhardiman.foodmarketplace.fragments.FragmentAkun;
import com.laurensius_dede_suhardiman.foodmarketplace.fragments.FragmentBeranda;
import com.laurensius_dede_suhardiman.foodmarketplace.fragments.FragmentKeranjang;
import com.laurensius_dede_suhardiman.foodmarketplace.fragments.FragmentRiwayat;
import com.laurensius_dede_suhardiman.foodmarketplace.model.Product;
import com.laurensius_dede_suhardiman.foodmarketplace.model.Shop;
import com.laurensius_dede_suhardiman.foodmarketplace.model.User;

public class FoodMarketplace extends AppCompatActivity {

    private Dialog dialBox;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editorPreferences;

    public String userId;
    public String shopId;
    public static User currentUser = null;
    public static Shop currentShop = null;

    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_marketplace);

        activity = this;

        loadSharedPreferences();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.navigation_beranda:
                        selectedFragment = new FragmentBeranda();
                        break;
                    case R.id.navigation_keranjang:
                        selectedFragment = new FragmentKeranjang();
                        break;
                    case R.id.navigation_riwayat:
                        selectedFragment = new FragmentRiwayat();
                        break;
                    case R.id.navigation_notifikasi:
//                        selectedFragment = new FragmentAbout();
                        break;
                    case R.id.navigation_akun:
                        selectedFragment = new FragmentAkun();
                        break;
                }

                if(selectedFragment != null){
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fl_master, selectedFragment);
                    transaction.commit();
                }

                return true;
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_master, new FragmentBeranda());
        transaction.commit();

        dialBox = createDialogBox();
    }

    @Override
    public void onBackPressed() {
        dialBox.show();
    }



    void loadSharedPreferences(){
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedpreferences), 0);
        editorPreferences = sharedPreferences.edit();
        userId = sharedPreferences.getString(getResources().getString(R.string.sharedpreferences_user_id),null);
        shopId = sharedPreferences.getString(getResources().getString(R.string.sharedpreferences_shop_id),null);
        if(userId != null){
            FoodMarketplace.currentUser = new User(
                    sharedPreferences.getString(getResources().getString(R.string.sharedpreferences_user_id),null),
                    sharedPreferences.getString(getResources().getString(R.string.sharedpreferences_user_username),null),
                    sharedPreferences.getString(getResources().getString(R.string.sharedpreferences_user_password),null),
                    sharedPreferences.getString(getResources().getString(R.string.sharedpreferences_user_full_name),null),
                    sharedPreferences.getString(getResources().getString(R.string.sharedpreferences_user_address),null),
                    sharedPreferences.getString(getResources().getString(R.string.sharedpreferences_user_phone),null),
                    sharedPreferences.getString(getResources().getString(R.string.sharedpreferences_user_last_login),null)
            );
        }
        if(shopId != null){
            Log.d(getResources().getString(R.string.debug),"Toko ok, namanya " + sharedPreferences.getString(getResources().getString(R.string.sharedpreferences_shop_name),null));
            FoodMarketplace.currentShop = new Shop(
                    sharedPreferences.getString(getResources().getString(R.string.sharedpreferences_shop_id),null),
                    sharedPreferences.getString(getResources().getString(R.string.sharedpreferences_user_id),null),
                    sharedPreferences.getString(getResources().getString(R.string.sharedpreferences_shop_name),null),
                    sharedPreferences.getString(getResources().getString(R.string.sharedpreferences_shop_address),null),
                    FoodMarketplace.currentUser
            );
            Toast.makeText(FoodMarketplace.this,FoodMarketplace.currentShop.getId(),Toast.LENGTH_LONG).show();
        }
    }

    private Dialog createDialogBox(){
        dialBox = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.dialog_exit_title_confirm))
                .setMessage(getResources().getString(R.string.dialog_exit_message))
                .setPositiveButton(getResources().getString(R.string.dialog_ya), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.dialog_tidak), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialBox.dismiss();
                    }
                })
                .create();
        return dialBox;
    }


}
