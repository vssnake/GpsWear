package com.vssnake.gpswear.fragment.presenter;

import android.app.Fragment;
import android.location.GpsStatus;
import android.location.Location;
import android.os.Environment;
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

    private File mCacheDir = null;

    HashSet<String> inProgressRequests = new HashSet<String>();

    final double mapR = (int)(StacData.MIDDLE_MAP/Math.PI/StacData.HEIGHT_BITMAP);



    MicroMapFragment mFragment;

    Timer mTimer = new Timer();



    public int pixelX = 0;
    public int pixelY = 0;


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

      /*  getMainPresenter().setMotionEvent(new MainPresenter.MapMotionEvent() {
            @Override
            public boolean onMotionEvent(MotionEvent event) {
                if(mFragment.mGestureDetector.onTouchEvent(event)){
                    return true;
                }

                if (mFragment.disableMove){
                 //   mFragment.getMapView().dispatchTouchEvent(event);
                    return true;
                }
               return false;
            }
        });*/




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

               //  if (D) Log.d(TAG,"North Pole " + degrees);
               if (lastRotation - degrees > 5
                       || 5 < degrees - lastRotation) {
                   // mFragment.getViewYouAreHere().setRotation(degrees);
               }
               lastRotation = degrees;

           }
       };

        mainActivityPresenter.addLocationHandler(mLocationHandler);


        getMainPresenter().disableMove(true);

      //  mTimer = new Timer();
      // mTimer.scheduleAtFixedRate(new PingTask(), 500, 2000);
    }
    public void pause(){
        if(D) Log.d(TAG, "onPause");


       // mainActivityPresenter.attachHandlers(null);

        mainActivityPresenter.removeLocationHandler(mLocationHandler);
        mLocationHandler = null;

        //mTimer.cancel();
    }

    public void centerMap(){
        mFragment.frameTo(pixelX, pixelY);
    }



    void onMessageLocation(Location location){
           // mFragment.getUserLocation().onLocationChanged(location,null);
        mFragment.getUserLocation().mIsLocationEnabled = true;
        mFragment.getUserLocation().onLocationChanged(location,null);
    }




    class PingTask extends TimerTask {
        public void run() {
            Log.d(TAG, "PingTask executed.");
            Location location = new Location("");
            location.setLatitude(43.3177118);
            location.setLongitude(-3.0206342);
            location.setAccuracy(50);
            onMessageLocation(location);
           // onMessageLocation(43.3177118,-3.0206342);
            //sendToPhone("ping", null, null);
        }
    }
}
