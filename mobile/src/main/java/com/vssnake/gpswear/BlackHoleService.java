package com.vssnake.gpswear;

import android.util.Log;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.mapbox.mapbox.sdk.shared.constants.BlackHoleConstants;
import com.mapbox.mapboxsdk.server.util.BlackHoleServiceSync;
import com.mapbox.mapboxsdk.server.util.NetworkUtils;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    public void onMessageReceived(final MessageEvent messageEvent) {
        {


            if (messageEvent.getPath().contains(BlackHoleConstants.REQUEST_DATA_HTTP)){
               /* if (!mData.contains(messageEvent.getPath())){
                    mData.add(messageEvent.getPath());
                    Log.d(TAG,"onMessageReceived : Message already send  |" + messageEvent.getPath());
                }else{
                    Log.d(TAG,"onMessageReceived : Message already send | " + messageEvent.getPath());
                    sendMessage(messageEvent.getPath(),null);
                    return;
                }*/
                        byte[] data = messageEvent.getData();
                        Log.d(TAG,"onMessageReceived : " + messageEvent.getPath());
                        String url = null;
                        try {
                            url = new String(data,"UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        Request request = new Request.Builder()
                                .url(url)
                                .build();

                        NetworkUtils.getOkHttp().newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                                sendMessage(messageEvent.getPath(),null);
                            }

                            @Override
                            public void onResponse(Response response) throws IOException {
                                try{
                                    sendMessage(messageEvent.getPath(),response.body().bytes());
                                }catch(IOException e){
                                    sendMessage(messageEvent.getPath(),null);
                                }

                            }
                        });
                        try {
                            Response response = NetworkUtils.getOkHttp().newCall(request).execute();

                            sendMessage(messageEvent.getPath(),response.body().bytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        /*try {

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




                            mData.remove(messageEvent.getPath());

                            sendMessage(messageEvent.getPath(),baos.toByteArray());



                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/



            }else if (messageEvent.getPath().equals(BlackHoleConstants.IS_NETWORK_ON)){
                sendMessage(messageEvent.getPath(),new byte[]{1});

        }

        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }
}
