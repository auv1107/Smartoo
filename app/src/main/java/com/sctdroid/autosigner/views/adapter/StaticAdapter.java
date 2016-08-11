package com.sctdroid.autosigner.views.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sctdroid.autosigner.models.Record;
import com.sctdroid.autosigner.views.StaticItem_;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixindong on 1/19/16.
 */
@EBean
public class StaticAdapter extends BaseAdapter {
    List<Record> mList;

    @RootContext
    Context context;

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Record getItem(int i) {
        return mList == null ? null : mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = StaticItem_.build(context);
        }
        ((StaticItem_)view).bind(getItem(i));
        return view;
    }

    @UiThread
    public void update(List<Record> list) {
        if (mList == null) {
            mList = new ArrayList<>();
        } else {
            mList.clear();
        }
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @UiThread
    public void append(List<Record> list) {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        mList.addAll(list);
        notifyDataSetChanged();
    }

}
