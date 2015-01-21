package com.vssnake.gpswear;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.DismissOverlayView;
import android.support.wearable.view.GridViewPager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

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

    @InjectView(R.id.dismiss_overlay)
    DismissOverlayView mDisMissOverlay;

    @InjectView(R.id.main_help_layout)
    FrameLayout mHelpLayout;
    @InjectView(R.id.main_help_text)
    TextView mTextLayout;

    private static final String TAG ="MainActivity";

    FragmentGridAdapter mLocalPagerAdapter;

    volatile GestureDetector mDetector;

    @Inject MainPresenter presenter;

    GpsStatusFragment mGpsStatusFragment;
    MicroMapFragment mMicroMapFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        ((GpsWearApp)getApplication()).inject(this);
        ButterKnife.inject(this);
        init();



        // Configure a gesture detector
        mDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(MotionEvent ev) {
                mDisMissOverlay.show();
            }
        });

    }


    public void onReadyForContent() {

    }

    /**
     * Change type of map
     * @param mapType
     */
    public void changeMapType(String mapType){
        mMicroMapFragment.changeMapType(mapType);
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

    protected void onDestroy(){
        super.onDestroy();
        presenter.stopLocation();
        presenter.dettach();
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

        mMicroMapFragment = MicroMapFragment.newInstance();
        mGpsStatusFragment = GpsStatusFragment.newInstance();

        mLocalPagerAdapter.addFragment(mGpsStatusFragment);
        mLocalPagerAdapter.addFragment(mMicroMapFragment);

        presenter.addChangeFragmentEvent(mGpsStatusFragment);
        presenter.addChangeFragmentEvent(mMicroMapFragment);

        mGridViewPager.setAdapter(mLocalPagerAdapter);


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








    }
    public void changePreviousPage(){
        mGridViewPager.setCurrentItem(0,0,true);
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

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
       //return mDetector.onTouchEvent(ev);
        return false;
    }


}

