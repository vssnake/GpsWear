package com.vssnake.gpswear.fragment.presenter;

import android.app.Fragment;
import android.location.GpsStatus;
import android.location.Location;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.vssnake.gpswear.MainPresenter;
import com.vssnake.gpswear.fragment.view.MicroMapFragment;

import java.io.File;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import com.vssnake.gpswear.utils.LocationManager;
import com.vssnake.gspshared.StacData;

/**
 * Created by vssnake on 11/11/2014.
 */
public class MicroMapPresenter extends BasicPresenter{

    public static final boolean D = true;
    public static final String TAG ="MicroMapPresenter";

    volatile File mCacheDir = null;

    HashSet<String> inProgressRequests = new HashSet<String>();

    final double mapR = (int)(StacData.MIDDLE_MAP/Math.PI/StacData.HEIGHT_BITMAP);

    MicroMapFragment mFragment;

    Timer mTimer = new Timer();


    public MicroMapPresenter(MainPresenter mainPresenter) {
        super(mainPresenter);
    }


    public void mapDisabled (boolean disabled){
        getMainPresenter().disableMove(true);
    }




    @Override
    public void attach(Fragment fragment) {
           mCacheDir = new File(Environment.getExternalStorageDirectory() + "/wearmaps");
        if (!mCacheDir.exists()){
            if(!mCacheDir.mkdir()) Log.e(TAG, "Failed to create external storage directory");
        }

        mFragment = (MicroMapFragment)fragment;
    }

    public void detach(){

    }
    float lastRotation;

    LocationManager.GpsDataChangeHandler mLocationHandler;

    public void resume(){
        if(D) Log.d(TAG, "onResume");

       // mFragment.getUserLocation().enableMyLocation();

       mLocationHandler = new LocationManager.GpsDataChangeHandler() {

           @Override
           public void onLocationChange(Location location) {
               if (location != null) {
                   onMessageLocation(location);
               }
           }

           @Override
           public void onGpsStatusChange(GpsStatus gpsStatus) {
           }

           @Override
           public void onNorthChange(float degrees) {

               /*  if (D) Log.d(TAG,"North Pole " + degrees);
               if (lastRotation - degrees > 5
                       || 5 < degrees - lastRotation) {
                    mFragment.getViewYouAreHere().setRotation(degrees);
               }*/
               lastRotation = degrees;

           }
       };

        mainActivityPresenter.addLocationHandler(mLocationHandler);


        getMainPresenter().disableMove(true);

        getMainPresenter().sendLastLocation();

       //mTimer = new Timer();
       //mTimer.scheduleAtFixedRate(new PingTask(), 500, 1000);
    }
    public void pause(){
        if(D) Log.d(TAG, "onPause");


       // mainActivityPresenter.attachHandlers(null);

        mainActivityPresenter.removeLocationHandler(mLocationHandler);
        mLocationHandler = null;

        //mTimer.cancel();
    }



    public void onReturnButtonClicked(){
        mFragment.changeVisibilityMenu();
        getMainPresenter().disableMove(true);
    }
    public void onGoToPositionClicked(){
        getMainPresenter().disableMove(true);
    }
    public void onMotionTrackingClicked(){

    }

    void onMessageLocation(final Location location){
           // mFragment.getUserLocation().onLocationChanged(location,null);
        Handler mainHandler = new Handler(mainActivityPresenter.getContext().getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                mFragment.getUserLocation().mIsLocationEnabled = true;
                mFragment.getUserLocation().onLocationChanged(location,null);

            }
        }; // This is your code
        mainHandler.post(myRunnable);

    }


    double latitude = 43.3177118;
    double longitude = -3.0206342;

    class PingTask extends TimerTask {
        public void run() {
            Log.d(TAG, "PingTask executed.");
            Location location = new Location("");
            location.setLatitude(latitude +=0.0001);
            location.setLongitude(longitude+=0.0001);
            location.setAccuracy(50);
            onMessageLocation(location);
           // onMessageLocation(43.3177118,-3.0206342);
            //sendToPhone("ping", null, null);
        }
    }
}
