package com.sctdroid.autosigner.views.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sctdroid.autosigner.views.StatusItem;
import com.sctdroid.autosigner.views.StatusItem_;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

import java.util.Collections;
import java.util.Comparator;

/**
 * Created by lixindong on 1/26/16.
 */
@EBean
public class StatusAdapter extends BaseAdapter {
    @RootContext
    Context context;

    StatusList mList;

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.statusList.size();
    }

    @Override
    public Status getItem(int i) {
        return mList == null || mList.statusList.size() <= i ? null : mList.statusList.get(i);
    }

    public StatusItem.OnImageClickListener mListener;

    public void setOnImageClickListener(StatusItem.OnImageClickListener listener) {
        mListener = listener;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        StatusItem item;
        if (view == null) {
            item = StatusItem_.build(context);
        } else {
            item = (StatusItem_) view;
        }
        item.bind(getItem(i));
        item.setOnImageClickListener(mListener);
        return item;
    }

    @UiThread
    public void update(StatusList list) {
        mList = list;
        Collections.sort(mList.statusList, new Comparator<Status>() {
            @Override
            public int compare(Status o, Status t1) {
                return Long.parseLong(o.id) > Long.parseLong(t1.id) ? 1 : 0;
            }
        });
        notifyDataSetChanged();
    }

    @UiThread
    public void append(StatusList list) {
        mList.statusList.addAll(list.statusList);
        mList.next_cursor = list.next_cursor;
        Collections.sort(mList.statusList, new Comparator<Status>() {
            @Override
            public int compare(Status o, Status t1) {
                return Long.parseLong(o.id) > Long.parseLong(t1.id) ? 1 : 0;
            }
        });
        notifyDataSetChanged();
    }
}
