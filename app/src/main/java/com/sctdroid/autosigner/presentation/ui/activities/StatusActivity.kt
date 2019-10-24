package com.sctdroid.autosigner.presentation.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.sctdroid.autosigner.R
import com.sctdroid.autosigner.fragments.StatusFragment
import com.sina.weibo.sdk.openapi.models.Status
import org.parceler.Parcels

/**
 * Created by lixindong on 6/28/16.
 */
class StatusActivity : AppCompatActivity() {
    companion object {
        const val KEY_STATUS = "key_status"
        public fun start(activity: Activity, status: Status) {
            val pStatus = Parcels.wrap(status)
            val intent = Intent(activity, StatusActivity::class.java).apply {
                putExtra(KEY_STATUS, pStatus)
            }
            return activity.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

        val pStatus = intent?.getParcelableExtra<Parcelable>(KEY_STATUS)
        val status = Parcels.unwrap<Status>(pStatus)
        val fragment = StatusFragment.newInstance(status)

        supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commitAllowingStateLoss()
    }
}