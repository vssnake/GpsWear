package com.vssnake.gpswear.utils;

import android.view.MotionEvent;
import android.view.ViewConfiguration;

import java.util.Timer;
import java.util.TimerTask;

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

    Timer mTimer = new Timer();

    float mX = 0;
    float mY = 0;

    public AdvanceTwoFingersDetector(){}
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
                        mTimer = new Timer();
                        mTimer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                mTimer.cancel();
                                onTwoFingerLongPress();
                                mTwoFingerLongTap = true;
                            }
                        }, LONG_PRESS_TIME, LONG_PRESS_TIME);
                    }

                    reset(event.getDownTime());
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mTimer.cancel();
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
               // if (mX - 10 > event.getX() || mX + 10 < event.getX() || mY - 10 > event.getY() || mY + 10 < event.getY()){
               //     mTimer.cancel();
              //  }

                break;

        }

        return false;
    }

    public abstract void onTwoFingerDoubleTap();

    public abstract boolean onTwoFingerLongPress();
}