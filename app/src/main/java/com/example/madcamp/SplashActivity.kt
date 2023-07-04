package com.example.madcamp

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DELAY: Long = 3000 // 3초
    private val ANIMATION_DELAY: Long = 500 // 1.5초

    private lateinit var imageViewLeft: ImageView
    private lateinit var imageViewRight: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        imageViewLeft = findViewById(R.id.imageViewLeft)
        imageViewRight = findViewById(R.id.imageViewRight)

        // image2를 숨김
        imageViewRight.visibility = View.INVISIBLE

        animateImageViewFromLeft(imageViewLeft, 0)
        Handler().postDelayed({
            imageViewRight.visibility = View.VISIBLE
            animateImageViewFromRight(imageViewRight, 0)
        }, ANIMATION_DELAY)

        Handler().postDelayed({
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_DELAY)
    }

    private fun animateImageViewFromLeft(imageView: ImageView, delay: Long) {
        val translationAnimator = ObjectAnimator.ofFloat(imageView, "translationX", -2000f, 0f)
        translationAnimator.duration = 1000
        translationAnimator.startDelay = delay
        translationAnimator.interpolator = AccelerateDecelerateInterpolator()
        translationAnimator.start()
    }

    private fun animateImageViewFromRight(imageView: ImageView, delay: Long) {
        val translationAnimator = ObjectAnimator.ofFloat(imageView, "translationX", 2000f, 0f)
        translationAnimator.duration = 1000
        translationAnimator.startDelay = delay
        translationAnimator.interpolator = AccelerateDecelerateInterpolator()
        translationAnimator.start()
    }
}