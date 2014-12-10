package com.vssnake.gpswear;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.activity.InsetActivity;
import android.support.wearable.view.GridViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.ImageView;

import com.vssnake.gpswear.config.GpsWearApp;
import com.vssnake.gpswear.fragment.view.GpsStatusFragment;
import com.vssnake.gpswear.fragment.view.MicroMapFragment;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends Activity
        {

    @InjectView(R.id.main_pager)
    GridViewPagerNew mGridViewPager;
    @InjectView(R.id.main_indicator_0)
    ImageView mIndicator1;

    @InjectView(R.id.main_indicator_1)
    ImageView mIndicator2;


    private static final String TAG ="MainActivity";

    FragmentGridAdapter mLocalPagerAdapter;

    @Inject MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        ((GpsWearApp)getApplication()).inject(this);
        ButterKnife.inject(this);
        init();





    }


    public void onReadyForContent() {

    }




    @Override
    protected void onResume() {
        super.onResume();
        presenter.initLocation();


    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.stopLocation();

    }





   /* @Override
    public boolean dispatchTouchEvent(MotionEvent event){

        if (presenter.getMotionEvent() != null){
            if (!presenter.getMotionEvent().onMotionEvent(event)){
                return super.dispatchTouchEvent(event);
            }else{
                return true;
            }
        }else{
            return super.dispatchTouchEvent(event);
        }



    }*/






    public void init(){
        presenter.attachMainActivity(this);

        mLocalPagerAdapter = new FragmentGridAdapter(getFragmentManager(),
                getApplicationContext());
        GpsStatusFragment gpsStatusFragment = new GpsStatusFragment();
        MicroMapFragment microMapFragment = new MicroMapFragment();
        mLocalPagerAdapter.addFragment(gpsStatusFragment);
        mLocalPagerAdapter.addFragment(microMapFragment);

        presenter.addChangeFragmentEvent(gpsStatusFragment);
        presenter.addChangeFragmentEvent(microMapFragment);



        mGridViewPager.setOnPageChangeListener(new GridViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, int i2, float v, float v2, int i3, int i4) {

            }

            @Override
            public void onPageSelected(int row, int column) {
                setIndicator(column);
                String nameFragment = mLocalPagerAdapter.getFragment(row,column).getClass().getName();
                presenter.changeFragment(nameFragment);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });


        mGridViewPager.setAdapter(mLocalPagerAdapter);


    }

    private void setIndicator(int paramInt)
    {
        switch (paramInt)
        {
            case 0:
                mIndicator1.setImageResource(R.drawable.pagerdot);
                mIndicator2.setImageResource(R.drawable.pagercircle);
                return;
            case 1:
                mIndicator1.setImageResource(R.drawable.pagercircle);
                mIndicator2.setImageResource(R.drawable.pagerdot);
                break;

        }

    }


            public GridViewPagerNew getGridViewPager() {
                System.out.println("");
                return mGridViewPager;
            }
        }