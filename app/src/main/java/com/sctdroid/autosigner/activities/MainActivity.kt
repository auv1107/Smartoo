package com.sctdroid.autosigner.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sctdroid.autosigner.R.layout

/**
 * Created by lixindong on 1/19/16.
 */
open class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)
    }
}