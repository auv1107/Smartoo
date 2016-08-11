package com.sctdroid.pullToRefresh.swipeable;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.WrapperListAdapter;

import com.sctdroid.pullToRefresh.pinnedhead.PinnedSectionListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建人： limeng
 * 日期： 10/23/15
 */

/**
 * Adapter for swipe list view.
 * Wrap a list adapter.
 */
public class SwipeListAdapter implements WrapperListAdapter, SwipeMenuView.OnSwipeMenuViewClickListener {

    private SwipeListItemView mOpenedChild;

    private ListAdapter mListAdapter;
    private Context mContext;
    private boolean mItemClickEnable = true;
    private SwipeListView.OnMenuItemClickListener mMenuItemClickListener;

    private boolean isAnimating = false;

    // for menu background
    private Drawable mBgDrawable;

    // for menu container size
    private int mMenuContainerHeight = -1;
    private int mMenuContainerWidth = -1;

    public SwipeListAdapter(Context context, ListAdapter adapter) {
        mContext = context;
        mListAdapter = adapter;
    }

    // Test Code
    public List<View> createMenuViews() {
        List<View> views = new ArrayList<View>();
        int id = 0;

        TextView textView = new TextView(mContext);
        textView.setTextColor(mContext.getResources().getColor(android.R.color.white));
        textView.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_red_light));
        textView.setText("text1");
        textView.setWidth(200);
        textView.setHeight(200);
        views.add(textView);

        TextView textView2 = new TextView(mContext);
        textView2.setTextColor(mContext.getResources().getColor(android.R.color.white));
        textView2.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_blue_light));
        textView2.setText("text2");
        textView2.setWidth(200);
        textView2.setHeight(200);
        views.add(textView2);

//        View view = ((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.test, null);
//        views.add(view);

        return views;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // when pinned head adapter was set, do not add menu view for it.
        if (mListAdapter instanceof PinnedSectionListView.PinnedSectionListAdapter
                && (((PinnedSectionListView.PinnedSectionListAdapter)
                mListAdapter).isItemViewTypePinned(mListAdapter.getItemViewType(position)))) {
            return mListAdapter.getView(position, convertView, parent);
        }
        else {
            SwipeListItemView itemView = (SwipeListItemView) convertView;
            if (itemView == null || (itemView.getTag() instanceof Boolean && ((Boolean) itemView.getTag()))) {
                View contentView = mListAdapter.getView(position, null, parent);

                List<View> views = createMenuViews();
                SwipeMenuView menuView = new SwipeMenuView(views, mMenuContainerWidth, mMenuContainerHeight);
                if (mBgDrawable != null) {
                    menuView.setBackgroundDrawable(mBgDrawable.getConstantState().newDrawable());
                }
                // for menu item click
                menuView.setOnSwipeMenuViewClickListener(this);

                itemView = new SwipeListItemView(contentView, menuView);
                itemView.setPosition(position);

                itemView.setTag(null);
            } else {
                itemView = (SwipeListItemView) convertView;
                itemView.setPosition(position);
                // need to call getView() to get new view
                mListAdapter.getView(position, itemView.getContentView(),
                        parent);
            }
            itemView.setSwipeListAdapter(this);
            return itemView;
        }
    }

    @Override
    public ListAdapter getWrappedAdapter() {
        return mListAdapter;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return mListAdapter.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(int position) {
        return mItemClickEnable && mListAdapter.isEnabled(position);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mListAdapter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mListAdapter.unregisterDataSetObserver(observer);
    }

    @Override
    public int getCount() {
        return mListAdapter.getCount();
    }

    @Override
    public Object getItem(int position) {
        return mListAdapter.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return mListAdapter.getItemId(position);
    }

    @Override
    public boolean hasStableIds() {
        return mListAdapter.hasStableIds();
    }

    @Override
    public int getItemViewType(int position) {
        return mListAdapter.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return mListAdapter.getViewTypeCount();
    }

    @Override
    public boolean isEmpty() {
        return mListAdapter.isEmpty();
    }

    // for menu item click
    @Override
    public void onMenuViewClick(SwipeMenuView view, List<View> menuViews, int index) {
        if (mMenuItemClickListener != null) {
            mMenuItemClickListener.onMenuItemClick(view.getPosition(), menuViews, index);
        }
    }

    public void setMenuItemClickListener(SwipeListView.OnMenuItemClickListener listener) {
        mMenuItemClickListener = listener;
    }

    public void setBgDrawable(Drawable drawable) {
        mBgDrawable = drawable;
    }

    public void setMenuContainerHeight(int value) {
        mMenuContainerHeight = value;
    }
    public void setMenuContainerWidth(int value) {
        mMenuContainerWidth = value;
    }

    public void setOpenedChild(SwipeListItemView view) {
        mOpenedChild = view;
    }

    public SwipeListItemView getOpenedChild() {
        return mOpenedChild;
    }

    public boolean isAdapterAnimating() {
        return isAnimating;
    }

    public void setAdapterAnimating(boolean isAnimating) {
        this.isAnimating = isAnimating;
//        onReadyToNotifyDataSetChanged();
    }
}
