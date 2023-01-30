package com.android.tgsmf.view.toggleswitch

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.android.tgsmf.R

/*********************************************************************
 *
 *
 *********************************************************************/
class TGSToggleSwitch : View {

    interface OnToggledListener {
        fun onSwitched(toggleableView: TGSToggleSwitch, isOn: Boolean)
    }

    private var isOn = false
    private var mWidth = 0
    private var mHeight = 0

    //private var enabled: Boolean = false


    private var padding = 0
    private var colorOn = 0
    private var colorOff = 0
    private var thumbColorOn = 0
    private var thumbColorOff = 0
    private var colorBorder = 0
    private var colorDisabled = 0
    private var textSize = 0
    private var outerRadii = 0
    private var thumbRadii = 0

    private lateinit var paint: Paint
    private var startTime: Long = 0
    private lateinit var labelOn: String
    private lateinit var labelOff: String
    private lateinit var thumbBounds: RectF
    private lateinit var leftBgArc: RectF
    private lateinit var rightBgArc: RectF
    private lateinit var leftFgArc: RectF
    private lateinit var rightFgArc: RectF

    var mToggledListener: OnToggledListener? = null
        set(_listener) {field = _listener}

    var typeface: Typeface? = null
        set(typeface) {
            field = typeface
            paint.setTypeface(typeface)
            invalidate()
        }
    private var thumbOnCenterX = 0f
    private var thumbOffCenterX = 0f

    //-----------------------------------------------------------------
    constructor(context: Context?) : super(context) {
        initView()
        super.isEnabled()
    }

    //-----------------------------------------------------------------
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
        initProperties(attrs)
    }

    //-----------------------------------------------------------------
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context,attrs,defStyleAttr) {
        initView()
        initProperties(attrs)
    }

    //-----------------------------------------------------------------
    private fun initView() {
        isOn = false
        labelOn = "ON"
        labelOff = "OFF"
        textSize = (12f * resources.displayMetrics.scaledDensity).toInt()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            colorOn = resources.getColor(R.color.colorAccent, context.theme)
            colorBorder = colorOn
        } else {
            colorOn = resources.getColor(R.color.colorAccent)
            colorBorder = colorOn
        }
        paint = Paint()
        paint.setAntiAlias(true)
        leftBgArc = RectF()
        rightBgArc = RectF()
        leftFgArc = RectF()
        rightFgArc = RectF()
        thumbBounds = RectF()
        thumbColorOn = Color.parseColor("#FFFFFF")
        thumbColorOff = Color.parseColor("#FFFFFF")
        colorOff = Color.parseColor("#FFFFFF")
        colorDisabled = Color.parseColor("#D3D3D3")
    }

    //-----------------------------------------------------------------
    private fun initProperties(attrs: AttributeSet?) {
        val tarr = context.theme.obtainStyledAttributes(attrs, R.styleable.TGSToggleSwitch, 0, 0)
        val N = tarr.indexCount
        for (i in 0 until N) {
            val attr = tarr.getIndex(i)
            if (attr == R.styleable.TGSToggleSwitch_on) {
                isOn = tarr.getBoolean(R.styleable.TGSToggleSwitch_on, false)
            } else if (attr == R.styleable.TGSToggleSwitch_colorOff) {
                colorOff = tarr.getColor(R.styleable.TGSToggleSwitch_colorOff, Color.parseColor("#FFFFFF"))
            } else if (attr == R.styleable.TGSToggleSwitch_thumbColorOff) {
                thumbColorOff = tarr.getColor(R.styleable.TGSToggleSwitch_thumbColorOff, Color.parseColor("#FFFFFF"))
            } else if (attr == R.styleable.TGSToggleSwitch_thumbColorOn) {
                thumbColorOn = tarr.getColor(R.styleable.TGSToggleSwitch_thumbColorOn, Color.parseColor("#FFFFFF"))
            } else if (attr == R.styleable.TGSToggleSwitch_colorBorder) {
                var accentColor: Int
                accentColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    resources.getColor(R.color.colorAccent, context.theme)
                } else {
                    resources.getColor(R.color.colorAccent)
                }
                colorBorder = tarr.getColor(R.styleable.TGSToggleSwitch_colorBorder, accentColor)
            } else if (attr == R.styleable.TGSToggleSwitch_colorOn) {
                var accentColor: Int
                accentColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    resources.getColor(R.color.colorAccent, context.theme)
                } else {
                    resources.getColor(R.color.colorAccent)
                }
                colorOn = tarr.getColor(R.styleable.TGSToggleSwitch_colorOn, accentColor)
            } else if (attr == R.styleable.TGSToggleSwitch_colorDisabled) {
                colorDisabled = tarr.getColor(R.styleable.TGSToggleSwitch_colorOff, Color.parseColor("#D3D3D3"))
            } else if (attr == R.styleable.TGSToggleSwitch_textOff) {
                tarr.getString(R.styleable.TGSToggleSwitch_textOff)?.let { labelOff = it }
            } else if (attr == R.styleable.TGSToggleSwitch_textOn) {
                tarr.getString(R.styleable.TGSToggleSwitch_textOn)?.let { labelOn = it }
            } else if (attr == R.styleable.TGSToggleSwitch_android_textSize) {
                val defaultTextSize = (12f * resources.displayMetrics.scaledDensity).toInt()
                textSize = tarr.getDimensionPixelSize(R.styleable.TGSToggleSwitch_android_textSize, defaultTextSize)
            } else if (attr == R.styleable.TGSToggleSwitch_android_enabled) {
                //sun : enabled = tarr.getBoolean(R.styleable.Toggle_android_enabled, false)
            }
        }
    }

    //-----------------------------------------------------------------
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.setTextSize(textSize.toFloat())

