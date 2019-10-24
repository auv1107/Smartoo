package com.antiless.support.fragment.list;

import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.antiless.support.R;
import com.antiless.support.widget.SwipeLayout;

import java.util.ArrayList;
import java.util.List;

import static com.antiless.support.utils.PreconditionsKt.checkNotNull;

/**
 * Created by lixindong on 2018/8/7.
 */

public class RecyclerViewFragment<T> extends Fragment implements RecyclerViewContract.View<T> {
    private RecyclerView mRecyclerView;
    private ContentAdapter mAdapter;
    protected RecyclerViewContract.Presenter mPresenter;
    private SwipeLayout mSwipeRefreshLayout;

    protected TextView mEmptyView;

    private RecyclerViewLayoutAdapter<T> mRecyclerViewLayoutAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkNotNull(mRecyclerViewLayoutAdapter, "You must specified a recyclerViewLayoutAdapter!");

        findViews(view);
        initRecyclerView();
        mPresenter.start();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return mRecyclerViewLayoutAdapter.onContextItemSelected(item);
    }

    public @LayoutRes int getLayoutId() {
        return R.layout.layout_recycler_view;
    }

    private void initRecyclerView() {
        mAdapter = new ContentAdapter();
        mRecyclerView.setAdapter(mAdapter);
        final RecyclerView.LayoutManager layoutManager = mRecyclerViewLayoutAdapter.getLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.refresh();
            }
        });
        mSwipeRefreshLayout.setSwipeContent(new SwipeLayout.LayoutManagerSwipeContent(layoutManager));
        mSwipeRefreshLayout.setOnLoadListener(new SwipeLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                if (mPresenter.hasMore()) {
                    mPresenter.loadMore();
                }
            }
        });
        mRecyclerView.addOnItemTouchListener(new OnItemTouchListener(mRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                T data = mAdapter.getItem(vh.getAdapterPosition());
                mRecyclerViewLayoutAdapter.onItemClick(mRecyclerView, vh, data);
            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder vh) {
                T data = mAdapter.getItem(vh.getAdapterPosition());
                mRecyclerViewLayoutAdapter.onItemLongClick(mRecyclerView, vh, data);
            }
        });
        mRecyclerViewLayoutAdapter.initRecyclerView(mRecyclerView);
    }

    private void findViews(View view) {
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        mEmptyView = view.findViewById(R.id.emptyView);
    }

    @Override
    public void setPresenter(RecyclerViewContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setRecyclerViewLayoutAdapter(RecyclerViewLayoutAdapter<T> recyclerViewLayoutAdapter) {
        mRecyclerViewLayoutAdapter = recyclerViewLayoutAdapter;
    }

    /**
     *  ====================================
     *  custom classes
     *  ====================================
     **/

    public class ContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final List<T> mData = new ArrayList<>();

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return mRecyclerViewLayoutAdapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            mRecyclerViewLayoutAdapter.onBindViewHolder(holder, getItem(position));
        }

        @Override
        public int getItemViewType(int position) {
            return mRecyclerViewLayoutAdapter.getItemViewType(position, getItem(position));
        }

        public T getItem(int position) {
            return mData.get(position);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public void appendData(List<T> data) {
            int oldCount = getItemCount();
            mData.addAll(data);
            notifyItemRangeInserted(oldCount, data.size());
        }

        public void updateData(List<T> data) {
            // fixme rewrite it here by DiffCallback
            mData.clear();
            if (data == null || data.isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mData.addAll(data);
                mRecyclerView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
            }
            notifyDataSetChanged();
        }
    }

    /**
     *  ====================================
     *  View interfaces
     *  ====================================
     **/

    @Override
    public void appendData(List<T> data) {
        mAdapter.appendData(data);
        if (mSwipeRefreshLayout.isLoading()) {
            mSwipeRefreshLayout.setLoading(false);
        }
        mRecyclerViewLayoutAdapter.onDataAppended(mRecyclerView);
    }

    @Override
    public void updateData(List<T> data) {
        mAdapter.updateData(data);
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        mRecyclerViewLayoutAdapter.onDataUpdated(mRecyclerView);
        mRecyclerView.scrollToPosition(0);
    }

    @Override
    public T getItem(int position) {
        return position < mAdapter.getItemCount() ? mAdapter.getItem(position) : null;
    }

    @Override
    public boolean getUserVisible() {
        return isFragmentUserVisible(this);
    }

    @Override
    public View getEmptyView() {
        return mEmptyView;
    }

    @Override
    public void setEmptyContent(String text) {
        mEmptyView.setText(text);
    }

    private boolean isFragmentUserVisible(Fragment fragment) {
        boolean visible = fragment.getUserVisibleHint();
        if (fragment.getParentFragment() != null) {
            visible = visible && isFragmentUserVisible(fragment.getParentFragment());
        }
        return visible;
    }
}
