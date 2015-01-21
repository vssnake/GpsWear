package com.vssnake.gpswear;

import android.content.SharedPreferences;

import com.google.android.gms.wearable.DataMap;
import com.mariux.teleport.lib.TeleportService;
import com.squareup.otto.Subscribe;
import com.vssnake.gpswear.config.GpsWearApp;
import com.vssnake.gpswear.otto.LocationInitialize;
import com.vssnake.gspshared.StacData;

/**
 * Created by vssnake on 05/12/2014.
 */
public class WearableCommunicator extends TeleportService{

    SharedPreferences settings;

    public static LocationInterface locationHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        ((GpsWearApp)getApplication()).inject(this);
       settings = getApplication()
                .getApplicationContext().getSharedPreferences(StacData.PREFS_NAME, 0);
        MainPresenter.bus.register(this);
    }


    @Override
    public void onMessageReceived(String path, byte[] data) {
        if (path.equals(StacData.REQUEST_MODE_LOCATION)){

            DataMap dataMap = new DataMap();
            dataMap.putBoolean(StacData.REQUEST_MODE_LOCATION_DATA,
                    settings.getBoolean(StacData.REQUEST_MODE_LOCATION,true));
            syncBoolean(StacData.REQUEST_MODE_LOCATION,
                    settings.getBoolean(StacData.REQUEST_MODE_LOCATION, true));

            sendMessage(StacData.REQUEST_MODE_LOCATION_DATA,dataMap.toByteArray());

        }else if (path.equals(StacData.REQUEST_MODE_LOCATION_DATA)){
            DataMap dataMap = DataMap.fromByteArray(data);
            boolean fusionLocation = dataMap.getBoolean(StacData.REQUEST_MODE_LOCATION_DATA);
            changeTypeLocation(fusionLocation);
            sendMessage(StacData.REQUEST_MODE_LOCATION_DATA_OK,data);
        }else if (path.equals(StacData.REQUEST_MODE_TYPEMAP)){
            DataMap dataMap = new DataMap();
            dataMap.putString(StacData.REQUEST_MODE_TYPEMAP,getTypeMap());
            sendMessage(StacData.REQUEST_MODE_TYPEMAP_OK, dataMap.toByteArray());
        }else if (path.equals(StacData.REQUEST_MODE_TYPEMAP_DATA)){
            DataMap dataMap = DataMap.fromByteArray(data);
            String mapType = dataMap.getString(StacData.REQUEST_MODE_TYPEMAP_DATA);
            saveTypeMap(mapType);
            sendMessage(StacData.REQUEST_MODE_TYPEMAP_OK,dataMap.toByteArray());
            if (locationHandler != null){
                locationHandler.onTypeMapChange(mapType);
            }

        }
    }



    public void changeTypeLocation(boolean fusionLocation){

        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(StacData.REQUEST_MODE_LOCATION,fusionLocation);
        editor.apply();
        //Save preferences of fusion Location
        if (locationHandler != null){

            locationHandler.onModeLocationChange(fusionLocation);
        }

    }

    public String  getTypeMap(){
        return settings.getString(StacData.REQUEST_MODE_TYPEMAP,
                getResources().getString(com.vssnake.gspshared.R.string.satelliteMapId));
    }

    public String saveTypeMap(String typeMap){
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(StacData.REQUEST_MODE_TYPEMAP,typeMap);
        editor.apply();
        return typeMap;
    }

    @Subscribe
    public void answerAvailable(LocationInitialize event) {
        boolean fusionLocation = settings.getBoolean(StacData.REQUEST_MODE_LOCATION,true);
        DataMap dataMap = new DataMap();
        dataMap.putBoolean(StacData.REQUEST_MODE_LOCATION_DATA,fusionLocation);
        sendMessage(StacData.REQUEST_MODE_LOCATION_DATA,dataMap.toByteArray());
    }

    public interface LocationInterface{
        void onModeLocationChange(Boolean locationFusion);
        void onTypeMapChange(String typeMap);
    }
}
