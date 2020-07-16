package com.example.cs496_week2;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private int mPageCount;



    public PagerAdapter(FragmentManager fm, int pageCount) {

        super(fm);

        this.mPageCount = pageCount;

    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                PhoneFragment phoneFragment = new PhoneFragment();
                return phoneFragment;

//            case 1:
//                GalleryFragment galleryFragment = new GalleryFragment();
//                return galleryFragment;
//
//            case 2:
//                ThirdFragment thirdFragment = new ThirdFragment();
//                return thirdFragment;

            default:
                return null;

        }



    }

    @Override
    public int getCount() {
        return mPageCount;
    }
}
