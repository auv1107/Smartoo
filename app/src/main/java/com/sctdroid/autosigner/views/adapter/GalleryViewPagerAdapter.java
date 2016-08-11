package com.sctdroid.autosigner.views.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.sctdroid.autosigner.views.GalleryPagerItem;
import com.sctdroid.autosigner.views.GalleryPagerItem_;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.List;

/**
 * Created by lixindong on 1/27/16.
 */
@EBean
public class GalleryViewPagerAdapter extends PagerAdapter {
    private List<String> mList;

    @RootContext
    Context context;

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public String getItem(int position) {
        return mList == null || mList.size() <= position ? "" : mList.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        GalleryPagerItem item = GalleryPagerItem_.build(context);
        item.bind(getItem(position));
        container.addView(item);
        return item;
    }

    public void update(List list) {
        mList = list;
        notifyDataSetChanged();
    }
}
