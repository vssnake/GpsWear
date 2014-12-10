package com.vssnake.gpswear;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.wearable.view.FragmentGridPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vssnake on 09/11/2014.
 */
public class FragmentGridAdapter extends FragmentGridPagerAdapter{

    private final Context mContext;

    List<Fragment> mFragments;

    public FragmentGridAdapter(FragmentManager fm, Context mContext) {
        super(fm);
        this.mContext = mContext;
        mFragments = new ArrayList<Fragment>();
    }

    @Override
    public Fragment getFragment(int row, int col) {
        return mFragments.get(col);
    }

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount(int i) {
        return mFragments.size();
    }

    public void addFragment(Fragment fragment){
        mFragments.add(fragment); }
}
