package com.vssnake.gpswear;

import android.util.Log;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.mapbox.mapbox.sdk.shared.constants.BlackHoleConstants;
import com.mapbox.mapboxsdk.server.util.BlackHoleServiceSync;
import com.mapbox.mapboxsdk.server.util.NetworkUtils;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by vssnake on 03/12/2014.
 */
public class BlackHoleService extends BlackHoleServiceSync {


    public static final String TAG = "BlackHoleService";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {


    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        {

            Log.d(TAG,"onMessageReceived : " + messageEvent.getPath());
            if (messageEvent.getPath().contains(BlackHoleConstants.REQUEST_DATA_HTTP)){

                byte[] data = messageEvent.getData();
                try {
                    String url = new String(data,"UTF-8");
                    HttpURLConnection connection = NetworkUtils.getHttpURLConnection(new URL(url));
                    InputStream is = connection.getInputStream();
                   // ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    byte[] buffer = new byte[16024];
                    int read;
                    while ((read = is.read(buffer, 0, buffer.length)) != -1) {
                        baos.write(buffer, 0, read);
                    }
                    baos.flush();



                    Log.d(TAG,"onMessageReceived : Send Message " + baos.size() +" ");






                    sendMessage(messageEvent.getPath(),baos.toByteArray());



                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (messageEvent.getPath().equals(BlackHoleConstants.IS_NETWORK_ON)){
                sendMessage(messageEvent.getPath(),new byte[]{1});

        }

        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }
}