//      Drawing Switch background here
        run {
            if (isEnabled) {
                paint.setColor(colorBorder)
            } else {
                paint.setColor(colorDisabled)
            }
            canvas.drawArc(leftBgArc, 90F, 180F, false, paint)
            canvas.drawArc(rightBgArc, 90F, -180F, false, paint)
            canvas.drawRect(outerRadii.toFloat(), 0F, (mWidth - outerRadii).toFloat(), mHeight.toFloat(), paint)
            paint.setColor(colorOff)
            canvas.drawArc(leftFgArc, 90F, 180F, false, paint)
            canvas.drawArc(rightFgArc, 90F, -180F, false, paint)
            canvas.drawRect(
                outerRadii.toFloat(),
                (padding / 10).toFloat(),
                (mWidth - outerRadii).toFloat(),
                (mHeight - padding / 10).toFloat(),
                paint
            )
            var alpha =
                ((thumbBounds!!.centerX() - thumbOffCenterX) / (thumbOnCenterX - thumbOffCenterX) * 255).toInt()
            alpha = if (alpha < 0) 0 else if (alpha > 255) 255 else alpha
            val onColor: Int
            onColor = if (isEnabled) {
                Color.argb(alpha, Color.red(colorOn), Color.green(colorOn), Color.blue(colorOn))
            } else {
                Color.argb(
                    alpha,
                    Color.red(colorDisabled),
                    Color.green(colorDisabled),
                    Color.blue(colorDisabled)
                )
            }
            paint.setColor(onColor)
            canvas.drawArc(leftBgArc, 90F, 180F, false, paint)
            canvas.drawArc(rightBgArc, 90F, -180F, false, paint)
            canvas.drawRect(outerRadii.toFloat(), 0F, (mWidth - outerRadii).toFloat(), mHeight.toFloat(), paint)
            alpha =
                ((thumbOnCenterX - thumbBounds!!.centerX()) / (thumbOnCenterX - thumbOffCenterX) * 255).toInt()
            alpha = if (alpha < 0) 0 else if (alpha > 255) 255 else alpha
            val offColor: Int =
                Color.argb(alpha, Color.red(colorOff), Color.green(colorOff), Color.blue(colorOff))
            paint.setColor(offColor)
            canvas.drawArc(leftFgArc, 90F, 180F, false, paint)
            canvas.drawArc(rightFgArc, 90F, -180F, false, paint)
            canvas.drawRect(
                outerRadii.toFloat(),
                (padding / 10).toFloat(),
                (mWidth - outerRadii).toFloat(),
                (mHeight - padding / 10).toFloat(),
                paint
            )
        }

