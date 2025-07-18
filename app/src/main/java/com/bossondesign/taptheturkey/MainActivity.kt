package com.bossondesign.taptheturkey

import android.media.MediaPlayer
import android.os.Bundle
//import android.support.v7.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val turkey = findViewById<ImageView>(R.id.turkey) as ImageView
        val mp = MediaPlayer.create(this, R.raw.gobble)

        //privacy policy text link
        val textView = findViewById<TextView>(R.id.textPrivacy)
        textView.movementMethod = LinkMovementMethod.getInstance()

        turkey.setOnClickListener {
            
            //handles animation
            turkey.startAnimation(TranslateAnimation(0f, 20f, 0f, 20f).apply {
                duration = 10
                repeatCount = 20

                //plays sound
                onPause()
                mp!!.start()

            })

        }

    }
}
