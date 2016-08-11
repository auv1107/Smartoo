package com.sctdroid.autosigner.presentation.ui.base;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.RelativeLayout;

import com.sctdroid.autosigner.presentation.ui.ItemView;

/**
 * Created by lixindong on 6/23/16.
 */
public class BaseItemView extends RelativeLayout implements ItemView {
    protected SparseArray<View> mViews;
    public BaseItemView(Context context) {
        this(context, null);
    }

    public BaseItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public void init() {
        mViews = new SparseArray<View>();
    }
    public  <T extends View> T findViewByID(int viewId){
        View view = mViews.get(viewId);
        if (view == null) {
            view = super.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T)view;
    }

    @Override
    public void bind(Object object) {

    }
}
