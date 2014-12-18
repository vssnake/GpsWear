package com.vssnake.gpswear;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.wearable.view.GridViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.vssnake.gpswear.utils.AdvanceTwoFingersDetector;

/**
 * Created by vssnake on 04/12/2014.
 */
public class GridViewPagerNew extends GridViewPager{

    private static final String TAG = "GridViewPagerNew";

    public boolean intercept = false;

    private GestureDetector gestureScanner;
    AdvanceTwoFingersDetector multiTouchListener;

    public GridViewPagerNew(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public GridViewPagerNew(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GridViewPagerNew(Context context) {
        super(context);
        init(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (intercept){
            if (multiTouchListener.onTouchEvent(ev)){
                return true;
            }
            return gestureScanner.onTouchEvent(ev);
        }else{
            return super.onInterceptTouchEvent(ev);
        }



    }

    public void init(Context context){

        multiTouchListener = new AdvanceTwoFingersDetector() {
            @Override
            public void onTwoFingerDoubleTap() {
                Log.i(TAG, "onTwoFingerDoubleTap");

            }

            @Override
            public boolean onTwoFingerLongPress() {
                Log.i(TAG,"onTwoFingersLongPress");
                if (intercept){
                    intercept = false;
                    return false;
                }
                return true;
            }
        };

        gestureScanner = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.i(TAG, "onDoubleTap");
                return intercept == false;

            }


            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                Log.i(TAG, "onDoubleTapEvent");
                return intercept == false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                Log.i(TAG, "onDown");
                return intercept == false;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2,
                                   float velocityX, float velocityY) {
                Log.i(TAG, "onFling");
                return intercept == false;
            }



            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                    float distanceX, float distanceY) {
                return intercept == false;

            }



            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return intercept == false;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.i(TAG, "onSingleTapUp");
                return intercept == false;
            }

        });
    }

}
