package com.sctdroid.autosigner.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.sctdroid.autosigner.R;
import com.sctdroid.autosigner.activities.GalleryActivity;
import com.sctdroid.autosigner.views.adapter.GalleryViewPagerAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

/**
 * Created by lixindong on 1/27/16.
 */
@EFragment(R.layout.fragment_gallery)
public class GalleryFragment extends Fragment {
    int position;
    ArrayList<String> imageUrls;

    @ViewById(R.id.pic_view_pager)
    ViewPager mViewPager;

    @ViewById(R.id.indicator)
    TextView mIndicator;

    @Bean(GalleryViewPagerAdapter.class)
    GalleryViewPagerAdapter pagerAdapter;

    void obtainDataFromIntent(Intent intent) {
        position = intent.getIntExtra(GalleryActivity.POSITION, 0);
        imageUrls = intent.getStringArrayListExtra(GalleryActivity.IMAGES);
    }

    @AfterViews
    void init() {
        obtainDataFromIntent(getActivity().getIntent());
        initViewPager();
    }

    void initViewPager() {
        mViewPager.setAdapter(pagerAdapter);
        pagerAdapter.update(imageUrls);
        mViewPager.setCurrentItem(position);
        setIndicatorText((position+1) + "/" + pagerAdapter.getCount());
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setIndicatorText((position+1) + "/" + pagerAdapter.getCount());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @UiThread
    void setIndicatorText(String text) {
        mIndicator.setText(text);
    }
}
