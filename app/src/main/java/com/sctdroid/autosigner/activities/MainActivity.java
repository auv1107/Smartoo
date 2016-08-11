package com.sctdroid.autosigner.activities;

import android.app.Activity;
import android.content.Intent;

import com.sctdroid.autosigner.R;
import com.sctdroid.autosigner.fragments.ProfileFragment;
import com.sctdroid.autosigner.fragments.TimelineFragment;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;

/**
 * Created by lixindong on 1/19/16.
 */
@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

    @FragmentById(R.id.fragment)
    ProfileFragment fragment;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}