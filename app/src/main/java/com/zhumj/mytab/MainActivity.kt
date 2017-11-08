package com.zhumj.mytab

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var build: TabBarGroup.Builder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initData()
        initView()
    }

    private fun initData() {
        val tabBeans = ArrayList<TabBarGroup.TabBean>()
        for (i in 1 until 5) {
            val tabBean = TabBarGroup.TabBean()
            tabBean.mTitle = "Tab$i"
            when(i) {
                1 -> {
                    tabBean.mNormIcon = getResourcesDrawable(R.mipmap.bottombar_zzx_norm)
                    tabBean.mSelectIcon = getResourcesDrawable(R.mipmap.bottombar_zzx_select)
                }
                2 -> {
                    tabBean.mNormIcon = getResourcesDrawable(R.mipmap.bottombar_forum_norm)
                    tabBean.mSelectIcon = getResourcesDrawable(R.mipmap.bottombar_forum_select)
                }
                3 -> {
                    tabBean.mNormIcon = getResourcesDrawable(R.mipmap.bottombar_deal_norm)
                    tabBean.mSelectIcon = getResourcesDrawable(R.mipmap.bottombar_deal_select)
                }
                else -> {
                    tabBean.mNormIcon = getResourcesDrawable(R.mipmap.bottombar_my_norm)
                    tabBean.mSelectIcon = getResourcesDrawable(R.mipmap.bottombar_my_select)
                }
            }
            tabBeans.add(tabBean)
        }
        build = TabBarGroup.Builder()
                .setHasIcon(true)
                .setTabList(tabBeans)
                .setTitleSize(15f)
                .setTitleColor(Color.parseColor("#929292"))
                .setSelectColor(Color.parseColor("#33CC66"))
    }

    private fun getResourcesDrawable(id: Int): Drawable {
        return ContextCompat.getDrawable(this, id)
    }

    private fun initView() {
        viewPager.adapter = MFragmentAdapter(supportFragmentManager)
        tabBarGroup_top.setViewPager(viewPager)
        tabBarGroup_bottom.setViewPager(viewPager, true, build)
    }
}
