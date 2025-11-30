package com.android.support

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.ShapeDrawable
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.core.view.setPadding
import androidx.core.graphics.drawable.toDrawable


const val ICON_SIZE = 120
const val POS_X = 50
const val POS_Y = 100

var MENU_WIDTH = 290
var MENU_HEIGHT = 210
var MENU_CORNER = 4f
val MENU_BG_COLOR = "#EE1C2A35".toColorInt()//#AARRGGBB
val MENU_FEATURE_BG_COLOR = "#DD141C22".toColorInt() //#AARRGGBB

val TEXT_COLOR = "#82CAFD".toColorInt()
val TEXT_COLOR_2 = "#FFFFFF".toColorInt()

val CATEGORY_BG_COLOR = "#2F3D4C".toColorInt()

class Menu(val context: Context) {

    val rootFrame: FrameLayout = FrameLayout(context)
    val menuContainer: LinearLayout = LinearLayout(context)
    val startImage = ImageView(context)

    val onTouchListener = object : OnTouchListener {
        var dx: Float = 0f
        var dy: Float = 0f
        var isClicked = true
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    var params = rootFrame.layoutParams as WindowManager.LayoutParams
                    dx = params.x - event.rawX
                    dy = params.y - event.rawY
                    isClicked = true
                }

                MotionEvent.ACTION_MOVE -> {
                    isClicked = false
                    rootFrame.alpha = 0.5f
                    var params = rootFrame.layoutParams as WindowManager.LayoutParams
                    params.x = (event.rawX + dx).toInt()
                    params.y = (event.rawY + dy).toInt()
                    val windowManager = (context as Activity).windowManager
                    var bounds = windowManager.currentWindowMetrics.bounds
                    if (params.x < POS_X) params.x = POS_X
                    else if (params.x > bounds.right - rootFrame.width - POS_X) {
                        params.x = bounds.right - rootFrame.width - POS_X
                    }
                    if (params.y < POS_Y) params.y = POS_Y
                    else if (params.y > bounds.bottom - rootFrame.height - POS_Y) {
                        params.y = bounds.bottom - rootFrame.height - POS_Y
                    }
                    windowManager.updateViewLayout(rootFrame, params)
                }

                MotionEvent.ACTION_UP -> {
                    rootFrame.alpha = 1f
                    if (isClicked) {
                        v.performClick()
                    }
                }

