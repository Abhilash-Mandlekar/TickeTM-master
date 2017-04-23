package com.varvet.barcodereadersample;

/**
 * Created by Chinmay on 23-04-2017.
 */

        import java.text.DateFormat;
        import java.util.Date;
        import java.util.HashMap;

        import android.app.Notification;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.net.Uri;
        import android.os.Bundle;
        import android.os.PowerManager;
        import android.support.v4.content.WakefulBroadcastReceiver;
        import android.util.Log;
        import android.widget.Toast;

        import static android.content.Context.MODE_PRIVATE;


public class AlarmNotificationReceiver extends BroadcastReceiver {
    private Intent mNotificationIntent;
    private PendingIntent mContentIntent;


    SharedPreferences someData;
    SharedPreferences.Editor editor;
    HashMap<String, String> map = new HashMap<String, String>();
    @Override
    public void onReceive(Context context, Intent intent) {

//        SharedPreferences prefs = context.getSharedPreferences("TICKETMSP", MODE_PRIVATE);
//        HashMap<String,String> h = (HashMap<String, String>) prefs.getAll();
        Log.e("BR","Called");
        Toast.makeText(context," BR called",Toast.LENGTH_SHORT);
//        Bundle b = intent.getExtras();
//        String key = b.getString("key");
//        Log.e("key in BR : ",key);
////        SharedPreferences.Editor editor = prefs.edit();
//        editor.remove(key);
//        editor.commit();

//        Log.e("now removed,new size:",h.size()+"");


    }
}