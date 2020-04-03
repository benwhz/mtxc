package com.iweon.mtxc;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class FragmentAdapter extends FragmentStatePagerAdapter {
    Context context;
    List<Fragment> listFragment;

    public FragmentAdapter(FragmentManager fm, Context context, List<Fragment> listFragment) {
        super(fm);
        this.context = context;
        this.listFragment = listFragment;
    }

    @Override
    public Fragment getItem(int i) {
        return this.listFragment.get(i);
    }

    @Override
    public int getCount() {
        return this.listFragment.size();
    }

    public void swapSwitchFragment()
    {
        Fragment tf = this.listFragment.get(0);

        this.listFragment.remove(0);
        this.listFragment.add(0, this.listFragment.get(2));
        this.listFragment.remove(3);
        this.listFragment.add(tf);
    }
}
