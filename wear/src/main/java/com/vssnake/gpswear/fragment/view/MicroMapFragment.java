package com.vssnake.gpswear.fragment.view;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.MapView;
import com.mapbox.mapboxsdk.overlay.GpsLocationProvider;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;
import com.vssnake.gpswear.MainPresenter;
import com.vssnake.gpswear.R;
import com.vssnake.gpswear.config.GpsWearApp;
import com.vssnake.gpswear.fragment.presenter.MicroMapPresenter;
import com.vssnake.gspshared.StacData;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MicroMapFragment#newInstance} factory method to
 * create an instance of this fragment_gps_status.
 */
public class MicroMapFragment extends Fragment implements MainPresenter.FragmentShowEvent{

    private static final String TAG ="MicroMapFragment";


    @InjectView(R.id.mp_mapview)
    com.mapbox.mapboxsdk.views.MapView mMapView;
    @InjectView(R.id.mp_menu)
    LinearLayout mMenuLayout;
    @InjectView(R.id.mp_main_layout)
    RelativeLayout mMainLayout;
    @InjectView(R.id.mp_show_position)
    Button mShowPositionButton;
    @InjectView(R.id.mp_motion_tracking)
    Button mMotionTrackingButton;
    @InjectView(R.id.mp_return)
    Button mReturnButton;
    @InjectView (R.id.mp_frame_layout)
    FrameLayout mFrameLayout;


    private ImageView viewYouAreHere;

    @Inject
    MicroMapPresenter presenter;

    private UserLocationOverlay mUserLocation;


    private String currentMap = null;

    public GestureDetector mGestureDetector;

    public static MicroMapFragment newInstance() {
        MicroMapFragment fragment = new MicroMapFragment();
        return fragment;
    }

    public MicroMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((GpsWearApp)getActivity().getApplication()).inject(this);
    }

    public boolean disableMove = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment_gps_status
        View view = inflater.inflate(R.layout.fragment_micro_map, container, false);

        ButterKnife.inject(this, view);


       // mMapView = (com.mapbox.mapboxsdk.views.MapView) view.findViewById(R.id.mapview);
        mMapView.setMinZoomLevel(mMapView.getTileProvider().getMinimumZoomLevel());
        mMapView.setMaxZoomLevel(mMapView.getTileProvider().getMaximumZoomLevel());
        //mv.setCenter(mv.getTileProvider().getCenterCoordinate());
        mMapView.setZoom(0);
        mMapView.setMaxZoomLevel(18);
        mMapView.setUserLocationRequiredZoom(18);
        mMapView.setDiskCacheEnabled(true);
        currentMap = getString(R.string.streetMapId);

       // mMapView.loadFromGeoJSONURL("https://gist.githubusercontent.com/tmcw/10307131/raw/21c0a20312a2833afeee3b46028c3ed0e9756d4c/map.geojson");
        GpsLocationProvider provider = new GpsLocationProvider(presenter.getMainPresenter().getContext());
        mMapView.setUserLocationEnabled(true);
        mUserLocation = mMapView.getUserLocationOverlay();
        //mUserLocation = new UserLocationOverlay(provider,mMapView);

        mUserLocation.setDrawAccuracyEnabled(true);

        mMapView.setUserLocationTrackingMode(UserLocationOverlay.TrackingMode.NONE);



        reloadTrackingButton();


        mGestureDetector = new GestureDetector(getActivity().getApplicationContext(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public void onLongPress(MotionEvent e) {
                Log.i(TAG, "onLongPress");
                reloadTrackingButton();
                changeVisibilityMenu();



            }
        });


        mFrameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                if (mMenuLayout.getVisibility() == View.INVISIBLE){
                    return mMapView.onTouchEvent(event);
                }
                return false;
            }
        });


        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onReturnButtonClicked();
            }
        });
        mMotionTrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserLocationOverlay.TrackingMode tracking = mUserLocation.getTrackingMode();
                switch (tracking){
                    case FOLLOW:
                    case FOLLOW_BEARING:
                        mMapView.setUserLocationTrackingMode(UserLocationOverlay.TrackingMode.NONE);

                        break;
                    case NONE:
                        mMapView.setUserLocationTrackingMode(UserLocationOverlay.TrackingMode.FOLLOW_BEARING);
                        break;
                }
                reloadTrackingButton();
            }
        });



        mShowPositionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMapView.setUserLocationTrackingMode(UserLocationOverlay.TrackingMode.NONE);
                mMapView.goToUserLocation(true);

                presenter.onGoToPositionClicked();

                reloadTrackingButton();
                changeVisibilityMenu();
            }
        });








      /*  mCenterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuLayout.setVisibility(View.INVISIBLE);
                presenter.centerMap();
            }
        });


        mRelativeLayout.setClickable(true);


        viewYouAreHere = new ImageView(getActivity());
        viewYouAreHere.setImageResource(R.drawable.youarehere2);


        mMapView.setDecoder(presenter.getDecoder());

        mMapView.setSize(StacData.MAP_SIZE, StacData.MAP_SIZE);
        mMapView.setTransitionsEnabled(false);
        mMapView.addDetailLevel( 1f, "%row% %col%", null, StacData.WIDTH_BITMAP,
                StacData.HEIGHT_BITMAP);
        mMapView.setScale( 1f );
        */
        presenter.attach(this);

        return view;
    }

    public void changeVisibilityMenu(){
        switch (mMenuLayout.getVisibility()){
            case View.VISIBLE:
                mMenuLayout.setVisibility(View.INVISIBLE);
                break;
            case View.INVISIBLE:
                mMenuLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

    }


    @Override
    public void onDetach(){
        super.onDetach();
        presenter.detach();

    }

    public void onPause(){
        super.onPause();
        presenter.pause();
    }
    public void onResume(){
        super.onResume();

    }

    public void setYouAreHere(final double x, final double y){
        if (!isVisible()){
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mMapView != null) {
                    //mMapView.removeAllMarkers();

                  //  mMapView.addMarker(viewYouAreHere, x - viewYouAreHere.getMeasuredWidth() / 2, y - viewYouAreHere.getMeasuredHeight() / 2);
                }
            }
        });
    }

    public void frameTo( final double x, final double y ) {
        if (!isVisible()){
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mMapView != null) {
                    //mMapView.moveToAndCenter(x, y);
                   // mMapView.setScale(1);
                   // mMapView.clear();
                   // mMapView.refresh();
                }
            }
        });
    }

    public void refresh(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
              //  mMapView.refresh();
            }
        });
    }


    private void reloadTrackingButton(){
        UserLocationOverlay.TrackingMode tracking = mUserLocation.getTrackingMode();
        String motionTrackingString =
                MicroMapFragment.this.getResources().getString(R.string.motion_tracking);
        switch (tracking){
            case FOLLOW:
            case FOLLOW_BEARING:
                mMotionTrackingButton.setText(motionTrackingString + " | ON");
                break;
            case NONE:
                mMotionTrackingButton.setText(motionTrackingString + " | OFF");

                break;
        }
    }

    public ImageView getViewYouAreHere() {
        return viewYouAreHere;
    }

    @Override
    public void monFragmentShow(String nameFragment) {
        if (nameFragment.equals(this.getClass().getName())) {
            presenter.resume();

        } else{
            mMenuLayout.setVisibility(View.INVISIBLE);
            presenter.pause();
    }
    }

    public com.mapbox.mapboxsdk.views.MapView getMapView() {
        return mMapView;
    }

    public UserLocationOverlay getUserLocation() {
        return mUserLocation;
    }
}
