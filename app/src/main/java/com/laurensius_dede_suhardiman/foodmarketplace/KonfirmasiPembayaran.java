package com.laurensius_dede_suhardiman.foodmarketplace;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.laurensius_dede_suhardiman.foodmarketplace.appcontroller.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class KonfirmasiPembayaran extends AppCompatActivity {

    private ImageView ivBuktiTransaksi;
    private Button btnUnggahBukti;

    private Boolean is_taked = false;
    private String image;

    private static String IMAGE_DIRECTORY;
    private int GALLERY = 1, CAMERA = 2;

    private Intent intent;
    private String idTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirmasi_pembayaran);

        intent = getIntent();
        idTransaction = intent.getStringExtra("idTransaction");


        IMAGE_DIRECTORY = getResources().getString(R.string.image_dir);

        ivBuktiTransaksi = (ImageView)findViewById(R.id.iv_bukti_transaksi);
        btnUnggahBukti = (Button)findViewById(R.id.btn_unggah_bukti);

        ivBuktiTransaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        btnUnggahBukti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_taked==true){
                    new AlertDialog.Builder(KonfirmasiPembayaran.this)
                            .setTitle("Konfirmasi")
                            .setMessage("Apakah Anda akan mengirim bukti transaksi?")
                            .setIcon(android.R.drawable.ic_menu_info_details)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    BitmapDrawable drawable = (BitmapDrawable) ivBuktiTransaksi.getDrawable();
                                    Bitmap bitmap = drawable.getBitmap();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] imageBytes = baos.toByteArray();
                                    image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                                    requestUploadBukti(idTransaction,image);
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                }else{
                    new AlertDialog.Builder(KonfirmasiPembayaran.this)
                            .setTitle("Whoops . . .")
                            .setMessage("Silakan pilih berkas transaksi")
                            .setIcon(android.R.drawable.ic_menu_info_details)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }}).show();

                }
            }
        });
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(KonfirmasiPembayaran.this);
        pictureDialog.setTitle(getResources().getString(R.string.image_selector_title));
        String[] pictureDialogItems = {
                getResources().getString(R.string.image_selector_gallery),
                getResources().getString(R.string.image_selector_camera) };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), contentURI);
                    saveImage(bitmap);
                    Toast.makeText(KonfirmasiPembayaran.this, getResources().getString(R.string.toast_save_image_success), Toast.LENGTH_SHORT).show();
                    ivBuktiTransaksi.setImageBitmap(bitmap);
                    is_taked = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(KonfirmasiPembayaran.this, getResources().getString(R.string.toast_save_image_failed), Toast.LENGTH_SHORT).show();
                    is_taked = false;
                }
            }
        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get(getResources().getString(R.string.image_camera_data));
            ivBuktiTransaksi.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(KonfirmasiPembayaran.this, getResources().getString(R.string.toast_save_image_success), Toast.LENGTH_SHORT).show();
            is_taked = true;
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }
        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + getResources().getString(R.string.image_ext_jpg));
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(KonfirmasiPembayaran.this,
                    new String[]{f.getPath()},
                    new String[]{getResources().getString(R.string.image_mime_jpg)}, null);
            fo.close();
            Log.d(getResources().getString(R.string.debug), f.getAbsolutePath());
            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    public void requestUploadBukti(String idTransaction,String image){
        Random random = new Random();
        int rnd = random.nextInt(999999 - 99) + 99;
        String upload_bukti = getResources().getString(R.string.tag_request_transac_upload_bukti);
        String url = getResources().getString(R.string.api)
                .concat(getResources().getString(R.string.endpoint_transac_upload_bukti))
                .concat(String.valueOf(rnd))
                .concat(getResources().getString(R.string.slash));
        final ProgressDialog pDialog = new ProgressDialog(KonfirmasiPembayaran.this);
        pDialog.setMessage(getResources().getString(R.string.progress_loading));
        pDialog.show();
        final Map<String, String> params = new HashMap<String, String>();
        params.put("id", idTransaction);
        params.put("image", image);
        params.put("status","ON_KONFIRMASI");
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
                        new AlertDialog.Builder(KonfirmasiPembayaran.this)
                                .setTitle("Whooops . . .")
                                .setMessage("Upload bukti transaksi gagal!")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                    }}).show().
                                getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjReq, upload_bukti );
    }

    public void parseData(JSONObject responseJsonObj){
        Log.d(getResources().getString(R.string.debug),responseJsonObj.toString());
        try{
            String severity = responseJsonObj.getString(getResources().getString(R.string.json_key_severity));
            final JSONObject content = responseJsonObj.getJSONObject(getResources().getString(R.string.json_key_content));
            if(severity.equals(getResources().getString(R.string.success))){
                new AlertDialog.Builder(KonfirmasiPembayaran.this)
                        .setTitle("Upload bukti transaksi berhasil")
                        .setMessage(responseJsonObj.getString("message"))
                        .setIcon(android.R.drawable.ic_menu_info_details)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                Intent intent = new Intent(KonfirmasiPembayaran.this,FoodMarketplace.class);
                                startActivity(intent);
                                finish();
                            }}).show().
                        getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));

            }else{
                new AlertDialog.Builder(KonfirmasiPembayaran.this)
                        .setTitle("Upload bukti transaksi gagal")
                        .setMessage(responseJsonObj.getString("message"))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }}).show().
                        getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
            }
        }catch (JSONException e){
            new AlertDialog.Builder(KonfirmasiPembayaran.this)
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
