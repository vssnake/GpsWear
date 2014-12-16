package com.vssnake.gpswear;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.wearable.view.GridViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

/**
 * Created by vssnake on 04/12/2014.
 */
public class GridViewPagerNew extends GridViewPager{

    private static final String TAG = "GridViewPagerNew";

    public boolean intercept = false;

    private GestureDetector gestureScanner;

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
        if (intercept == true){
            boolean result = gestureScanner.onTouchEvent(ev);
            return result;
        }else{
            return super.onInterceptTouchEvent(ev);
        }



    }

    public void init(Context context){
        gestureScanner = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.i(TAG, "onDoubleTap");
                if (intercept == true){
                    return false;
                }
                return true;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                Log.i(TAG, "onDoubleTapEvent");
                if (intercept == true){
                    return false;
                }
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                Log.i(TAG, "onDown");
                if (intercept == true){
                    return false;
                }
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2,
                                   float velocityX, float velocityY) {
                Log.i(TAG, "onFling");
                if (intercept == true){
                    return false;
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.i(TAG, "onLongPress");
                if (intercept == true){
                    intercept = false;
                }
                         }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                    float distanceX, float distanceY) {
                if (intercept == true){
                    return false;
                }
                return true;

            }

            @Override
            public void onShowPress(MotionEvent e) {
                Log.i(TAG, "onShowPress");
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (intercept == true){
                    return false;
                }
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.i(TAG, "onSingleTapUp");
                if (intercept == true){
                    return false;
                }
                return true;
            }

        });
    }

}
