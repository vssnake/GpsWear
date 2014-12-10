package com.mapbox.mapbox.sdk.shared.network;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by vssnake on 03/12/2014.
 */
public class BlackHoleSync implements MessageApi.MessageListener,NodeApi.NodeListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    MonitorObject mMonitor;
    String mUrl;


    private static final String TAG = "BlackHoleSync";

    private GoogleApiClient mGoogleApiClient;

    public BlackHoleSync(Context context){
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        mMonitor = new MonitorObject();
    }

    private String mPath;
    private byte[] mResult;

    public byte[] getData(String path,String url,byte[] data){
        this.mUrl = url;
        Collection<String> nodes = getNodes();
        mPath = path;
        for (String node : nodes) {
            Wearable.MessageApi.sendMessage(
                    mGoogleApiClient, node, path + " / " +url, data).setResultCallback(

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

        mMonitor.doWait(10000);
        disconnect();
        return mResult;
    }

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();

        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }

        return results;
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
       if (messageEvent.getPath().contains(mPath)){
           if (messageEvent.getPath().contains(mUrl)){
               mResult = messageEvent.getData();
               mMonitor.doNotify();
           }

        }
    }

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

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onPeerConnected(Node node) {

    }

    @Override
    public void onPeerDisconnected(Node node) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