//      Drawing Switch Labels here
        val MAX_CHAR = "N"
        val textCenter: Float = paint.measureText(MAX_CHAR) / 2
        if (isOn) {
            var alpha =
                (((mWidth ushr 1) - thumbBounds!!.centerX()) / ((mWidth ushr 1) - thumbOffCenterX) * 255).toInt()
            alpha = if (alpha < 0) 0 else if (alpha > 255) 255 else alpha
            val onColor: Int =
                Color.argb(alpha, Color.red(colorOn), Color.green(colorOn), Color.blue(colorOn))
            paint.setColor(onColor)
            var centerX =
                (mWidth - padding - (padding + (padding ushr 1) + (thumbRadii shl 1)) ushr 1).toFloat()
            canvas.drawText(
                labelOff,
                padding + (padding ushr 1) + (thumbRadii shl 1) + centerX - paint.measureText(
                    labelOff
                ) / 2,
                (mHeight ushr 1) + textCenter,
                paint
            )
            alpha =
                ((thumbBounds!!.centerX() - (mWidth ushr 1)) / (thumbOnCenterX - (mWidth ushr 1)) * 255).toInt()
            alpha = if (alpha < 0) 0 else if (alpha > 255) 255 else alpha
            val offColor: Int =
                Color.argb(alpha, Color.red(colorOff), Color.green(colorOff), Color.blue(colorOff))
            paint.setColor(offColor)
            val maxSize = mWidth - (padding shl 1) - (thumbRadii shl 1)
            centerX = ((padding ushr 1) + maxSize - padding ushr 1).toFloat()
            canvas.drawText(
                labelOn,
                padding + centerX - paint.measureText(labelOn) / 2,
                (mHeight ushr 1) + textCenter,
                paint
            )
        } else {
            var alpha =
                ((thumbBounds!!.centerX() - (mWidth ushr 1)) / (thumbOnCenterX - (mWidth ushr 1)) * 255).toInt()
            alpha = if (alpha < 0) 0 else if (alpha > 255) 255 else alpha
            val offColor: Int =
                Color.argb(alpha, Color.red(colorOff), Color.green(colorOff), Color.blue(colorOff))
            paint.setColor(offColor)
            val maxSize = mWidth - (padding shl 1) - (thumbRadii shl 1)
            var centerX = ((padding ushr 1) + maxSize - padding ushr 1).toFloat()
            canvas.drawText(
                labelOn,
                padding + centerX - paint.measureText(labelOn) / 2,
                (mHeight ushr 1) + textCenter,
                paint
            )
            alpha =
                (((mWidth ushr 1) - thumbBounds!!.centerX()) / ((mWidth ushr 1) - thumbOffCenterX) * 255).toInt()
            alpha = if (alpha < 0) 0 else if (alpha > 255) 255 else alpha
            val onColor: Int
            onColor = if (isEnabled) {
                Color.argb(alpha, Color.red(colorOn), Color.green(colorOn), Color.blue(colorOn))
            } else {
                Color.argb(
                    alpha,
                    Color.red(colorDisabled),
                    Color.green(colorDisabled),
                    Color.blue(colorDisabled)
                )
            }
            paint.setColor(onColor)
            centerX =
                (mWidth - padding - (padding + (padding ushr 1) + (thumbRadii shl 1)) ushr 1).toFloat()
            canvas.drawText(
                labelOff,
                padding + (padding ushr 1) + (thumbRadii shl 1) + centerX - paint.measureText(
                    labelOff
                ) / 2,
                (mHeight ushr 1) + textCenter,
                paint
            )
        }

