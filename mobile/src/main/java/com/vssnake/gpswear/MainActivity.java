package com.vssnake.gpswear;

import android.content.res.Resources;
import android.os.Bundle;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;




import com.google.android.gms.wearable.DataMap;
import com.mariux.teleport.lib.TeleportClient;
import com.vssnake.gpswear.custom.SpinnerImage;
import com.vssnake.gpswear.custom.SpinnerImageAdapter;
import com.vssnake.gspshared.StacData;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "Main Activity";

    private String currentMap = null;
    private TeleportClient mTeleportClient;

    Timer mTimer;

    CheckBox mCheckBox;

    TextView mTextView;

    Spinner mSpinner;
    SpinnerImageAdapter mSpinnerAdapter;

    RunnableParameter<Boolean> mCheckBoxRunnable;
    RunnableParameter<Integer> mChangeMapRunnable;

    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        // Crashlytics.start(this);

        setContentView(R.layout.activity_main);
        mCheckBox = (CheckBox)findViewById(R.id.checkBox);

        mTextView = (TextView) findViewById(R.id.main_connect_status);


        mSpinner = (Spinner) findViewById(R.id.main_type_map);

        mSpinnerAdapter = new SpinnerImageAdapter(this,
                getApplicationContext(),R.layout.custom_spinner,R.id.spinner_title,initSpinnerData());

        mSpinner.setAdapter(mSpinnerAdapter);



        mTeleportClient = new TeleportClient(this);


        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mTextView.setText(R.string.connecting);
                mTextView.setTextColor(getResources().getColor(R.color.red));

                mCheckBox.setEnabled(true);
                mCheckBox.setEnabled(false);
                mCheckBoxRunnable.setParameter(mCheckBox.isChecked());
                mHandler.post(mCheckBoxRunnable);
               // sendModeFusion(mCheckBox.isChecked());
            }
        });

        mCheckBoxRunnable = new RunnableParameter<Boolean>() {
            @Override
            public void run() {
                DataMap dataMap = new DataMap();
                dataMap.putBoolean(StacData.REQUEST_MODE_LOCATION_DATA,
                        getParameter());
                mTeleportClient.sendMessage(StacData.REQUEST_MODE_LOCATION_DATA,dataMap.toByteArray());
                mHandler.postDelayed(mCheckBoxRunnable,1000);
            }
        };


        mChangeMapRunnable = new RunnableParameter<Integer>() {
            @Override
            public void run() {
                DataMap dataMap = new DataMap();

                dataMap.putString(StacData.REQUEST_MODE_TYPEMAP_DATA,
                        mSpinnerAdapter.getItem(getParameter()).getCode());
                mTeleportClient.sendMessage(StacData.REQUEST_MODE_TYPEMAP_DATA,dataMap.toByteArray());
                mHandler.postDelayed(mChangeMapRunnable,1000);
            }
        };


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

        mSpinner.setEnabled(false);
        mCheckBox.setEnabled(false);
        mTextView.setText(R.string.connecting);
        mTextView.setTextColor(getResources().getColor(R.color.red));
        //mTimer.scheduleAtFixedRate(new PingTask(), 500, 1000);

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
                mHandler.removeCallbacks(mCheckBoxRunnable);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText(R.string.connected);
                        mTextView.setTextColor(getResources().getColor(R.color.blue));

                        mCheckBox.setEnabled(true);
                        mCheckBox.setChecked(fusionLocation);
                    }
                });
            }else if (path.equals(StacData.REQUEST_MODE_TYPEMAP_OK)){
                DataMap datamap = DataMap.fromByteArray(data);
                String codeMap = datamap.getString(StacData.REQUEST_MODE_TYPEMAP);
                final int position = mSpinnerAdapter.getPositionMapID(codeMap);
                if (position != -1){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSpinner.setSelection(position);
                            mSpinner.setEnabled(true);
                            mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                    mChangeMapRunnable.setParameter(position);

                                    mHandler.post(mChangeMapRunnable);

                                    mSpinner.setEnabled(false);

                                    /*DataMap dataMap = new DataMap();

                                    dataMap.putString(StacData.REQUEST_MODE_TYPEMAP_DATA,
                                            mSpinnerAdapter.getItem(position).getCode());
                                    mTeleportClient.sendMessage(StacData.REQUEST_MODE_TYPEMAP_DATA,dataMap.toByteArray());*/
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                    Log.d(TAG,"onSpinnerItemSelected | Nothing selected " );
                                }
                            });
                        }
                    });


                }else{
                    mSpinner.setEnabled(true);
                    mHandler.removeCallbacks(mChangeMapRunnable);
                }
            }
        }
    }

    public List<SpinnerImage> initSpinnerData(){
        Resources r = getResources();
        List<SpinnerImage> spinnerData = new ArrayList<>();
        spinnerData.add(new SpinnerImage(
                r.getString(R.string.streetMap),
                R.drawable.map_street,
                r.getString(R.string.streetMapId)));

        spinnerData.add(new SpinnerImage(
                r.getString(R.string.satellite),
                R.drawable.map_satellite,
                r.getString(R.string.satelliteMapId)));

        spinnerData.add(new SpinnerImage(
                r.getString(R.string.terrainMap),
                R.drawable.map_terrain,
                r.getString(R.string.terrainMapId)));

        spinnerData.add(new SpinnerImage(
                r.getString(R.string.outdoorsMap),
                R.drawable.map_outdoors,
                r.getString(R.string.outdoorsMapId)));

        spinnerData.add(new SpinnerImage(
                r.getString(R.string.woodcutMap),
                R.drawable.map_woodcut,
                r.getString(R.string.woodcutMapId)));

        spinnerData.add(new SpinnerImage(
                r.getString(R.string.pencilMap),
                R.drawable.map_pencil,
                r.getString(R.string.pencilMapId)));

        spinnerData.add(new SpinnerImage(
                r.getString(R.string.spaceShipMap),
                R.drawable.map_spaceship,
                r.getString(R.string.spaceShipMapId)));

        return spinnerData;
    }

    //mTimer = new Timer();
    //mTimer.scheduleAtFixedRate(new PingTask(), 500, 1000);
    class PingTask extends TimerTask {
        public void run() {
            mTeleportClient.sendMessage(StacData.REQUEST_MODE_LOCATION,null);
            mTeleportClient.sendMessage(StacData.REQUEST_MODE_TYPEMAP,null);
        }
    }


    abstract class RunnableParameter<T> implements Runnable {
        T parameter;
        public T getParameter(){ return parameter;}
        public void setParameter(T parameter){ this.parameter = parameter;}
        //OneShotTask(String s) { str = s; }
        /*public void run() {
            someFunc(str);
        }*/
    }

}



