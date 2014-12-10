package com.mapbox.mapbox.sdk.shared.network;

import android.os.Handler;

/**
 * Created by vssnake on 03/12/2014.
 */

public class MonitorObject {

    String myMonitorObject = "";
    boolean wasSignalled = false;
    boolean timeOut = true;
    Handler handler;
    Runnable runnable;

    public MonitorObject(){

    }

    public void doWait(int time){
        synchronized(myMonitorObject){
            while(!wasSignalled){
                try{
                    myMonitorObject.wait(time);
                } catch(InterruptedException e){}
                if (timeOut){
                    wasSignalled = true;
                }
            }
            //clear signal and continue running.
            wasSignalled = false;
        }
    }

    public void doNotify(){
        synchronized(myMonitorObject){
            wasSignalled = true;
            timeOut = true;
            myMonitorObject.notify();
        }
    }
}