                else -> return false
            }
            return true
        }
    }

    init {
        initRootFrame()
        initStartImage()
        initRootContainer()
    }

    fun initRootFrame() {
        rootFrame.setOnTouchListener(onTouchListener)
        val frameParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            POS_X,  //initialX
            POS_Y,  //initialY
            WindowManager.LayoutParams.TYPE_APPLICATION,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_SPLIT_TOUCH,
            PixelFormat.TRANSPARENT
        )
        frameParams.gravity = Gravity.TOP or Gravity.LEFT
        (context as Activity).windowManager.addView(rootFrame, frameParams)
    }

    fun initStartImage() {
        startImage.layoutParams = RelativeLayout.LayoutParams(
            ICON_SIZE,
            ICON_SIZE,
        )
        startImage.scaleType = ImageView.ScaleType.FIT_XY
        startImage.setImageDrawable(Color.RED.toDrawable())
        startImage.setOnTouchListener(onTouchListener)
        startImage.setOnClickListener {
            startImage.visibility = View.GONE
            menuContainer.visibility = View.VISIBLE
        }

        rootFrame.addView(startImage)
    }

    fun initRootContainer() {
        menuContainer.visibility = View.GONE
        menuContainer.setBackgroundColor(MENU_BG_COLOR)
        menuContainer.orientation = LinearLayout.VERTICAL

        menuContainer.setLayoutParams(
            LinearLayout.LayoutParams(
                dp(MENU_WIDTH),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val head = LinearLayout(context)
        head.orientation = LinearLayout.VERTICAL
        head.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val divider = ShapeDrawable()
        divider.intrinsicHeight = 10
        divider.alpha = 0
        head.setDividerDrawable(divider)
        head.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
        head.setPadding(0, 20, 0, 20)
        menuContainer.addView(head)

        //********** Title **********
        val title = TextView(context)
        title.setTextColor(TEXT_COLOR)
        title.text = "Mod by Typh00n"
        title.textSize = 18.0f
        title.typeface = Typeface.DEFAULT_BOLD
        title.setGravity(Gravity.CENTER)
        title.layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        head.addView(title)


        //********** Sub title **********
        val subTitle = TextView(context)
        subTitle.text = "whatever here"
        subTitle.ellipsize = TextUtils.TruncateAt.MARQUEE
        subTitle.marqueeRepeatLimit = -1
        subTitle.setSingleLine(true)
        subTitle.setSelected(true)
        subTitle.setTextColor(TEXT_COLOR)
        subTitle.textSize = 12.0f
        subTitle.setGravity(Gravity.CENTER)
        head.addView(subTitle)

        menuContainer.addView(generateFeatureList())

        val actionBar = LinearLayout(context)
        actionBar.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        actionBar.gravity = Gravity.RIGHT

        val closeBtn = Button(context)
        closeBtn.text = "CLOSE"
        closeBtn.setBackgroundColor(Color.TRANSPARENT)
        closeBtn.setTextColor(TEXT_COLOR)
        closeBtn.setOnClickListener {
            menuContainer.visibility = View.GONE
            startImage.visibility = View.VISIBLE
        }
        closeBtn.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        actionBar.addView(closeBtn)

        menuContainer.addView(actionBar)
        rootFrame.addView(menuContainer)
    }

    fun generateFeatureList(): View {
        //********** Mod menu feature list **********
        val scrollView = ScrollView(context)

        //Auto size. To set size manually, change the width and height example 500, 500
        scrollView.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(MENU_HEIGHT)
            )
        scrollView.setBackgroundColor(MENU_FEATURE_BG_COLOR)
        val mods = LinearLayout(context)
        mods.orientation = LinearLayout.VERTICAL
        val divider = ShapeDrawable()
        divider.intrinsicHeight = 10
        divider.alpha = 0
        mods.setDividerDrawable(divider)
        mods.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
        val featureList = getFeatureList()
        if (featureList != null) {
            for ((idx, feat) in featureList.withIndex()) {
                feat.trim().split(":").let { it ->
                    when (it[0].lowercase()) {
                        "category" -> mods.addView(this.category(it[1]))
                        "toggle" -> {
                            var defaultValue = false
                            if (it.size >= 3) {
                                defaultValue = it[2].toBoolean()
                            }
                            mods.addView(this.toggle(idx, it[1], defaultValue))

                        }

                        "seekbar" -> {
                            var min = 1
                            var max = 10
                            var defaultValue = min
                            if (it.size >= 3) {
                                it[2].split("_").let {
                                    if (it.size >= 3) {
                                        min = it[0].toInt()
                                        max = it[1].toInt()
                                        defaultValue = it[2].toInt()
                                    } else if (it.size == 2) {
                                        min = it[0].toInt()
                                        max = it[1].toInt()
                                        defaultValue = min
                                    } else if (it.size == 1) {
                                        min = it[0].toInt()
                                        defaultValue = min
                                    }
                                }

                            }
                            mods.addView(this.seekbar(idx, it[1], min, max, defaultValue))
                        }
                    }

                }
            }
        }

        scrollView.addView(mods)
        return scrollView
    }

    external fun getFeatureList(): Array<String>?
    external fun valueChange(featIdx: Int, value: Any)


    private fun category(name: String): View {
        val textView = TextView(context)
        textView.setBackgroundColor(CATEGORY_BG_COLOR)
        textView.text = name
        textView.setGravity(Gravity.CENTER)
        textView.setTextColor(TEXT_COLOR_2)
        textView.typeface = Typeface.DEFAULT_BOLD
        textView.setPadding(10)
        return textView
    }

    private fun toggle(featIdx: Int, featName: String, defaultValue: Boolean = false): View {
        val switch = Switch(context)
        switch.text = featName
        switch.isChecked = defaultValue
        switch.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        switch.setTextColor(TEXT_COLOR_2)
        switch.setPadding(20)
        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            valueChange(featIdx, isChecked)
        }
        return switch
    }

    private fun seekbar(
        featIdx: Int,
        featName: String,
        min: Int,
        max: Int,
        defaultValue: Int = min
    ): View {
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(20)
        val divider = ShapeDrawable()
        divider.intrinsicHeight = 20
        divider.alpha = 0
        linearLayout.setDividerDrawable(divider)
        linearLayout.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE

        val head = LinearLayout(context)
        head.orientation = LinearLayout.HORIZONTAL
        head.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val name = TextView(context)
        name.text = featName
        name.setTextColor(TEXT_COLOR_2)
        name.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1.0f
        )
        head.addView(name)

        val value = TextView(context)
        value.setTextColor(TEXT_COLOR)
        value.text = defaultValue.toString()
        value.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        value.minWidth = 100
        head.addView(value)

        linearLayout.addView(head)

        val seekbar = SeekBar(context)
        seekbar.min = min
        seekbar.max = max
        seekbar.progress = defaultValue
        seekbar.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                value.text = progress.toString()
                valueChange(featIdx, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        linearLayout.addView(seekbar)
        return linearLayout
    }


    private fun dp(i: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            i.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }
}



