package com.laurensius_dede_suhardiman.foodmarketplace;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.laurensius_dede_suhardiman.foodmarketplace.appcontroller.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ServiceNotification extends Service {

    SharedPreferences sharedPreferences;

    Timer timer = new Timer();

    Boolean init = true;
    String recent_str = "";

    public ServiceNotification(){}

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("UNSUPPORTED OPERATION EXCEPTION");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedpreferences), 0);
        final String userId = sharedPreferences.getString(getResources().getString(R.string.sharedpreferences_user_id),null);

        final Handler handler = new Handler();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        loadNotification(userId);
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 5000);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    public void loadNotification(String idUser){
        Random random = new Random();
        int rnd = random.nextInt(999999 - 99) + 99;
        String product_search = getResources().getString(R.string.tag_request_notification_by_user);
        String url = getResources().getString(R.string.api)
                .concat(getResources().getString(R.string.endpoint_notification_by_user))
                .concat(idUser)
                .concat(getResources().getString(R.string.slash))
                .concat(String.valueOf(rnd))
                .concat(getResources().getString(R.string.slash));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(getResources().getString(R.string.debug),"Service response Success");
                        Log.d(getResources().getString(R.string.debug),response.toString());
                        parseData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(getResources().getString(R.string.debug),"Service response Error");
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjReq, product_search );
    }


    void parseData(JSONObject responseJsonObj){
        try {
            String severity = responseJsonObj.getString(getResources().getString(R.string.json_key_severity));
            JSONObject content = responseJsonObj.getJSONObject(getResources().getString(R.string.json_key_content));
            if(severity.equals(getResources().getString(R.string.success))){
                if(content.getJSONArray("notification").length() > 0){
                    if(init == true){
                        init = false;
                    }else{
                        if(!recent_str.equals(content.getJSONArray("notification").toString())){
                            Log.d("notifikasi ","Buat notifikasi");
                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(getApplicationContext(), "notify_001");
                            Intent ii = new Intent(getApplicationContext(), FoodMarketplace.class);
                            ii.putExtra("navigasi","notifikasi");
                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, ii, 0);
                            NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
                            bigText.bigText("Notification");
                            bigText.setBigContentTitle("Notification");
                            bigText.setSummaryText(content.getJSONArray("notification").getJSONObject(0).getString("message"));
                            mBuilder.setContentIntent(pendingIntent);
                            mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
                            mBuilder.setContentTitle("Notification");
                            mBuilder.setContentText(content.getJSONArray("notification").getJSONObject(0).getString("message"));
                            mBuilder.setPriority(Notification.PRIORITY_MAX);
                            mBuilder.setStyle(bigText);
                            NotificationManager mNotificationManager =
                                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NotificationChannel channel = new NotificationChannel("notify_001",
                                        "Channel human readable title",
                                        NotificationManager.IMPORTANCE_DEFAULT);
                                mNotificationManager.createNotificationChannel(channel);
                            }
                            mNotificationManager.notify(0, mBuilder.build());
                        }
                    }
                    recent_str = content.getJSONArray("notification").toString();

                }
            }
        }catch (JSONException e){
            Log.d("Notification", e.getMessage().toString());
        }


    }

}