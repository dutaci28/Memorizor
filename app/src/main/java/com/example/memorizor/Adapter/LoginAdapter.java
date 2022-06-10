package com.example.memorizor.Adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.memorizor.Fragments.LoginTabFragment;
import com.example.memorizor.Fragments.RegisterTabFragment;

public class LoginAdapter extends FragmentPagerAdapter {

    private Context context;
    private String[] tabTitles = new String[]{"Login","Register"};
    int totalTabs;

    public LoginAdapter(FragmentManager fm){
        super(fm);
        this.totalTabs=2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public int getCount() {
        return totalTabs;
    }

    public Fragment getItem(int position){
        switch (position){
            case 0:
                LoginTabFragment loginTabFragment = new LoginTabFragment();
                return loginTabFragment;

            case 1:
                RegisterTabFragment registerTabFragment = new RegisterTabFragment();
                return registerTabFragment;
            default:
                return null;
        }
    }
}
