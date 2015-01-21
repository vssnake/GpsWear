package com.vssnake.gpswear;

import android.content.Context;
import android.content.SharedPreferences;

import com.squareup.otto.Bus;
import com.vssnake.gpswear.utils.LocationManager;
import com.vssnake.gspshared.StacData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vssnake on 10/11/2014.
 */
public class MainPresenter{

    private Context mContext;
    private MainActivity mMainActivity;
    LocationManager mLocationManager;

    SharedPreferences settings;

    public static Bus bus = new Bus();

    private List<FragmentShowEvent> mFragmentShowEvent = new ArrayList<FragmentShowEvent>();



    public MainPresenter(Context context){
        // Restore preferences
        settings = context.getSharedPreferences(StacData.PREFS_NAME, 0);


        mContext = context;
    }

    public void attachMainActivity (final MainActivity mainActivity){
        boolean fusionLocation = settings.getBoolean(StacData.REQUEST_MODE_LOCATION, true);
        mLocationManager = new LocationManager(this,fusionLocation);
        this.mMainActivity = mainActivity;


        WearableCommunicator.locationHandler = new WearableCommunicator.LocationInterface() {
            @Override
            public void onModeLocationChange(final Boolean locationFusion) {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLocationManager.setFusionGps(locationFusion);
                    }
                });

            }

            @Override
            public void onTypeMapChange(String typeMap) {
                mainActivity.changeMapType(typeMap);
            }
        };

    }

    public void dettach(){
        WearableCommunicator.locationHandler = null;
    }

    public Context getContext() {
        return mContext;
    }

    public void initLocation(){
        mLocationManager.initPosition();
    }

    public void stopLocation(){
        mLocationManager.stopPosition();
    }

    public boolean hasGps() {
        return mLocationManager.hasGps();
    }

    public boolean hasFusionLocationEnabled(){ return mLocationManager.getFusionGps();}

    public void addLocationHandler(LocationManager.GpsDataChangeHandler handler){
            mLocationManager.addEventInterface(handler);
    }
    public void removeLocationHandler(LocationManager.GpsDataChangeHandler handler){
        mLocationManager.removeEventHandler(handler);
    }
    public boolean getTypeLocation(){
        return mLocationManager.getFusionGps();
    }

    public void disableMove (boolean disableMove){
        mMainActivity.getGridViewPager().intercept = disableMove;

    }

    public void changePreviousPage(){
        mMainActivity.changePreviousPage();
    }


    public void changeFragment(String nameFragment){
        for (FragmentShowEvent f : mFragmentShowEvent){
            f.monFragmentShow(nameFragment);
        }
    }
    public void addChangeFragmentEvent(FragmentShowEvent fragment){
        mFragmentShowEvent.add(fragment);
    }


    //endregion

    //region InterfaceHandlers


    public void sendLastLocation(){
        mLocationManager.sendLastLocation();
    }

    public interface FragmentShowEvent{
        void monFragmentShow(String nameFragment);
    }


    //endregion
}
