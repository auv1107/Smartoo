package com.sctdroid.autosigner.presentation.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sctdroid.autosigner.domain.model.Model;
import com.sctdroid.autosigner.presentation.ui.base.BaseItemView;
import com.sctdroid.autosigner.presentation.ui.items.DefaultItemView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixindong on 6/22/16.
 */
public class ListAdapter extends BaseAdapter {
    List<Model> mData;
    Class mItemViewClass;
    Constructor mConstructor;
    Context mContext;

    public ListAdapter(Context context, Class itemViewClass) {
        super();
        mItemViewClass = itemViewClass;
        try {
            mConstructor = mItemViewClass.getDeclaredConstructor(new Class[] {Context.class});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        mContext = context;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Model getItem(int i) {
        return (mData == null || mData.size() <= i) ? null : mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        Model model = getItem(i);
        return model == null ? -1 : model.getItemId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        BaseItemView item = null;
        if (view == null) {
            try {
                item = (BaseItemView) mConstructor.newInstance(mContext);
            } catch (InstantiationException e) {
                e.printStackTrace();
                item = new DefaultItemView(mContext);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                item = new DefaultItemView(mContext);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                item = new DefaultItemView(mContext);
            }
        } else {
            item = (BaseItemView) view;
        }
        item.bind(getItem(i).getObject());
        return item;
    }

    public void update(List<Model> data) {
        mData = data;
        notifyDataSetChanged();
    }
    public void append(List<Model> data) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.addAll(data);
        notifyDataSetChanged();
    }
}
