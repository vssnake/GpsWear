package com.vssnake.gpswear.config;

import android.content.Context;
import android.hardware.SensorManager;
import android.location.LocationManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by vssnake on 29/10/2014.
 */
@Module(library = true)
public class AndroidModule {
    private final GpsWearApp application;

    public AndroidModule(GpsWearApp application) {
        this.application = application;
    }

    /**
     * Allow the application context to be injected but require that it be annotated with
     * {@link ForApplication @Annotation} to explicitly differentiate it from an activity context.
     */
    @Provides
    @Singleton
    @ForApplication
    Context provideApplicationContext() {
        return application;
    }

    @Provides @Singleton
    LocationManager provideLocationManager() {
        return (LocationManager) application.getSystemService(LOCATION_SERVICE);
    }

    @Provides  @Singleton
    SensorManager sensorManager(){
        return (SensorManager) application.getSystemService(Context.SENSOR_SERVICE);
    }
}