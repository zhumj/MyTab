package com.zhumj.mytab

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.RadioGroup
import android.support.v4.view.ViewPager
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout

/**
 * @author Created by zhumj
 * @date on 2017/9/5
 * @function 装载BottombarItem的容器
 */
class TabBarGroup
@JvmOverloads constructor(private val build: Builder? = null, context: Context?, attrs: AttributeSet?)
    : RadioGroup(context, attrs), ViewPager.OnPageChangeListener {

    private var mViewPager: ViewPager? = null
    private var listener: OnTabItemClickListener? = null
    //是否在外部控制TabItem的点击事件
    private var isOutListener = false

    fun setViewPager(viewPager: ViewPager, isOutListener: Boolean = false, position: Int = 0, needItemLayouts: Boolean = false, tBuild: Builder? = build) {
        this.mViewPager = viewPager
        this.mViewPager?.addOnPageChangeListener(this)
        this.isOutListener = isOutListener
        if (needItemLayouts) {
            try {
                val mTabList = tBuild?.getTabList() ?: ArrayList()
                mTabList
                        .map { it ->
                            TabBarItem.Builder(context)
                                    .setHasIcon(tBuild?.getHasIcon() ?: false)
                                    .setTitle(it.mTitle ?: "")
                                    .setNormColor(tBuild?.getNormColor() ?: Color.WHITE)
                                    .setSelectColor(tBuild?.getSelectColor() ?: Color.BLACK)
                                    .setNormDrawable(it.mNormIcon)
                                    .setSelectDrawable(it.mSelectIcon)
                                    .setFormWidth(tBuild?.getFormWidth() ?: 3f)
                                    .setFormColor(tBuild?.getFormColor() ?: Color.BLACK)
                                    .build()
                        }
                        .forEach {
                            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, tBuild?.getTitleSize() ?: 15f)
                            it.setTextColor(tBuild?.getTitleColor() ?: android.R.color.black)
                            it.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                            this.addView(it)
                        }
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }

        setClickedViewChecked(position)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        for (i in 0 until childCount) {
            getChildAt(i).setOnClickListener {
                listener?.onTabItemClick(getChildAt(i), i)
                if (!isOutListener) {
                    setClickedViewChecked(i)
                }
            }
        }
    }

    fun setClickedViewChecked(position: Int){
        for (i in 0 until childCount){
            (getChildAt(i) as TabBarItem).setRadioButtonChecked(i == position)
        }
        if (position != mViewPager?.currentItem) {
            mViewPager?.setCurrentItem(position, false)
        }
    }


    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        updateGradient(position, positionOffset)
    }

    private fun updateGradient(position: Int, positionOffset: Float) {
        if (positionOffset > 0){
            (getChildAt(position) as TabBarItem).updateAlpha(255 * (1 - positionOffset))
            (getChildAt(position + 1) as TabBarItem).updateAlpha(255 * positionOffset)
        }
    }

    override fun onPageSelected(position: Int) {
        for (i in 0 until childCount){
            (getChildAt(i) as TabBarItem).isSelected = i==position
        }
    }

    fun setOnTabItemClickListener (listener: OnTabItemClickListener) {
        this.listener = listener
    }

    interface OnTabItemClickListener {
        fun onTabItemClick(v: View, position: Int)
    }

    class Builder {
        private var hasIcon = false
        private var tabList: ArrayList<TabBean>? = null
        private var mTitleSize: Float = 15f
        private var mTitleColor: Int = 0
        private var mNormColor: Int = 0
        private var mSelectColor: Int = 0
        private var mFormWidth: Float = 0f
        private var mFormColor: Int = 0

        fun setHasIcon(hasIcon: Boolean): Builder {
            this.hasIcon = hasIcon
            return this
        }

        fun getHasIcon(): Boolean {
            return hasIcon
        }

        fun setTabList(tabList: ArrayList<TabBean>?): Builder {
            this.tabList = tabList
            return this
        }

        fun getTabList(): ArrayList<TabBean> = tabList ?: ArrayList()

        fun setTitleSize(sp: Float): Builder {
            this.mTitleSize = sp
            return this
        }

        fun getTitleSize(): Float = mTitleSize

        fun setTitleColor(titleColor: Int): Builder {
            this.mTitleColor = titleColor
            return this
        }

        fun getTitleColor(): Int = mTitleColor

        fun setNormColor(normColor: Int): Builder {
            this.mNormColor = normColor
            return this
        }

        fun getNormColor(): Int = mNormColor

        fun setSelectColor(selectColor: Int): Builder {
            this.mSelectColor = selectColor
            return this
        }

        fun getSelectColor(): Int = mSelectColor

        fun setFormWidth(formWidth: Float): Builder {
            this.mFormWidth = formWidth
            return this
        }

        fun getFormWidth(): Float = mFormWidth

        fun setFormColor(formColor: Int): Builder {
            this.mFormColor = formColor
            return this
        }

        fun getFormColor(): Int = mFormColor

        fun build(context: Context): TabBarGroup = TabBarGroup(this, context, null)
    }

    class TabBean {
        var mTitle: String? = null
        var mNormIcon: Drawable? = null
        var mSelectIcon: Drawable? = null
    }

}