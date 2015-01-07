package com.vssnake.gpswear.utils;

import android.support.annotation.IntDef;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mapbox.mapboxsdk.views.MapView;
import com.vssnake.gpswear.GridViewPagerNew;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by vssnake on 24/12/2014.
 */
public class TutorialManager {

    AdvanceTwoFingersDetector mMultiTouchListener;
    GestureDetector mGestureDetector;
    GridViewPagerNew mPagerView;
    MapView mMapView;

    FrameLayout mHelpLayout;
    TextView mHelpText;

    final static int GPS_STATUS_MODE = 0;
    final static int MAP_FLING_MODE = 1;
    final static int MAP_ZOOM_IN_MODE = 2;
    final static int MAP_ZOOM_OUT_MODE = 3;
    final static int MAP_LONG_TOUCH_MODE = 4;
    final static int MAP_BACK_MODE = 5;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({GPS_STATUS_MODE, MAP_FLING_MODE, MAP_ZOOM_IN_MODE,
            MAP_ZOOM_OUT_MODE,MAP_LONG_TOUCH_MODE,MAP_BACK_MODE})
    public @interface NavigationMode {}

    @NavigationMode int navigationMode;

    public TutorialManager(AdvanceTwoFingersDetector multiTouchListener,
                           GestureDetector gestureDetector,
                           GridViewPagerNew pagerView,
                           final MapView mapView,
                           FrameLayout helpLayout,
                           TextView helpText){

        this.mGestureDetector = gestureDetector;
        this.mMultiTouchListener = multiTouchListener;
        this.mPagerView = pagerView;
        this.mMapView = mapView;

        this.mHelpLayout = helpLayout;
        this.mHelpText = helpText;

        this.mHelpLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (navigationMode){
                    case GPS_STATUS_MODE:
                    case MAP_BACK_MODE:
                        mPagerView.onInterceptTouchEvent(event);
                        break;
                    case MAP_FLING_MODE:
                        mapView.onTouchEvent(event);
                        break;
                    case MAP_ZOOM_IN_MODE:
                        mapView.onTouchEvent(event);
                        break;
                    case MAP_ZOOM_OUT_MODE:
                        mapView.onTouchEvent(event);
                        break;
                    case MAP_LONG_TOUCH_MODE:
                        mMultiTouchListener.onTouchEvent(event);
                        break;
                }
                return false;
            }
        });

    }


    public void changePage(int page){

    }

    public void flingMap(){

    }
    public void zoomIn(){}

    public void zoomOut(){}

    public void zoomMenu(){}
}
