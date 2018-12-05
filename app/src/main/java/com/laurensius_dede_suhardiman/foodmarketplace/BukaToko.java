package com.laurensius_dede_suhardiman.foodmarketplace;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.laurensius_dede_suhardiman.foodmarketplace.appcontroller.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BukaToko extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editorPreferences;

    private EditText etShopName,etAddress;
    private Button btnRegisterShop;
    String idUser;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buka_toko);
        intent = getIntent();
        idUser = intent.getStringExtra("idUser");
        etShopName = (EditText)findViewById(R.id.et_shop_name);
        etAddress = (EditText)findViewById(R.id.et_address);
        btnRegisterShop = (Button)findViewById(R.id.btn_register_shop);
        btnRegisterShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInput();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(BukaToko.this,FoodMarketplace.class);
        startActivity(intent);
        finish();
    }

    public void validateInput(){
        if(etShopName.getText().toString().equals("") || etAddress.getText().toString().equals("")){
            new AlertDialog.Builder(BukaToko.this)
                    .setTitle("Whooops . . .")
                    .setMessage("Semua bidang harus diisi!")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }}).show().
                    getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));

        }else{
            requestRegisterShop(idUser,etShopName.getText().toString(),etAddress.getText().toString());
        }
    }

    public void requestRegisterShop(String currentIdUser,String shopName,String address){
        Random random = new Random();
        int rnd = random.nextInt(999999 - 99) + 99;
        String register_shop = getResources().getString(R.string.tag_request_shop_insert);
        String url = getResources().getString(R.string.api)
                .concat(getResources().getString(R.string.endpoint_shop_insert))
                .concat(String.valueOf(rnd))
                .concat(getResources().getString(R.string.slash));
        final ProgressDialog pDialog = new ProgressDialog(BukaToko.this);
        pDialog.setMessage(getResources().getString(R.string.progress_loading));
        pDialog.show();
        final Map<String, String> params = new HashMap<String, String>();
        params.put("id_user", currentIdUser);
        params.put("shop_name", shopName);
        params.put("address", address);
        JSONObject parameter = new JSONObject(params);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,url, parameter,
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
                        new AlertDialog.Builder(BukaToko.this)
                                .setTitle("Whooops . . .")
                                .setMessage("Something went wrong. Please try again!")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        requestRegisterShop(idUser,etShopName.getText().toString(),etAddress.getText().toString());
                                    }}).show().
                                getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjReq, register_shop);
    }

    public void parseData(JSONObject responseJsonObj){
        Log.d(getResources().getString(R.string.debug),responseJsonObj.toString());
        try{
            String severity = responseJsonObj.getString(getResources().getString(R.string.json_key_severity));
            final JSONObject content = responseJsonObj.getJSONObject(getResources().getString(R.string.json_key_content));
            if(severity.equals(getResources().getString(R.string.success))){
                JSONArray arrayUser = content.getJSONArray("user");
                final JSONObject objUser = arrayUser.getJSONObject(0);
                new AlertDialog.Builder(BukaToko.this)
                        .setTitle("Mendaftarkan toko berhasil!")
                        .setMessage(responseJsonObj.getString("message"))
                        .setIcon(android.R.drawable.ic_menu_info_details)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                try{
                                    sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedpreferences), 0);
                                    editorPreferences = sharedPreferences.edit();
                                    editorPreferences.putString(getResources().getString(R.string.sharedpreferences_user_id),objUser.getString(getResources().getString(R.string.json_key_id)));
                                    editorPreferences.putString(getResources().getString(R.string.sharedpreferences_user_username),objUser.getString(getResources().getString(R.string.json_key_username)));
                                    editorPreferences.putString(getResources().getString(R.string.sharedpreferences_user_password),objUser.getString(getResources().getString(R.string.json_key_password)));
                                    editorPreferences.putString(getResources().getString(R.string.sharedpreferences_user_full_name),objUser.getString(getResources().getString(R.string.json_key_full_name)));
                                    editorPreferences.putString(getResources().getString(R.string.sharedpreferences_user_address),objUser.getString(getResources().getString(R.string.json_key_address)));
                                    editorPreferences.putString(getResources().getString(R.string.sharedpreferences_user_phone),objUser.getString(getResources().getString(R.string.json_key_phone)));
                                    editorPreferences.putString(getResources().getString(R.string.sharedpreferences_user_last_login),objUser.getString(getResources().getString(R.string.json_key_last_login)));
                                    Intent intent = new Intent(BukaToko.this,FoodMarketplace.class);
                                    if(content.getJSONArray("shop").length() > 0){
                                        JSONArray arrayShop = content.getJSONArray("shop");
                                        JSONObject objShop = arrayShop.getJSONObject(0);
                                        editorPreferences.putString(getResources().getString(R.string.sharedpreferences_shop_id),objShop.getString(getResources().getString(R.string.json_key_id)));
                                        editorPreferences.putString(getResources().getString(R.string.sharedpreferences_shop_name),objShop.getString(getResources().getString(R.string.json_key_shop_name)));
                                        editorPreferences.putString(getResources().getString(R.string.sharedpreferences_shop_address),objShop.getString(getResources().getString(R.string.json_key_address)));
                                    }
                                    editorPreferences.commit();
                                    startActivity(intent);
                                    finish();
                                }catch (JSONException e){
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                    new AlertDialog.Builder(BukaToko.this)
                                            .setTitle("Whooops . . .")
                                            .setMessage("Terjadi kendala saat terhubung ke server! Silakan coba lagi!")
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {

                                                }}).show().
                                            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
                                }
                            }}).show().
                        getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));

            }else{
                new AlertDialog.Builder(BukaToko.this)
                        .setTitle("Mendaftarkan toko gagal")
                        .setMessage(responseJsonObj.getString("message"))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }}).show().
                        getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
            }
        }catch (JSONException e){
            new AlertDialog.Builder(BukaToko.this)
                    .setTitle("Whooops . . .")
                    .setMessage("Terjadi kendala saat terhubung ke server! Silakan coba lagi!")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }}).show().
                    getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
        }
    }
}
