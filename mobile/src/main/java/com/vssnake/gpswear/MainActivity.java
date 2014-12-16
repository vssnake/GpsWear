package com.vssnake.gpswear;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;
import com.mariux.teleport.lib.TeleportClient;
import com.vssnake.gspshared.StacData;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "Main Activity";

    private String currentMap = null;
    private TeleportClient mTeleportClient;

    Timer mTimer;

    CheckBox mCheckBox;

    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        // Crashlytics.start(this);

        setContentView(R.layout.activity_main);
        mCheckBox = (CheckBox)findViewById(R.id.checkBox);

        mTextView = (TextView) findViewById(R.id.main_connect_status);

        mTeleportClient = new TeleportClient(this);


        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mTextView.setText(R.string.connecting);
                mTextView.setTextColor(getResources().getColor(R.color.red));

                mCheckBox.setEnabled(true);
                mCheckBox.setEnabled(false);
                sendModeFusion(mCheckBox.isChecked());


            }
        });


    }

    public void sendModeFusion(Boolean modeFusion){
        DataMap dataMap = new DataMap();
        dataMap.putBoolean(StacData.REQUEST_MODE_LOCATION_DATA,
                modeFusion);

        mTeleportClient.sendMessage(StacData.REQUEST_MODE_LOCATION_DATA,dataMap.toByteArray());
    }


    @Override
    protected void onStart() {
        super.onStart();
        mTeleportClient.connect();
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new PingTask(), 500, 1000);

        mTeleportClient.setOnGetMessageCallback(new onMessage());
        mTeleportClient.addConnectionCallbacks(new TeleportClient.OnConnectionCallback() {
            @Override
            public void onConnected(Bundle bundle) {
                mTimer.scheduleAtFixedRate(new PingTask(), 500, 1000);
               // mTeleportClient.sendMessage(StacData.REQUEST_MODE_LOCATION,null);

            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        });




    }

    @Override
    protected void onStop() {
        super.onStop();
        mTeleportClient.disconnect();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    public class onMessage extends TeleportClient.OnGetMessageCallback{

        @Override
        public void onCallback(String path, byte[] data) {
            if (path.equals(StacData.REQUEST_MODE_LOCATION_DATA)){
                mTimer.cancel();
                DataMap dataMap = DataMap.fromByteArray(data);
                final boolean fusionLocation = dataMap.getBoolean(StacData.REQUEST_MODE_LOCATION_DATA);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mTextView.setText(R.string.connected);
                        mTextView.setTextColor(getResources().getColor(R.color.blue));

                        mCheckBox.setEnabled(true);
                        mCheckBox.setChecked(fusionLocation);
                    }
                });

            }else if (path.equals(StacData.REQUEST_MODE_LOCATION_DATA_OK)){
                DataMap dataMap = DataMap.fromByteArray(data);
                final boolean fusionLocation = dataMap.getBoolean(StacData.REQUEST_MODE_LOCATION_DATA);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText(R.string.connected);
                        mTextView.setTextColor(getResources().getColor(R.color.blue));

                        mCheckBox.setEnabled(true);
                        mCheckBox.setChecked(fusionLocation);
                    }
                });
            }
        }
    }

    //mTimer = new Timer();
    //mTimer.scheduleAtFixedRate(new PingTask(), 500, 1000);
    class PingTask extends TimerTask {
        public void run() {
            mTeleportClient.sendMessage(StacData.REQUEST_MODE_LOCATION,null);
        }
    }

}



