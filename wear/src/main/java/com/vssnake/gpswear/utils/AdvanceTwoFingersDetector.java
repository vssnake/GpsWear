package com.vssnake.gpswear.utils;

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
    private static final long LONG_PRESS_TIME = ViewConfiguration.getLongPressTimeout();
    private long mTwoFingerPressTime;

    public AdvanceTwoFingersDetector(){}
    private void reset(long time) {
        mFirstDownTime = time;
        mSeparateTouches = false;
        mTwoFingerTapCount = 0;
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if(mFirstDownTime == 0 || event.getEventTime() - mFirstDownTime > TIMEOUT)
                    reset(event.getDownTime());
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if(event.getPointerCount() == 2) {
                    mTwoFingerPressTime =event.getEventTime() -event.getDownTime();
                    mTwoFingerTapCount++;
                }else
                    mFirstDownTime = 0;
                break;
            case MotionEvent.ACTION_UP:
                if(!mSeparateTouches)
                    mSeparateTouches = true;
                if (mTwoFingerTapCount == 1  && mTwoFingerPressTime >= LONG_PRESS_TIME){
                    return onTwoFingerLongPress();
                }
                else if(mTwoFingerTapCount == 2 && event.getEventTime() - mFirstDownTime < TIMEOUT) {
                    onTwoFingerDoubleTap();
                    mFirstDownTime = 0;
                    return true;
                }
        }

        return false;
    }

    public abstract void onTwoFingerDoubleTap();

    public abstract boolean onTwoFingerLongPress();
}