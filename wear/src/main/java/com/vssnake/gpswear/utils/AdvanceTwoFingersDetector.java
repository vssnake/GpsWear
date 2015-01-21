package com.vssnake.gpswear.utils;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Created by vssnake on 17/12/2014.
 */
public abstract class AdvanceTwoFingersDetector {
    private static final int TIMEOUT = ViewConfiguration.getDoubleTapTimeout() + 100;
    private long mFirstDownTime = 0;
    private boolean mSeparateTouches = false;
    private byte mTwoFingerTapCount = 0;
    private static final long LONG_PRESS_TIME = 500;
    private long mTwoFingerPressTime;
    private boolean mTwoFingerLongTap = false;

    Handler mHandler = new Handler();
    Runnable mRunnable;

    float mX = 0;
    float mY = 0;

    public AdvanceTwoFingersDetector(){
        mRunnable =  new Runnable() {
            @Override
            public void run() {
                onTwoFingerLongPress();
                mTwoFingerLongTap = true;
            }
        };
    }
    private void reset(long time) {
        mFirstDownTime = time;
        mSeparateTouches = false;
        mTwoFingerTapCount = 0;
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                if(mFirstDownTime == 0 || event.getEventTime() - mFirstDownTime > TIMEOUT)

                    if (event.getPointerCount() == 2){
                        mX = event.getX();
                        mY = event.getY();

                        mHandler.postDelayed(mRunnable, LONG_PRESS_TIME);
                    }

                    reset(event.getDownTime());
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mHandler.removeCallbacks(mRunnable);
                if(event.getPointerCount() == 2) {
                    mTwoFingerPressTime =event.getEventTime() -event.getDownTime();
                    if (mTwoFingerPressTime >= LONG_PRESS_TIME){
                        return true;
                    }
                    mTwoFingerTapCount++;
                }else
                    mFirstDownTime = 0;
                break;
            case MotionEvent.ACTION_UP:



                if(!mSeparateTouches)
                    mSeparateTouches = true;
                if (mTwoFingerTapCount == 1  && mTwoFingerPressTime >= LONG_PRESS_TIME){
                    //return onTwoFingerLongPress();
                }
                else if(mTwoFingerTapCount == 2 && event.getEventTime() - mFirstDownTime < TIMEOUT) {
                    onTwoFingerDoubleTap();
                    mFirstDownTime = 0;
                    return true;
                }
                if (mTwoFingerLongTap){
                    mTwoFingerLongTap = false;
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mX - 40 > event.getX() || mX + 40 < event.getX() || mY - 40 > event.getY() || mY + 40 < event.getY()){
                    mHandler.removeCallbacks(mRunnable);
              }

                break;

        }

        return false;
    }

    public abstract void onTwoFingerDoubleTap();

    public abstract boolean onTwoFingerLongPress();
}