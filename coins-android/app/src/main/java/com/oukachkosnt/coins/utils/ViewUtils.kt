package com.oukachkosnt.coins.utils

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.abs

fun TextInputEditText.addTextChangedListener(consumer: (String) -> Unit) {
    addTextChangedListener(
        object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (hasFocus()) {
                    consumer(s?.toString() ?: "")
                }
            }
        }
    )
}

@SuppressLint("ClickableViewAccessibility")
fun View.interceptHorizontalTouchGesturesFor(
    guardedView: View,
    onGestureStarted: () -> Unit,
    onGestureFinished: () -> Unit
) {
    val viewConfig = ViewConfiguration.get(guardedView.context)
    val touchSlop = viewConfig.scaledTouchSlop
    var startXPos = 0f

    setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startXPos = event.x
            }

            MotionEvent.ACTION_MOVE -> {
                if (abs(startXPos - event.x) > touchSlop) {
                    // User scrolled in horizontal direction.
                    // Treat this as intention to interact with guarded view.
                    onGestureStarted()
                }
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                onGestureFinished()
            }
        }

        guardedView.onTouchEvent(event)

        true
    }
}