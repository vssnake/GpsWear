package com.mapbox.mapboxsdk.server.util;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.mapbox.mapbox.sdk.shared.constants.BlackHoleConstants;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created by vssnake on 03/12/2014.
 */
public abstract class  BlackHoleServiceSync extends WearableListenerService implements  GoogleApiClient.ConnectionCallbacks{

    private static final String TAG = "BlackHoleService";

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();
        mGoogleApiClient.connect();
    }





    @Override
    public abstract void onDataChanged(DataEventBuffer dataEvents);

    public void syncData(final String path,final int data){
        new Thread(new Runnable() {
            @Override
            public void run() {
                PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(path);
                putDataMapRequest.getDataMap().putInt("test", data);

                PutDataRequest request = putDataMapRequest.asPutDataRequest();
                PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
                        .putDataItem(mGoogleApiClient,request);
                DataApi.DataItemResult result = pendingResult.await();

                if (result.getStatus().isSuccess()){
                    Log.d(TAG, "Data item set: " + result.getDataItem().getUri());
                }
            }
        }).start();

    }


    public abstract void onMessageReceived(MessageEvent messageEvent);


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        Wearable.NodeApi.addListener(mGoogleApiClient, this);

    }

    public void disconnect() {
        Log.d(TAG, "disconnect");
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
        Wearable.NodeApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();

    }

    public void sendMessage(String path,byte[] data){
        Collection<String> nodes = getNodes();
        for (String node : nodes) {
            Wearable.MessageApi.sendMessage(
                    mGoogleApiClient, node, path, data).setResultCallback(

                    new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            if (!sendMessageResult.getStatus().isSuccess()) {
                                Log.e(TAG, "Failed to send message with status code: "
                                        + sendMessageResult.getStatus().getStatusCode());
                            }
                        }
                    }
            );
        }
    }

    public Collection<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();

        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }

        return results;
    }
}
