package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var loadingWidth = 0f
    private var loadingAngle = 0f
    private var buttonText = ""
    private var buttonBackgroundColor = 0
    private var buttonTextColor = 0
    private var loadingCircleColor = 0
    private var loadingBarColor = 0

    private var buttonAnimator = ValueAnimator()
    private var loadingCircleAnimator = ValueAnimator()

    private val paintButton = Paint()
    private val paintLoadingBar = Paint()
    private val paintLoadingCircle = Paint()
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 40f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("", Typeface.BOLD)
        isAntiAlias = true
    }

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                buttonText = context.getString(R.string.button_loading_text)
                // button animation set up
                buttonAnimator = ValueAnimator.ofFloat(0f, measuredWidth.toFloat())
                        .apply {
                            duration = 2000
                            //repeatMode = ValueAnimator.RESTART
                            addUpdateListener {
                                loadingWidth = animatedValue as Float
                                invalidate()
                            }
                            start()
                        }
                // circle animation set up
                loadingCircleAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
                    duration = 3000
                    repeatMode = ValueAnimator.RESTART
                    repeatCount = ValueAnimator.INFINITE
                    interpolator = AccelerateInterpolator(1f)
                    addUpdateListener {
                        loadingAngle = animatedValue as Float
                        invalidate()
                    }
                    start()
                }
            }
            ButtonState.Completed -> {
                //reset to initial values
                buttonText = context.getString(R.string.button_initial_text)
                loadingWidth = 0f
                loadingAngle = 0f
                buttonAnimator.end()
                loadingCircleAnimator.end()
            }
        }
    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true
        when (buttonState) {
            buttonState -> ButtonState.Loading
            else -> ButtonState.Completed
        }
        invalidate()
        return true
    }

    init {
        isClickable = true
        buttonState = ButtonState.Initial
        buttonText = context.getString(R.string.button_initial_text)

        context.theme.obtainStyledAttributes(attrs, R.styleable.LoadingButton, 0, 0).apply {
            buttonBackgroundColor = getColor(R.styleable.LoadingButton_backgroundColor, 0)
            buttonTextColor = getColor(R.styleable.LoadingButton_textColor, 0)
            loadingCircleColor = getColor(R.styleable.LoadingButton_loadingCircleColor, 0)
            loadingBarColor = getColor(R.styleable.LoadingButton_loadingBarColor, 0)

        }

        paintButton.color = buttonBackgroundColor
        paintText.color = buttonTextColor
        paintLoadingCircle.color = loadingCircleColor
        paintLoadingBar.color = loadingBarColor
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        when (buttonState) {
            ButtonState.Loading -> {
                paintLoadingBar.color = loadingBarColor
                canvas?.drawRect(0f, 0f, loadingWidth, measuredHeight.toFloat(), paintLoadingBar)
                canvas?.drawArc(
                        measuredWidth - 150f,
                        (measuredHeight / 2) - 35f,
                        measuredWidth - 75f,
                        (measuredHeight / 2) + 35f,
                        0f, loadingAngle, true, paintLoadingCircle

                )
                drawText(canvas)
            }

            else -> {
                paintButton.color = buttonBackgroundColor
                canvas?.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), paintButton)
                drawText(canvas)
            }
        }
    }

    private fun drawText(canvas: Canvas?) {
        canvas?.drawText(
                buttonText,
                measuredWidth.toFloat() / 2,
                measuredHeight / 1.7f,
                paintText
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minW: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minW, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
                MeasureSpec.getSize(w),
                heightMeasureSpec,
                0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }
}