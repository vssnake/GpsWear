package com.vssnake.gpswear.fragment.presenter;

import android.app.Fragment;

import com.vssnake.gpswear.MainPresenter;

/**
 * Created by vssnake on 10/11/2014.
 */
public  abstract class BasicPresenter {
    public MainPresenter mainActivityPresenter;

    public BasicPresenter(MainPresenter mainPresenter){
        this.mainActivityPresenter = mainPresenter;
    }

    public abstract void attach(Fragment fragment);


    public MainPresenter getMainPresenter(){
        return mainActivityPresenter;
    }




}
