package com.vssnake.gpswear;

import android.content.SharedPreferences;

import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.mariux.teleport.lib.TeleportService;
import com.squareup.otto.Subscribe;
import com.vssnake.gpswear.config.GpsWearApp;
import com.vssnake.gpswear.fragment.presenter.MicroMapPresenter;
import com.vssnake.gpswear.otto.LocationInitialize;
import com.vssnake.gspshared.StacData;

import javax.inject.Inject;

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
        }
    }



    public void changeTypeLocation(boolean fusionLocation){

        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(StacData.REQUEST_MODE_LOCATION,fusionLocation);
        editor.commit();
        //Save preferences of fusion Location
        if (locationHandler != null){

            locationHandler.onModeLocationChange(fusionLocation);
        }

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
    }
}
