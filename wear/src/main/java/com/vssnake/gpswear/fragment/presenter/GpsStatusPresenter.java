package com.vssnake.gpswear.fragment.presenter;


import android.app.Fragment;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;

import com.vssnake.gpswear.MainPresenter;
import com.vssnake.gpswear.R;
import com.vssnake.gpswear.fragment.view.GpsStatusFragment;
import com.vssnake.gpswear.utils.LocationManager;

import java.util.Iterator;

/**
 * Created by vssnake on 10/11/2014.
 */
public class GpsStatusPresenter extends BasicPresenter {

    GpsStatusFragment mFragment;

    LocationManager.GpsDataChangeHandler mLocationHandler;

    public GpsStatusPresenter(MainPresenter mainPresenter) {
        super(mainPresenter);
    }

    @Override
    public void attach(Fragment fragment) {
        mFragment = (GpsStatusFragment)fragment;
    }


    public void initGps(){
        if (!getMainPresenter().hasGps()){
            mFragment.getTitle().setText(R.string.gps_not_found);
            return;
        }else{
            mFragment.getTitle().setText(R.string.gps_searching);
        }

       // mainActivityPresenter.initLocation();
    }

    public void resume(){
        initGps();
        mLocationHandler = new  LocationManager.GpsDataChangeHandler() {
            @Override
            public void onLocationChange(Location location) {
                if (location == null){
                    mFragment.getLatitude().setText(R.string.no_data);
                    mFragment.getLongitude().setText(R.string.no_data);
                    mFragment.getError().setText(R.string.no_data);
                    mFragment.getAltitude().setText(R.string.no_data);
                    mFragment.getSpeed().setText(R.string.no_data);
                    mFragment.getTitle().setText(R.string.no_data);
                    return;
                }
                StringBuilder stringBuilder = new StringBuilder();

                String latitude = Location.convert(location.getLatitude(),Location.FORMAT_MINUTES);
                stringBuilder.append(latitude.replace(":","º"));
                stringBuilder.append("'N");
                mFragment.getLatitude().setText(stringBuilder.toString());

                stringBuilder = new StringBuilder();
                String longitude = Location.convert(location.getLongitude(),Location.FORMAT_MINUTES);
                stringBuilder.append(longitude.replace(":","º"));
                stringBuilder.append("'E");
                mFragment.getLongitude().setText(stringBuilder.toString());

                mFragment.getError().setText(location.getAccuracy() +"");
                mFragment.getAltitude().setText((int)location.getAltitude() +"");
                mFragment.getSpeed().setText(location.getSpeed()*18/5 +" Km/h");

                if (getMainPresenter().hasFusionLocationEnabled()){
                    mFragment.getTitle().setText(R.string.gps_fusion_found);
                    mFragment.getSatelliteCount().setText(R.string.no_data);
                }else{
                    mFragment.getTitle().setText(R.string.gps_found);
                }


            }

            @Override
            public void onGpsStatusChange(GpsStatus gpsStatus) {
                if (mFragment == null){
                    return;
                }
                if (gpsStatus == null || mainActivityPresenter.hasFusionLocationEnabled()){
                    mFragment.getSatellites().removeAllViews();
                    mFragment.getTitle().setText(R.string.gps_searching);
                    mFragment.getSatelliteCount().setText(R.string.no_data);
                    return;
                }
                mFragment.getSatellites().removeAllViews();
                int maxSatellites = 0; int satellitesUsed = 0;
                int cont = 1;
                Iterable<GpsSatellite> satelliteIterable = gpsStatus.getSatellites();
                for (Iterator<GpsSatellite> iter = satelliteIterable.iterator(); iter.hasNext();){
                    GpsSatellite gpsSatellite = iter.next();
                    calculateSatelliteData(
                            gpsSatellite.getSnr(),
                            gpsSatellite.usedInFix(),
                            cont);
                    cont++;
                    if (gpsSatellite.usedInFix()){
                        satellitesUsed++;
                    }
                    maxSatellites ++;
                }
                if (satellitesUsed == 0){
                    mFragment.getTitle().setText(R.string.gps_searching);
                }
                mFragment.getSatelliteCount().setText( satellitesUsed + " / " + maxSatellites);

            }

            @Override
            public void onNorthChange(float degrees) {

            }
        };

        mainActivityPresenter.addLocationHandler(mLocationHandler);

        getMainPresenter().sendLastLocation();
    }
    public void pause(){
        mainActivityPresenter.removeLocationHandler(mLocationHandler);
    }

    private void calculateSatelliteData(float SNR,boolean fix,int position){
       mFragment.addSatellite((int)SNR,fix,String.valueOf(position));
    }


}
