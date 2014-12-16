package com.vssnake.gpswear.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.Wearable;
import com.vssnake.gpswear.MainPresenter;
import com.vssnake.gpswear.otto.LocationInitialize;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vssnake on 05/12/2014.
 */
public class LocationManager  implements GpsStatus.Listener, LocationListener,
        com.google.android.gms.location.LocationListener,
        SensorEventListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    public LocationManager(MainPresenter mainPresenter,boolean fusionLocation){
        this.mMainPresenter = mainPresenter;
        this.mContext = mainPresenter.getContext();
        this.mFusionGps = fusionLocation;
        mGpsDataChangeHandlers = new ArrayList<GpsDataChangeHandler>();

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API)
                .addApi(Wearable.API)  // used for data layer API
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mLocationManagerNative =
                (android.location.LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    MainPresenter mMainPresenter;
    Context mContext;
    boolean mRunning = false;

    private static final String TAG = "LocationManager";

    private boolean mFusionGps = true;
    private static final String FUSIONLOCATION = "fusion";
    private static final String NATIVELOCATION = "native";
    private static final int UPDATE_POSITION_MS = 1000;

    private String typeSensorOn = "";

    private android.location.LocationManager mLocationManagerNative;
    private LocationRequest mLocationManagerFusion;
    private SensorManager mSensorManager;
    private Sensor mSensorMagneticField;
    private Sensor mSensorAccelerometer;
    private GpsStatus mGpsStatus;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private float[] mValuesAccelerometer = new float[3];
    private float[] mValuesMagneticField = new float[3];
    private float[] mRotationMatrix = new float[9]; //Calculation from accelerometer and magnetic
    // field data
    private float[] mOrientation = new float[3];

    private List<GpsDataChangeHandler> mGpsDataChangeHandlers;


    public boolean hasGps() {
        return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }

    public void changeTypeLocation(boolean isFusion){
            mFusionGps = isFusion;
    }

    public boolean initPosition(){
        if (mFusionGps || !hasGps()){
            return initSensorFusionLocation();
        }else{
            return initNativeLocation();
        }
    }

    public boolean stopPosition(){


        boolean returnStatus = false;
        switch (typeSensorOn){
            case FUSIONLOCATION:
                returnStatus = stopSensorFusionLocation();
                break;
            case NATIVELOCATION:
                returnStatus = stopNativeLocation();
                break;
        }
        for (int i = 0; mGpsDataChangeHandlers.size() > i;i++){
            mGpsDataChangeHandlers.get(i).onLocationChange(null);
            mGpsDataChangeHandlers.get(i).onGpsStatusChange(null);
        }
        return returnStatus;
    }

    private boolean initNativeLocation(){
        typeSensorOn = NATIVELOCATION;


        mLocationManagerNative.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER,
                UPDATE_POSITION_MS,5,this);

        mLocationManagerNative.addGpsStatusListener(this);


        MainPresenter.bus.post(new LocationInitialize()); //Send to bus the initialization of GPS
       // mSensorMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
       // mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

       // mSensorManager.registerListener(this, mSensorMagneticField, SensorManager.SENSOR_DELAY_NORMAL);
        //mSensorManager.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        return true;
    }
    private boolean initSensorFusionLocation(){
        if(!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
            return false;
        }
        typeSensorOn = FUSIONLOCATION;

        mLocationManagerFusion = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_POSITION_MS)
                .setFastestInterval(UPDATE_POSITION_MS-200);

        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient,mLocationManagerFusion,this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.getStatus().isSuccess()) {
                            if (Log.isLoggable(TAG, Log.DEBUG)) {
                                Log.d(TAG, "Successfully requested location updates");
                            }

                        } else {
                            Log.e(TAG,
                                    "Failed in requesting location updates, "
                                            + "status code: "
                                            + status.getStatusCode()
                                            + ", message: "
                                            + status.getStatusMessage());
                            typeSensorOn = ""; //Problem with API
                        }

                    }
                });


        MainPresenter.bus.post(new LocationInitialize());//Send to bus the initialization of GPS
        return true;
    }

    public void sendLastLocation(){
        if (mLastLocation != null){
            onLocationChanged(mLastLocation);
        }
    }


    private boolean stopNativeLocation(){
        mLocationManagerNative.removeUpdates(this);
        mLocationManagerNative.removeGpsStatusListener(this);

        //mSensorManager.unregisterListener(this);
        typeSensorOn = "";
        return true;
    }

    private boolean stopSensorFusionLocation(){
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi
                    .removeLocationUpdates(mGoogleApiClient, this);
        }
        mGoogleApiClient.disconnect();
        typeSensorOn = "";
        return true;
    }

    public void addEventInterface(GpsDataChangeHandler event){
        mGpsDataChangeHandlers.add(event);
    }
    public void removeEventHandler(GpsDataChangeHandler event){
        mGpsDataChangeHandlers.remove(event);
    }

    @Override
    public void onGpsStatusChanged(int event) {
        mGpsStatus = mLocationManagerNative.getGpsStatus(mGpsStatus);
        for (int i = 0; mGpsDataChangeHandlers.size() > i;i++){
            mGpsDataChangeHandlers.get(i).onGpsStatusChange(mGpsStatus);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        for (int i = 0; mGpsDataChangeHandlers.size() > i;i++){
            mGpsDataChangeHandlers.get(i).onLocationChange(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()){
            case Sensor.TYPE_MAGNETIC_FIELD:
                System.arraycopy(event.values,0,mValuesMagneticField,0,event.values.length);
                break;
            case Sensor.TYPE_ACCELEROMETER:
                System.arraycopy(event.values,0,mValuesAccelerometer,0,event.values.length);
                break;

        }
        if (mValuesAccelerometer != null && mValuesMagneticField != null){
            SensorManager.getRotationMatrix(mRotationMatrix,null,mValuesAccelerometer,
                    mValuesMagneticField);
            SensorManager.getOrientation(mRotationMatrix,mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegrees = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
            for (int i = 0; mGpsDataChangeHandlers.size() > i;i++){
                mGpsDataChangeHandlers.get(i).onNorthChange(azimuthInDegrees);
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mFusionGps || !hasGps()) {
            initSensorFusionLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void setFusionGps(boolean mFusionGps) {
        this.mFusionGps = mFusionGps;
        boolean empty = typeSensorOn.isEmpty();

        stopPosition();
        if (!empty){
            initPosition();
        }

    }
    public boolean getFusionGps(){
        return mFusionGps;
    }


    public interface GpsDataChangeHandler{
        void onLocationChange(Location location);
        void onGpsStatusChange(GpsStatus gpsStatus);
        void onNorthChange(float degrees);
    }
}
