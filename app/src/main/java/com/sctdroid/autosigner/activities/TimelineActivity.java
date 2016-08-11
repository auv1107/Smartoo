package com.sctdroid.autosigner.activities;

import android.app.Activity;

import com.sctdroid.autosigner.R;
import com.sctdroid.autosigner.fragments.TimelineFragment;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;

/**
 * Created by lixindong on 1/26/16.
 */
@EActivity(R.layout.activity_timeline)
public class TimelineActivity extends Activity {
    @FragmentById(R.id.fragment)
    TimelineFragment fragment;
}
