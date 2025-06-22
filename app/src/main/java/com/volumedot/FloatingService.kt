package com.volumedot

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.content.ContextCompat

class FloatingService : Service() {
    private lateinit var wm: WindowManager
    private lateinit var bubble: ImageView

    override fun onCreate() {
        super.onCreate()
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        bubble = ImageView(this).apply {
            setImageDrawable(ContextCompat.getDrawable(this@FloatingService, R.drawable.ic_dot))
            alpha = 1f
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100; y = 300
        }

        wm.addView(bubble, params)

        bubble.setOnClickListener {
            val audio = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audio.adjustVolume(AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI)
            bubble.alpha = 1f
            resetFadeOut()
        }

        resetFadeOut()
    }

    private fun resetFadeOut() {
        Handler(Looper.getMainLooper()).removeCallbacksAndMessages(null)
        Handler(Looper.getMainLooper()).postDelayed({
            bubble.animate().alpha(0.3f).duration = 500
        }, 3000)
    }

    override fun onDestroy() {
        wm.removeView(bubble)
        super.onDestroy()
    }
    override fun onBind(intent: Intent?) = null
}
