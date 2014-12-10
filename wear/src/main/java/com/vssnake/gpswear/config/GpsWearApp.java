package com.vssnake.gpswear.config;

import android.app.Application;



import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

/**
 * Created by vssnake on 29/10/2014.
 */
public class GpsWearApp extends Application {

    private ObjectGraph graph;

    @Override public void onCreate() {
        super.onCreate();

        graph = ObjectGraph.create(getModules().toArray());
    }

    protected List<Object> getModules() {
        return Arrays.asList(
                new AndroidModule(this),
                new ConfigModule(this)

        );
    }

    public void inject(Object object) {
        graph.inject(object);
    }

}