//      Drawing Switch Thumb here
        run {
            var alpha = ((thumbBounds!!.centerX() - thumbOffCenterX) / (thumbOnCenterX - thumbOffCenterX) * 255).toInt()
            alpha = if (alpha < 0) 0 else if (alpha > 255) 255 else alpha
            val onColor: Int = Color.argb(alpha, Color.red(thumbColorOn), Color.green(thumbColorOn), Color.blue(thumbColorOn))
            paint.setColor(onColor)
            canvas.drawCircle(thumbBounds!!.centerX(), thumbBounds!!.centerY(), thumbRadii.toFloat(), paint)

            alpha = ((thumbOnCenterX - thumbBounds!!.centerX()) / (thumbOnCenterX - thumbOffCenterX) * 255).toInt()
            alpha = if (alpha < 0) 0 else if (alpha > 255) 255 else alpha
            val offColor: Int
            offColor =   if (isEnabled) {
                            Color.argb(alpha, Color.red(thumbColorOff), Color.green(thumbColorOff), Color.blue(thumbColorOff))
                        } else {
                            Color.argb(
                                alpha,
                                Color.red(colorDisabled),
                                Color.green(colorDisabled),
                                Color.blue(colorDisabled)
                            )
                        }
            paint.setColor(offColor)
            canvas.drawCircle(thumbBounds!!.centerX(), thumbBounds!!.centerY(), thumbRadii.toFloat(), paint)
        }
    }

    //-----------------------------------------------------------------
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = resources.getDimensionPixelSize(R.dimen.tgs_toggle_switch_labeled_default_width)
        val desiredHeight = resources.getDimensionPixelSize(R.dimen.tgs_toggle_switch_labeled_default_height)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        mWidth = if (widthMode == MeasureSpec.EXACTLY) {
            widthSize
        } else if (widthMode == MeasureSpec.AT_MOST) {
            Math.min(desiredWidth, widthSize)
        } else {
            desiredWidth
        }
        mHeight = if (heightMode == MeasureSpec.EXACTLY) {
            heightSize
        } else if (heightMode == MeasureSpec.AT_MOST) {
            Math.min(desiredHeight, heightSize)
        } else {
            desiredHeight
        }
        setMeasuredDimension(mWidth, mHeight)
        outerRadii = Math.min(mWidth, mHeight) ushr 1
        thumbRadii = (Math.min(mWidth, mHeight) / 2.88f).toInt()
        padding = mHeight - thumbRadii ushr 1
        thumbBounds!![(mWidth - padding - thumbRadii).toFloat(), padding.toFloat(), (mWidth - padding).toFloat()] =
            (mHeight - padding).toFloat()
        thumbOnCenterX = thumbBounds!!.centerX()
        thumbBounds!![padding.toFloat(), padding.toFloat(), (padding + thumbRadii).toFloat()] =
            (mHeight - padding).toFloat()
        thumbOffCenterX = thumbBounds!!.centerX()
        if (isOn) {
            thumbBounds!![(mWidth - padding - thumbRadii).toFloat(), padding.toFloat(), (mWidth - padding).toFloat()] =
                (mHeight - padding).toFloat()
        } else {
            thumbBounds!![padding.toFloat(), padding.toFloat(), (padding + thumbRadii).toFloat()] =
                (mHeight - padding).toFloat()
        }
        leftBgArc!![0f, 0f, (outerRadii shl 1).toFloat()] = mHeight.toFloat()
        rightBgArc!![(mWidth - (outerRadii shl 1)).toFloat(), 0f, mWidth.toFloat()] = mHeight.toFloat()
        leftFgArc!![(padding / 10).toFloat(), (padding / 10).toFloat(), ((outerRadii shl 1) - padding / 10).toFloat()] =
            (mHeight - padding / 10).toFloat()
        rightFgArc!![(mWidth - (outerRadii shl 1) + padding / 10).toFloat(), (padding / 10).toFloat(), (mWidth - padding / 10).toFloat()] =
            (mHeight - padding / 10).toFloat()
    }

    //-----------------------------------------------------------------
    override fun performClick(): Boolean {
        super.performClick()
        if (isOn) {
            val switchColor =
                ValueAnimator.ofFloat((mWidth - padding - thumbRadii).toFloat(), padding.toFloat())
            switchColor.addUpdateListener { animation: ValueAnimator ->
                val value = animation.animatedValue as Float
                thumbBounds!![value, thumbBounds!!.top, value + thumbRadii] = thumbBounds!!.bottom
                invalidate()
            }
            switchColor.interpolator = AccelerateDecelerateInterpolator()
            switchColor.duration = 250
            switchColor.start()
        } else {
            val switchColor = ValueAnimator.ofFloat(
                padding.toFloat(),
                (mWidth - padding - thumbRadii).toFloat()
            )
            switchColor.addUpdateListener { animation: ValueAnimator ->
                val value = animation.animatedValue as Float
                thumbBounds!![value, thumbBounds!!.top, value + thumbRadii] = thumbBounds!!.bottom
                invalidate()
            }
            switchColor.interpolator = AccelerateDecelerateInterpolator()
            switchColor.duration = 250
            switchColor.start()
        }
        isOn = !isOn
        if (mToggledListener != null) {
            mToggledListener!!.onSwitched(this, isOn)
        }
        return true
    }

    //-----------------------------------------------------------------
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (isEnabled) {
            val x = event.x
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startTime = System.currentTimeMillis()
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (x - (thumbRadii ushr 1) > padding && x + (thumbRadii ushr 1) < mWidth - padding) {
                        thumbBounds!![x - (thumbRadii ushr 1), thumbBounds!!.top, x + (thumbRadii ushr 1)] =
                            thumbBounds!!.bottom
                        invalidate()
                    }
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val endTime = System.currentTimeMillis()
                    val span = endTime - startTime
                    if (span < 200) {
                        performClick()
                    } else {
                        if (x >= mWidth ushr 1) {
                            val switchColor = ValueAnimator.ofFloat(
                                (if (x > mWidth - padding - thumbRadii) (mWidth - padding - thumbRadii).toFloat() else x),
                                (mWidth - padding - thumbRadii).toFloat()
                            )
                            switchColor.addUpdateListener { animation: ValueAnimator ->
                                val value =
                                    animation.animatedValue as Float
                                thumbBounds!![value, thumbBounds!!.top, value + thumbRadii] =
                                    thumbBounds!!.bottom
                                invalidate()
                            }
                            switchColor.interpolator = AccelerateDecelerateInterpolator()
                            switchColor.duration = 250
                            switchColor.start()
                            isOn = true
                        } else {
                            val switchColor = ValueAnimator.ofFloat(
                                (if (x < padding) padding.toFloat() else x),
                                padding.toFloat()
                            )
                            switchColor.addUpdateListener { animation: ValueAnimator ->
                                val value = animation.animatedValue as Float
                                thumbBounds!![value, thumbBounds!!.top, value + thumbRadii] = thumbBounds!!.bottom
                                invalidate()
                            }
                            switchColor.interpolator = AccelerateDecelerateInterpolator()
                            switchColor.duration = 250
                            switchColor.start()
                            isOn = false
                        }
                        if (mToggledListener != null) {
                            mToggledListener!!.onSwitched(this, isOn)
                        }
                    }
                    invalidate()
                    true
                }
                else -> {
                    super.onTouchEvent(event)
                }
            }
        } else {
            false
        }
    }

    //-----------------------------------------------------------------
    fun getColorOn(): Int {
        return colorOn
    }
    //-----------------------------------------------------------------
    fun setColorOn(colorOn: Int) {
        this.colorOn = colorOn
        invalidate()
    }

    //-----------------------------------------------------------------
    fun getColorOff(): Int {
        return colorOff
    }
    //-----------------------------------------------------------------
    fun setColorOff(colorOff: Int) {
        this.colorOff = colorOff
        invalidate()
    }
    //-----------------------------------------------------------------
    fun getThumbColorOn(): Int {
        return thumbColorOn
    }
    //-----------------------------------------------------------------
    fun setThumbColorOn(thumbColorOn: Int) {
        this.thumbColorOn = thumbColorOn
        invalidate()
    }
    //-----------------------------------------------------------------
    fun getThumbColorOff(): Int {
        return thumbColorOff
    }
    //-----------------------------------------------------------------
    fun setThumbColorOff(thumbColorOff: Int) {
        this.thumbColorOff = thumbColorOff
        invalidate()
    }

    //-----------------------------------------------------------------
    fun getLabelOn(): String? {
        return labelOn
    }
    //-----------------------------------------------------------------
    fun setLabelOn(labelOn: String) {
        this.labelOn = labelOn
        invalidate()
    }
    //-----------------------------------------------------------------
    fun getLabelOff(): String? {
        return labelOff
    }
    //-----------------------------------------------------------------
    fun setLabelOff(labelOff: String) {
        this.labelOff = labelOff
        invalidate()
    }
    //-----------------------------------------------------------------
    fun setOn(on: Boolean) {
        isOn = on
        if (isOn) {
            thumbBounds!![(mWidth - padding - thumbRadii).toFloat(), padding.toFloat(), (mWidth - padding).toFloat()] =
                (mHeight - padding).toFloat()
        } else {
            thumbBounds!![padding.toFloat(), padding.toFloat(), (padding + thumbRadii).toFloat()] =
                (mHeight - padding).toFloat()
        }
        invalidate()
    }
    //-----------------------------------------------------------------
    fun getColorDisabled(): Int {
        return colorDisabled
    }
    //-----------------------------------------------------------------
    fun setColorDisabled(colorDisabled: Int) {
        this.colorDisabled = colorDisabled
        invalidate()
    }
    //-----------------------------------------------------------------
    fun getColorBorder(): Int {
        return colorBorder
    }

    fun setColorBorder(colorBorder: Int) {
        this.colorBorder = colorBorder
        invalidate()
    }
    //-----------------------------------------------------------------
    fun getTextSize(): Int {
        return textSize
    }
    //-----------------------------------------------------------------
    fun setTextSize(textSize: Int) {
        this.textSize = (textSize * resources.displayMetrics.scaledDensity).toInt()
        invalidate()
    }
}