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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

public class TambahProduk extends AppCompatActivity {

    String idShop;
//    String name;
//    String category;
//    String status;
//    String price;
//    String discount;
//    String description;
//    String rating;
//    String image;

    private ImageView ivImage;
    private EditText etName, etPrice, etDiscount, etDescription;
    private Spinner spCategory, spStatus;
    private Button btnSave;

    private Boolean is_taked = false;
    private String image;

    private static String IMAGE_DIRECTORY;
    private int GALLERY = 1, CAMERA = 2;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_produk);

        IMAGE_DIRECTORY = "/Produk_Marketplace";
        intent = getIntent();
        idShop = intent.getStringExtra("idShop");

        ivImage = (ImageView)findViewById(R.id.iv_image);
        etName = (EditText)findViewById(R.id.et_name);
        etPrice = (EditText)findViewById(R.id.et_price);
        etDiscount = (EditText)findViewById(R.id.et_discount);
        etDescription = (EditText)findViewById(R.id.et_description);

        spCategory = (Spinner)findViewById(R.id.sp_category);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter(
                TambahProduk.this,
                R.layout.support_simple_spinner_dropdown_item,getResources().getStringArray(R.array.category));
        spCategory.setAdapter(categoryAdapter);

        spStatus = (Spinner)findViewById(R.id.sp_status);
        ArrayAdapter<String> statusAdapter = new ArrayAdapter(
                TambahProduk.this,
                R.layout.support_simple_spinner_dropdown_item,getResources().getStringArray(R.array.status));
        spStatus.setAdapter(statusAdapter);

        btnSave = (Button)findViewById(R.id.btn_save);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUploadProduk();
            }
        });

        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(TambahProduk.this);
        pictureDialog.setTitle("Pilih sumber foto : ");
        String[] pictureDialogItems = {
                "Gallery",
                "Camera"};
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
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    ivImage.setAdjustViewBounds(true);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), contentURI);
                    saveImage(bitmap);
                    Toast.makeText(TambahProduk.this, "Simpan gambar berhasil", Toast.LENGTH_SHORT).show();
                    ivImage.setImageBitmap(bitmap);
                    is_taked = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(TambahProduk.this, "Simpan gambar gagal", Toast.LENGTH_SHORT).show();
                    is_taked = false;
                }
            }
        } else if (requestCode == CAMERA) {
            ivImage.setAdjustViewBounds(true);
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            ivImage.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(TambahProduk.this, "Simpan gambar berhasil", Toast.LENGTH_SHORT).show();
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
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(getApplicationContext(),
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d(getResources().getString(R.string.debug), f.getAbsolutePath());
            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }


    void validateUploadProduk(){
        if(is_taked == true &&
                !etName.getText().toString().equals("") &&
                !etPrice.getText().toString().equals("") &&
                !etDescription.getText().toString().equals("")){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BitmapDrawable drawable = (BitmapDrawable) ivImage.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            String[] ctgr =  spCategory.getSelectedItem().toString().split(" - ");
            String[] stat =  spStatus.getSelectedItem().toString().split(" - ");

            uploadProduk(idShop,
                    etName.getText().toString(),
                    ctgr[0],
                    stat[0],
                    etPrice.getText().toString(),
                    etDiscount.getText().toString(),
                    etDescription.getText().toString(),
                    "0",
                    image
            );
        }else{
            Toast.makeText(TambahProduk.this,"Semua kolom harus diisi!",Toast.LENGTH_LONG).show();
        }
    }

    void uploadProduk(String id_shop,
                      String name,
                      String category,
                      String status,
                      String price,
                      String discount,
                      String description,
                      String rating,
                      String image){
        Random random = new Random();
        int rnd = random.nextInt(999999 - 99) + 99;
        String request_upload = getResources().getString(R.string.tag_request_product_insert);
        String url = getResources().getString(R.string.api)
                .concat(getResources().getString(R.string.endpoint_product_insert))
                .concat(String.valueOf(rnd))
                .concat(getResources().getString(R.string.slash));
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        final Map<String, String> params = new HashMap<String, String>();
        params.put("id_shop", id_shop);
        params.put("name", name);
        params.put("category", category);
        params.put("status", status);
        params.put("price", price);
        params.put("discount", discount);
        params.put("description", description);
        params.put("rating",rating);
        params.put("image", image);
        JSONObject parameter = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                parameter,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(getResources().getString(R.string.debug),response.toString());
                        pDialog.dismiss();
                        try{
                            if(response.getString("severity").equals("success")){
                                new AlertDialog.Builder(TambahProduk.this)
                                        .setTitle("Informasi")
                                        .setMessage(response.getString("message"))
                                        .setIcon(android.R.drawable.ic_menu_info_details)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                dialog.dismiss();
                                                ivImage.setImageResource(R.drawable.icon_food);
                                                etName.setText("");
                                                etDiscount.setText("");
                                                etPrice.setText("");
                                                etDescription.setText("");
                                            }}).show().
                                        getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
                            }else{
                                Toast.makeText(TambahProduk.this,response.getString("message"),Toast.LENGTH_LONG).show();
                                new AlertDialog.Builder(TambahProduk.this)
                                        .setTitle("Informasi")
                                        .setMessage(response.getString("message"))
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                dialog.dismiss();
                                            }}).show().
                                        getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
                            }
                        }catch (JSONException e){
                            new AlertDialog.Builder(TambahProduk.this)
                                    .setTitle("Whooops . . .")
                                    .setMessage("Terjadi kendala saat terhubung ke server! Silakan coba lagi!")
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
                        pDialog.dismiss();
                        new AlertDialog.Builder(TambahProduk.this)
                                .setTitle("Whooops . . .")
                                .setMessage("Something went wrong. Please try again!")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        }}).show().
                                getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d9b2d"));
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest,request_upload);
    }
}
