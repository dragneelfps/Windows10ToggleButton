package com.nooblabs.windows10togglebutton

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

class WinToggleButton: View, View.OnClickListener {

    private val minWidth: Int = 200
    private val minHeight: Int = minWidth/3

    @ColorInt private  var blackColor = resources.getColor(android.R.color.black)
    @ColorInt private var whiteColor = resources.getColor(android.R.color.white)
    @ColorInt private var blueColor = resources.getColor(android.R.color.holo_blue_light)

    private var strokeWidth = 20f

    private var onState = false

    init {
        setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        onState = !onState
        invalidate()
        mStateChangedListener?.onToggle(onState)
    }

    private var mStateChangedListener: StateChanedListener? = null

    fun setStateChangedListener(listener: StateChanedListener?){
        mStateChangedListener = listener
    }

    interface StateChanedListener{
        fun onToggle(isOn: Boolean)
    }

    constructor(context: Context): super(context)

    constructor(context: Context, attributeSet: AttributeSet): this(context,attributeSet, 0)

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int): super(context,attributeSet,defStyle)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val lW: Int
        val lH: Int
        val specWidth = MeasureSpec.getSize(widthMeasureSpec)

        lW = when(MeasureSpec.getMode(widthMeasureSpec)){
            MeasureSpec.UNSPECIFIED -> {
                specWidth
            }
            MeasureSpec.EXACTLY -> {
                max(specWidth, minWidth)
            }
            MeasureSpec.AT_MOST -> {
                minWidth
            }
            else -> {
                specWidth
            }
        }
        val specHeight = MeasureSpec.getSize(heightMeasureSpec)
        if(specHeight < specWidth){
            lH = when(MeasureSpec.getMode(widthMeasureSpec)){
                MeasureSpec.UNSPECIFIED -> {
                    specHeight
                }
                MeasureSpec.EXACTLY -> {
                    max(specHeight, minHeight)
                }
                MeasureSpec.AT_MOST -> {
                    minHeight
                }
                else -> {
                    specHeight
                }
            }
        }else {
            lH = lW/4
        }
        setMeasuredDimension(lW, lH)
    }



    override fun onDraw(canvas: Canvas?) {
        canvas?.let { cv ->
            if(!onState){
                drawBorder(cv)
                drawSwitch(cv)
            }else{
                drawBackground(cv)
                drawOnSwitch(cv)
            }
        }

    }

    private fun drawBackground(canvas: Canvas){
        val w = canvas.width
        val h = canvas.height
        val radius = h/2
        //Draw middle
        var brush = getBrush(blueColor, true, strokeWidth)
        canvas.drawRect(RectF(radius.toFloat(), strokeWidth/4, (w - radius).toFloat(), h - strokeWidth/4), brush)
        //Draw left and right background
        brush = getBrush(blueColor, true, strokeWidth/2)
        brush.style = Paint.Style.FILL_AND_STROKE
        canvas.drawArc(RectF(strokeWidth/4, strokeWidth/4, radius*2f + strokeWidth/2, h - strokeWidth/4), 90f, 180f,false,  brush)
        canvas.drawArc(RectF((w-radius*2 - strokeWidth/2), strokeWidth/4, w - strokeWidth/4, (h - strokeWidth/4)), -90f, 180f, false, brush)
    }

    private fun drawOnSwitch(canvas: Canvas){
        val w = canvas.width
        val h = canvas.height
        val radius = h/2  - strokeWidth/2 - strokeWidth/4
        canvas.drawCircle(w - h/2f, h - h/2f, radius, getBrush(whiteColor, true, 0f))
    }

    private fun drawBorder(canvas: Canvas){
        val w = canvas.width
        val h = canvas.height
        val radius = h/2
        //Draw Top and bottom border
        var brush = getBrush(blackColor, false, strokeWidth)
        canvas.drawLine(radius.toFloat(), 0f, (w - radius).toFloat(), 0f, brush )
        canvas.drawLine(radius.toFloat(), h.toFloat(), (w - radius).toFloat(), h.toFloat(), brush)
        //Draw left and right border
        brush = getBrush(blackColor, false, strokeWidth/2)
        canvas.drawArc(RectF(strokeWidth/4, strokeWidth/4, radius*2f + strokeWidth/2, h - strokeWidth/4), 90f, 180f,false,  brush)
        canvas.drawArc(RectF((w-radius*2 - strokeWidth/2), strokeWidth/4, w - strokeWidth/4, (h - strokeWidth/4)), -90f, 180f, false, brush)
    }

    private fun drawSwitch(canvas: Canvas){
        val h = canvas.height
        val radius = h/2  - strokeWidth/2 - strokeWidth/4
        canvas.drawCircle(h/2f, h/2f, radius, getBrush(blackColor, true, 0f))
    }

    private fun getBrush(@ColorInt color : Int, fill: Boolean, strokeWidth: Float): Paint{
        val p = Paint()
        p.isAntiAlias = true
        if(!fill) {
            p.style = Paint.Style.STROKE
            p.strokeWidth = strokeWidth
        }
        p.color = color
        return p
    }

    /**
     * Saving state of view with BaseSavedState Pattern
     */

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(): Parcelable {
//        Log.d("debug","onSave Called")
        return onSaveInstanceStateStandard()
    }

    @SuppressLint("MissingSuperCall")
    override fun onRestoreInstanceState(state: Parcelable?) {
        onRestoreInstanceStateStandard(state)
    }

    private fun onSaveInstanceStateStandard(): Parcelable{
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.isOn = if(onState) 1 else 0
        return ss
    }

    private fun onRestoreInstanceStateStandard(source: Parcelable?){
        if(source !is SavedState){
            super.onRestoreInstanceState(source)
            return
        }
        super.onRestoreInstanceState(source.superState)
        onState = source.isOn == 1
        invalidate()
    }

    companion object {
        class SavedState: BaseSavedState{

            var isOn: Int = 0

            constructor(superState: Parcelable) : super(superState)
            constructor(p: Parcel?) : super(p){
                isOn = p?.readInt() ?: 0
            }

            override fun writeToParcel(out: Parcel?, flags: Int) {
                super.writeToParcel(out, flags)
                out?.writeInt(isOn)
            }

            override fun toString(): String {
                return "TestView: isOn = $isOn"
            }
            companion object {
                val CREATOR = object : Parcelable.Creator<SavedState> {
                    override fun createFromParcel(source: Parcel?): SavedState {
                        return SavedState(source)
                    }

                    override fun newArray(size: Int): Array<SavedState?> {
                        return arrayOfNulls<SavedState?>(size)
                    }
                }
            }
        }
    }
}