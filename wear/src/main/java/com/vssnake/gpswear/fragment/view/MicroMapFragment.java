package com.vssnake.gpswear.fragment.view;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.wearable.view.DismissOverlayView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mapbox.mapboxsdk.overlay.GpsLocationProvider;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;
import com.vssnake.gpswear.MainPresenter;
import com.vssnake.gpswear.R;
import com.vssnake.gpswear.config.GpsWearApp;
import com.vssnake.gpswear.fragment.presenter.MicroMapPresenter;
import com.vssnake.gpswear.utils.AdvanceTwoFingersDetector;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MicroMapFragment#newInstance} factory method to
 * create an instance of this fragment_gps_status.
 */
public class MicroMapFragment extends Fragment implements MainPresenter.FragmentShowEvent {

    private static final String TAG = "MicroMapFragment";


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
    @InjectView(R.id.mp_frame_layout)
    FrameLayout mFrameLayout;
    @InjectView(R.id.mp_dismiss_overlay)
    DismissOverlayView mDismissOverlay;


    private ImageView viewYouAreHere;

    @Inject
    MicroMapPresenter presenter;

    private UserLocationOverlay mUserLocation;


    private String currentMap = null;

    public GestureDetector mGestureDetector;
    AdvanceTwoFingersDetector multiTouchListener;

    public static MicroMapFragment newInstance() {
        return new MicroMapFragment();
    }

    public MicroMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((GpsWearApp) getActivity().getApplication()).inject(this);
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


        mMapView.setUserLocationEnabled(true);
        mUserLocation = mMapView.getUserLocationOverlay();
        //mUserLocation = new UserLocationOverlay(provider,mMapView);

        mUserLocation.setDrawAccuracyEnabled(true);

        mMapView.setUserLocationTrackingMode(UserLocationOverlay.TrackingMode.NONE);


        reloadTrackingButton();


        mGestureDetector = new GestureDetector(getActivity().getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                Log.i(TAG, "onLongPress");


                mDismissOverlay.show();


            }
        });

        multiTouchListener = new AdvanceTwoFingersDetector() {
            @Override
            public void onTwoFingerDoubleTap() {
                Log.i(TAG, "TwoFingerDoubleTap");
            }

            @Override
            public boolean onTwoFingerLongPress() {
                Log.i(TAG, "onTwoFindersLongPress");
                getActivity().runOnUiThread(new Runnable() {


                    @Override
                    public void run() {
                        reloadTrackingButton();

                        changeVisibilityMenu();
                    }
                });
                return true;
            }
        };


        mFrameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (multiTouchListener.onTouchEvent(event)) {
                    return true;
                }
                if (mGestureDetector.onTouchEvent(event)) {
                    return true;
                }
                if (mMenuLayout.getVisibility() == View.INVISIBLE) {
                    return mMapView.onTouchEvent(event);
                }
                return true;
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
                switch (tracking) {
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


        presenter.attach(this);

        return view;
    }

    public void changeVisibilityMenu() {
        switch (mMenuLayout.getVisibility()) {
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
