package com.zhumj.mytab

import android.content.Context
import android.util.AttributeSet
import android.widget.RadioButton
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.StaticLayout

/**
 * @author Created by zhumj
 * @date on 2017/9/5
 * @function 底部菜单
 */
class TabBarItem
@JvmOverloads constructor(build: Builder? = null, context: Context?, attrs: AttributeSet?)
    : RadioButton(context, attrs) {

    //画背景颜色的画笔
    private val mBackgroundColorPaint = Paint()
    //画默认背景图片的画笔
    private val mBackgroundNormIconPaint = Paint()
    //画选中背景图片的画笔
    private val mBackgroundSelectIconPaint = Paint()
    //画边框的画笔
    private val mBackgroundFormPaint = Paint()
    //画文字的画笔
    private val mTextPaint = Paint()
    //默认背景颜色
    private var mNormColor: Int = 0
    private var mSelectColor: Int = 0
    //文字长宽
    private var mTextWidth: Float = 0F
    private var mTextHeight: Float = 0F
    //边框宽度和颜色
    private var mFormWidth: Float = 0F
    private var mFormColor: Int = 0
    //默认背景图片
    private var mNormDrawable: Drawable? = null
    private var mBackgroundBitmap: Bitmap? = null
    //选中中的背景图片
    private var mSelectingBackgroundBitmap: Bitmap? = null
    //选中时的背景图片
    private var mSelectDrawable: Drawable? = null
    private var mSelectBackgroundBitmap: Bitmap? = null
    //背景图片长宽
    private var iconWidth: Int = 0
    private var iconHeight: Int = 0
    private var iconPadding: Int = 0
    //背景图Rect
    private var mIconRect: Rect? = null
    //背景图Canvas
    private var mIconCanvas: Canvas? = null

    //是否拥有背景图（为true的时候为仿微信首页下边菜单栏样式）
    private var hasIcon = false

    private var mAlpha: Int = 0

    init {

        if (build == null) {
            val ta = context!!.obtainStyledAttributes(attrs, R.styleable.TabBarItem)
            mNormColor = ta.getColor(R.styleable.TabBarItem_norm_color, Color.WHITE)
            mSelectColor = ta.getColor(R.styleable.TabBarItem_select_color, Color.BLACK)
            mFormWidth = ta.getDimension(R.styleable.TabBarItem_form_width, 3F)
            mFormColor = ta.getColor(R.styleable.TabBarItem_form_color, Color.BLACK)
            mNormDrawable = ta.getDrawable(R.styleable.TabBarItem_icon_norm)
            mSelectDrawable = ta.getDrawable(R.styleable.TabBarItem_icon_select)
            hasIcon = ta.getBoolean(R.styleable.TabBarItem_hasIcon, false)
            ta.recycle()
        } else {
            mNormColor = build.getNormColor()
            mSelectColor = build.getSelectColor()
            mFormWidth = build.getFormWidth()
            mFormColor = build.getFormColor()
            mNormDrawable = build.getNormDrawable()
            mSelectDrawable = build.getSelectDrawable()
            hasIcon = build.getHasIcon()
            text = build.getTitle()
        }

        if (hasIcon) {
            buttonDrawable = null
            if (mNormDrawable != null) {
                setCompoundDrawablesWithIntrinsicBounds(null, mNormDrawable, null, null)
            }
            mNormDrawable = compoundDrawables[1]

            mBackgroundBitmap = getBitmapFromDrawable(mNormDrawable)
            mSelectBackgroundBitmap = getBitmapFromDrawable(mSelectDrawable)

            mIconRect = mNormDrawable!!.bounds
            iconWidth = mIconRect!!.width()
            iconHeight = mIconRect!!.height()
            iconPadding = compoundDrawablePadding

            mSelectingBackgroundBitmap = Bitmap.createBitmap(iconWidth, iconHeight, Bitmap.Config.ARGB_8888)
            mIconCanvas?.drawColor(Color.TRANSPARENT)
            mIconCanvas = Canvas(mSelectingBackgroundBitmap)
        }

        val fontMetrics = paint.fontMetrics
        mTextHeight = Math.ceil((fontMetrics.descent - fontMetrics.ascent).toDouble()).toFloat()
        mTextWidth = StaticLayout.getDesiredWidth(text, paint)

        if (isChecked) {
            mAlpha = 255
        }
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        } else {
            throw RuntimeException("The Drawable must be an instance of BitmapDrawable")
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (hasIcon) {
            setMeasuredDimension(widthMeasureSpec, (iconHeight + paddingTop + paddingBottom + compoundDrawablePadding + mTextHeight + 3).toInt())
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (hasIcon) {
            drawBackgroundIcon(canvas)
            drawBackgroundSelectIcon(canvas)
        } else {
            drawBackgroundColor(canvas)
            drawBackgroundSelectedColor(canvas)
            drawBackgroundForm(canvas)
        }
        drawText(canvas)
        drawTargetText(canvas)
    }

    //画默认背景图
    private fun drawBackgroundIcon(canvas: Canvas) {
        mBackgroundNormIconPaint.alpha = 255 - mAlpha
        mBackgroundNormIconPaint.isAntiAlias = true
        canvas.drawBitmap(mBackgroundBitmap, ((width - iconWidth) / 2).toFloat(), iconPadding.toFloat(), mBackgroundNormIconPaint)
    }

    //画选中背景图
    private fun drawBackgroundSelectIcon(canvas: Canvas) {
        if (mAlpha/255f >= 0.9) {
            mBackgroundNormIconPaint.alpha = mAlpha
            mBackgroundNormIconPaint.isAntiAlias = true
            canvas.drawBitmap(mSelectBackgroundBitmap, ((width - iconWidth) / 2).toFloat(), iconPadding.toFloat(), mBackgroundNormIconPaint)
        } else {
            mNormDrawable!!.draw(mIconCanvas)
            mBackgroundSelectIconPaint.color = mSelectColor
            mBackgroundSelectIconPaint.alpha = mAlpha
            mBackgroundSelectIconPaint.isAntiAlias = true
            mBackgroundSelectIconPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            mIconCanvas!!.drawRect(mIconRect, mBackgroundSelectIconPaint)
            canvas.drawBitmap(mSelectingBackgroundBitmap, ((width - iconWidth) / 2).toFloat(), paddingTop.toFloat() + iconPadding.toFloat(), null)
        }
    }

    //画默认的背景色
    private fun drawBackgroundColor(canvas: Canvas) {
        mBackgroundColorPaint.color = mNormColor
        mBackgroundColorPaint.isAntiAlias = true
        mBackgroundColorPaint.alpha = 255 - mAlpha
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), mBackgroundColorPaint)
    }

    //画选中的背景色
    private fun drawBackgroundSelectedColor(canvas: Canvas) {
        mBackgroundColorPaint.color = mSelectColor
        mBackgroundColorPaint.isAntiAlias = true
        mBackgroundColorPaint.alpha = mAlpha
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), mBackgroundColorPaint)
    }

    //画表格
    private fun drawBackgroundForm(canvas: Canvas){
        mBackgroundFormPaint.color = mFormColor
        mBackgroundFormPaint.style = Paint.Style.STROKE
        mBackgroundFormPaint.isAntiAlias = true
        mBackgroundFormPaint.strokeWidth = mFormWidth
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), mBackgroundFormPaint)
    }

    //画文字
    private fun drawText(canvas: Canvas) {
        mTextPaint.color = currentTextColor
        mTextPaint.isAntiAlias = true
        mTextPaint.textSize = textSize
        if (hasIcon) {
            mTextPaint.alpha = 255 - mAlpha
            canvas.drawText(text.toString(), width / 2 - mTextWidth / 2, iconHeight + iconPadding + paddingTop + mTextHeight, mTextPaint)
        } else {
            canvas.drawText(text.toString(), width / 2 - mTextWidth / 2, height / 2F + mTextHeight / 4, mTextPaint)
        }
    }

    private fun drawTargetText(canvas: Canvas) {
        if (hasIcon) {
            mTextPaint.color = mSelectColor
            mTextPaint.isAntiAlias = true
            mTextPaint.alpha = mAlpha
            canvas.drawText(text.toString(), width / 2 - mTextWidth / 2, iconHeight + iconPadding + paddingTop + mTextHeight, mTextPaint)
        }
    }

    fun updateAlpha(alpha: Float) {
        mAlpha = alpha.toInt()
        invalidate()
    }

    fun setRadioButtonChecked(isChecked: Boolean) {
        setChecked(isChecked)
        mAlpha = if (isChecked) 255 else 0
        invalidate()
    }

    class Builder(private val context: Context) {
        private var hasIcon = false
        private var mTitle: String = ""
        private var mNormColor: Int = Color.WHITE
        private var mSelectColor: Int = Color.BLACK
        private var mNormDrawable: Drawable? = null
        private var mSelectDrawable: Drawable? = null
        private var mFormWidth: Float = 3F
        private var mFormColor: Int = Color.BLACK

        fun setHasIcon(hasIcon: Boolean): Builder {
            this.hasIcon = hasIcon
            return this
        }

        fun getHasIcon(): Boolean {
            return hasIcon
        }

        fun setTitle(title: String): Builder {
            this.mTitle = title
            return this
        }

        fun getTitle(): String {
            return mTitle
        }

        fun setNormColor(normColor: Int): Builder {
            this.mNormColor = normColor
            return this
        }

        fun getNormColor(): Int {
            return mNormColor
        }

        fun setSelectColor(selectColor: Int): Builder {
            this.mSelectColor = selectColor
            return this
        }

        fun getSelectColor(): Int {
            return mSelectColor
        }

        fun setNormDrawable(normDrawable: Drawable?): Builder {
            this.mNormDrawable = normDrawable
            return this
        }

        fun getNormDrawable(): Drawable? {
            return mNormDrawable
        }

        fun setSelectDrawable(selectDrawable: Drawable?): Builder {
            this.mSelectDrawable = selectDrawable
            return this
        }

        fun getSelectDrawable(): Drawable? {
            return mSelectDrawable
        }

        fun setFormWidth(formWidth: Float): Builder {
            this.mFormWidth = formWidth
            return this
        }

        fun getFormWidth(): Float {
            return mFormWidth
        }

        fun setFormColor(formColor: Int): Builder {
            this.mFormColor = formColor
            return this
        }

        fun getFormColor(): Int {
            return mFormColor
        }

        fun build(): TabBarItem {
            return TabBarItem(this, context, null)
        }
    }

}