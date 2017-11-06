package com.zhumj.mytab

import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * @author Created by Administrator
 * @date on 2017/11/6
 * @function
 */
class MFragmentAdapter(fm: FragmentManager?): FragmentPagerAdapter(fm) {
    private val fragments = ArrayList<Fragment>()

    init {
        for (i in 1 until 5) {
            val bundle = Bundle()
            bundle.putString("text", "Tab$i")

            val fragment = MFragment()
            fragment.arguments = bundle
            fragments.add(fragment)
        }
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}