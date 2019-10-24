package com.sctdroid.autosigner.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.sctdroid.autosigner.R;

import org.androidannotations.annotations.EActivity;

import java.util.ArrayList;

/**
 * Created by lixindong on 1/27/16.
 */
@EActivity(R.layout.activity_gallery)
public class GalleryActivity extends AppCompatActivity {
    public static final String IMAGES = "images";
    public static final String POSITION = "position";

    public static void viewPictures(Context context, int position, ArrayList<String> picUrls) {
        Intent intent = new Intent(context, GalleryActivity_.class);
        intent.putStringArrayListExtra(IMAGES, picUrls);
        intent.putExtra(POSITION, position);
        context.startActivity(intent);
    }
}
