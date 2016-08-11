package com.sctdroid.pullToRefresh;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by huanghanhui on 14-8-28.
 */
public class RefreshHeaderView extends LinearLayout{
    private int mClipBottom = 0;

    public RefreshHeaderView(Context context) {
        super(context);
    }

    public RefreshHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.clipRect(0, 0, this.getMeasuredWidth(), mClipBottom);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    public void setClipBottom(int bottom) {
        mClipBottom = bottom;
        invalidate();
    }

    public int getClipBottom(){
        return mClipBottom;
    }

    public interface RefreshHeaderListener {
        void headerClipBottomChanged(int bottom);
    }
}
