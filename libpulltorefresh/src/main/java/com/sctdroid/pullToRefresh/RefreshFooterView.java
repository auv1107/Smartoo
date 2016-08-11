package com.sctdroid.pullToRefresh;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 创建人： limeng
 * 日期： 8/20/15
 */
public class RefreshFooterView extends LinearLayout{
    private int mClipTop = 0;

    public RefreshFooterView(Context context) {
        super(context);
    }

    public RefreshFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.clipRect(0, mClipTop, this.getMeasuredWidth(), this.getMeasuredHeight());
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    public void setClipTop(int top) {
        mClipTop = top;
        invalidate();
    }

    public int getClipTop(){
        return mClipTop;
    }

    public interface RefreshFooterListener {
        void footerClipBottomChanged(int bottom);
    }
}
