package com.sctdroid.pullToRefresh.swipeable;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.sctdroid.pullToRefresh.DensityUtil;

import java.util.List;

/**
 * 创建人： limeng
 * 日期： 10/23/15
 */

/**
 * This view is used to create the menus.
 * Menus can be seen when user swipes any item of the swipe list to left.
 */
public class SwipeMenuView extends LinearLayout {

    private List<View> mMenuViews;
    private Context mContext;
    private int mPosition;
    private OnSwipeMenuViewClickListener mSwipeMenuViewClickListener;

    private SwipeListItemView mSwipeListItemView;

    private LinearLayout mMenuContainer;
    /**
     * Menu container height and width, in px.
     */
    private int mMenuContainerHeight = -1;
    private int mMenuContainerWidth = -1;

    public SwipeMenuView(Context context) {
        super(context);
    }

    public SwipeMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeMenuView(@NonNull List<View> views) {
        super(views.get(0).getContext());
        mContext = views.get(0).getContext();
        mMenuViews = views;

        init();
    }

    public SwipeMenuView(@NonNull List<View> views, int width, int height) {
        super(views.get(0).getContext());
        mContext = views.get(0).getContext();
        mMenuViews = views;
        mMenuContainerWidth = width;
        mMenuContainerHeight = height;
        init();
    }

    private void init() {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        mMenuContainer = new LinearLayout(mContext);
        mMenuContainer.setOrientation(LinearLayout.HORIZONTAL);
        mMenuContainer.setGravity(Gravity.CENTER);
        LayoutParams containerParams = new LayoutParams(
                mMenuContainerWidth >= 0 ? DensityUtil.dp2px(mContext, mMenuContainerWidth) : LayoutParams.WRAP_CONTENT,
                mMenuContainerHeight >= 0 ? DensityUtil.dp2px(mContext, mMenuContainerHeight) : LayoutParams.WRAP_CONTENT);
        mMenuContainer.setLayoutParams(containerParams);

        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        int index = 0;
        for (View view : mMenuViews) {
            final int temp = index++;
            view.setLayoutParams(params);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // when item is open and click on menu item.
                    if (mSwipeListItemView != null && !mSwipeListItemView.isClosed()) {
                        mSwipeMenuViewClickListener.onMenuViewClick(SwipeMenuView.this, mMenuViews, temp);
                    }
                }
            });
            view.setClickable(false);
            mMenuContainer.addView(view);
        }

        addView(mMenuContainer);
    }

    /**
     * get the right position of the last menu item.
     * @return int
     */
    public int getLastMenuRightEdge() {
//        if (mMenuViews.isEmpty()) {
//            return -1;
//        }
//        return mMenuViews.get(mMenuViews.size()-1).getRight();
        return mMenuContainer.getRight();
    }
    /**
     * get the top position of the first menu item.
     * @return int
     */
    public int getMenuItemTopEdge() {
//        if (mMenuViews.isEmpty()) {
//            return -1;
//        }
//        return mMenuViews.get(0).getTop();
        return mMenuContainer.getTop();
    }
    /**
     * get the bottom position of the first menu item.
     * @return int
     */
    public int getMenuItemBottomEdge() {
//        if (mMenuViews.isEmpty()) {
//            return -1;
//        }
//        return mMenuViews.get(0).getBottom();
        return mMenuContainer.getBottom();
    }

    public void setSwipeListItemView(SwipeListItemView itemView) {
        mSwipeListItemView = itemView;
    }

    public void setPosition(int position) {
        mPosition = position;
    }
    public int getPosition() {
        return mPosition;
    }

    /**
     * Menu view can only be clicked when open.
     * @param flag: whether it's open
     */
    public void setMenuClickable(boolean flag) {
        for (View view : mMenuViews) {
            view.setClickable(flag);
        }
    }

    public interface OnSwipeMenuViewClickListener {
        void onMenuViewClick(SwipeMenuView view, List<View> menuViews, int index);
    }

    public void setOnSwipeMenuViewClickListener(OnSwipeMenuViewClickListener listener) {
        mSwipeMenuViewClickListener = listener;
    }

    /**
     * @param value, in dp
     */
    public void  setMenuContainerHeight(int value) {
        mMenuContainerHeight = value;
    }
    public void setMenuContainerWidth(int value) {
        mMenuContainerWidth = value;
    }

    public List<View> getMenuViews() {
        return mMenuViews;
    }
}
