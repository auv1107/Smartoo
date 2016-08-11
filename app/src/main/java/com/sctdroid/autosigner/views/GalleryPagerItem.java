package com.sctdroid.autosigner.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.sctdroid.autosigner.R;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

/**
 * Created by lixindong on 1/27/16.
 */
@EViewGroup(R.layout.item_pager_gallery)
public class GalleryPagerItem extends RelativeLayout {
    private static final String TAG = GalleryPagerItem.class.getSimpleName();

    public GalleryPagerItem(Context context) {
        super(context);
    }

    @ViewById(R.id.gallery)
    ImageView gallery;

    @ViewById(R.id.iamge_not_found)
    TextView hint;

    @ViewById(R.id.slide_focus_image_progress)
    ProgressBar pb_slide_focus_image_progress;


    @UiThread
    public void bind(String url) {
        if (!TextUtils.isEmpty(url)) {
            Log.d(TAG, "bind " + url);
            Glide.with(getContext())
                    .load(url)
                    .into(new GlideDrawableImageViewTarget(gallery) {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable>
                                animation) {
                            super.onResourceReady(resource, animation);
                            pb_slide_focus_image_progress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            pb_slide_focus_image_progress.setVisibility(GONE);
                        }
                    });
        } else {
            hint.setVisibility(View.VISIBLE);
            gallery.setVisibility(View.GONE);
        }
    }
}
