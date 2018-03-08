package com.example.android.camera2basic

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter



/**
 * Created by idorenyin on 1/21/18.
 */
class ViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        return ImagePreviewFragment.newInstance()

    }

    override fun getCount(): Int {
        return 5
    }
}