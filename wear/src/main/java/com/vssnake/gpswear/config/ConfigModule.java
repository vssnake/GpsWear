package com.vssnake.gpswear.config;


import android.hardware.SensorManager;
import android.location.LocationManager;

import com.vssnake.gpswear.MainActivity;
import com.vssnake.gpswear.MainPresenter;
import com.vssnake.gpswear.WearableCommunicator;
import com.vssnake.gpswear.fragment.presenter.GpsStatusPresenter;
import com.vssnake.gpswear.fragment.presenter.MicroMapPresenter;
import com.vssnake.gpswear.fragment.view.GpsStatusFragment;
import com.vssnake.gpswear.fragment.view.MicroMapFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by vssnake on 29/10/2014.
 */
@Module(
        injects = {
                MainActivity.class,
                GpsStatusFragment.class,
                MicroMapFragment.class,
                WearableCommunicator.class,
        },
        library = false,
        complete = false
)
class ConfigModule {

    private final GpsWearApp application;

    public ConfigModule(GpsWearApp application){
        this.application = application;
    }


    @Provides
    @Singleton
    MainPresenter mainPresenter(LocationManager provideLocationManager,
                                SensorManager sensorManager){
        return new MainPresenter(application.getApplicationContext());
    }

    @Provides
    @Singleton
    GpsStatusPresenter gpsStatusPresenter(MainPresenter mainPresenter){
        return new GpsStatusPresenter(mainPresenter);
    }

    @Provides
    @Singleton
    MicroMapPresenter microMapPresenter(MainPresenter mainPresenter){
        return new MicroMapPresenter(mainPresenter);
    }



}
