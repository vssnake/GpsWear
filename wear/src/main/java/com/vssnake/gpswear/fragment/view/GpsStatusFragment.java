package com.vssnake.gpswear.fragment.view;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.wearable.activity.InsetActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vssnake.gpswear.MainPresenter;
import com.vssnake.gpswear.R;
import com.vssnake.gpswear.config.GpsWearApp;
import com.vssnake.gpswear.customviews.CustomProgressBar;
import com.vssnake.gpswear.fragment.presenter.GpsStatusPresenter;


import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GpsStatusFragment#newInstance} factory method to
 * create an instance of this fragment_gps_status.
 */
public class GpsStatusFragment extends Fragment implements MainPresenter.FragmentShowEvent{

    public static final String TAG = "GPSStatusFragment";

    @InjectView(R.id.gs_title)
    TextView mTitle;
    @InjectView(R.id.gs_altitude)
    TextView mAltitude;
    @InjectView(R.id.gs_error)
    TextView mError;
    @InjectView(R.id.gs_latitude)
    TextView mLatitude;
    @InjectView(R.id.gs_longuitude)
    TextView mLongitude;
    @InjectView(R.id.gs_speed)
    TextView mSpeed;
    @InjectView(R.id.gs_satellite_count)
    TextView mSatelliteCount;
    @InjectView(R.id.gs_satellites)
    LinearLayout mSatellites;
    @InjectView(R.id.gs_test)
    LinearLayout mTest;

    @Inject
    GpsStatusPresenter presenter;

    /**
     * Use this factory method to create a new instance of
     * this fragment_gps_status using the provided parameters.
     * @return A new instance of fragment_gps_status GpsStatusFragment.
     */
    public static GpsStatusFragment newInstance() {
        GpsStatusFragment fragment = new GpsStatusFragment();
        return fragment;
    }

    public GpsStatusFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((GpsWearApp)getActivity().getApplication()).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment_gps_status
        View view;
        //if(((InsetActivity)getActivity()).isRound()){
          //  view = inflater.inflate(R.layout.fragment_gps_status_round,container,false);
       // }else{
           view = inflater.inflate(R.layout.fragment_gps_status,container,false);
        //}


        WatchViewStub stub = (WatchViewStub) view.findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override public void onLayoutInflated(WatchViewStub stub) {
                // Now you can access your views
                ButterKnife.inject(GpsStatusFragment.this,stub);

                presenter.attach(GpsStatusFragment.this);

                presenter.resume();



            }
        });


        return view;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

    }

    @Override
    public void onDetach(){
        super.onDetach();
    }

    public void addSatellite(int powerPercent,boolean fixed,String position){
        if (getActivity() == null){
            return;
        }
        final float scale = getActivity().getResources().getDisplayMetrics().density;
        int pixels = (int) (12 * scale + 0.5f);
        CustomProgressBar progressBar = new CustomProgressBar(getActivity().getApplicationContext());


        int marginPixels = (int) (6 * scale + 0.5f);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                pixels);
        layoutParams.setMargins(0,marginPixels,0,0);

        progressBar.setLayoutParams(layoutParams);

        int color;
        if (fixed){
            if (powerPercent< 10){
                color = R.color.red;
            }else if (powerPercent < 20){
                color = R.color.orange;
            }else if (powerPercent < 30){
                color = R.color.snr_30;
            }else if (powerPercent < 40){
                color = R.color.snr_40;
            }else{
                color = R.color.snr_99;
            }
            color = getActivity().getResources().getColor(color);
        }else{
            color = Color.GRAY;
        }
        progressBar.setData(position,color,powerPercent);

        getSatellites().addView(progressBar);
    }



    public LinearLayout getSatellites(){ return mSatellites;}

    public TextView getTitle() {
        return mTitle;
    }

    public TextView getAltitude() {
        return mAltitude;
    }

    public TextView getError() {
        return mError;
    }

    public TextView getLatitude() {
        return mLatitude;
    }

    public TextView getLongitude() {
        return mLongitude;
    }

    public TextView getSpeed() {
        return mSpeed;
    }

    public TextView getSatelliteCount() {
        return mSatelliteCount;
    }

    @Override
    public void monFragmentShow(String nameFragment) {
        if (nameFragment.equals(this.getClass().getName())){
            presenter.resume();


        }else
        presenter.pause();
    }
}
