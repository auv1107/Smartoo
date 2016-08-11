package com.sctdroid.autosigner.presentation.ui.views;

import android.content.Context;
import android.util.AttributeSet;

import com.sctdroid.autosigner.domain.interactors.PaginationInteractor;
import com.sctdroid.autosigner.domain.model.Model;
import com.sctdroid.autosigner.presentation.adapters.PaginationAdapter;
import com.sctdroid.pullToRefresh.PullToRefreshListView;

import java.util.List;

/**
 * Created by lixindong on 6/28/16.
 */
public class PaginationListview extends PullToRefreshListView implements PaginationInteractor.Callback{
    public PaginationListview(Context context, AttributeSet attrs) {
        super(context, attrs);
        setRefreshListener(new RefreshListener() {
            @Override
            public void onPullDownToRefresh() {
                if (mInteractor != null) {
                    mInteractor.readFirstPage();
                }
            }

            @Override
            public void onPullUpTpRefresh() {
                if (mInteractor != null) {
                    mInteractor.readNextPage();
                }
            }

            @Override
            public void onPullUpToLoad() {
                if (mInteractor != null) {
                    mInteractor.readNextPage();
                }
            }
        });
        enablePullUp();
    }
    protected PaginationInteractor mInteractor;

    public void setInteractor(PaginationInteractor interactor) {
        mInteractor = interactor;
        mInteractor.setCallback(this);
    }
    PaginationAdapter mPaginationAdapter;

    public void setAdapter(PaginationAdapter adapter) {
        super.setAdapter(adapter);
        mPaginationAdapter = adapter;
        if (mInteractor != null) {
            mInteractor.readFirstPage();
        }
    }

    @Override
    public void onReadNextPage(List<Model> models, boolean isLastPage) {
        if (mPaginationAdapter != null) {
            mPaginationAdapter.append(models);
        }
        if (isLastPage) {
            hideLoadMore();
        } else {
            showLoadMore();
        }
    }

    @Override
    public void onReadPreviousPage(List<Model> models) {

    }

    @Override
    public void onReadFirstPage(List<Model> models, boolean isLastPage) {
        if (mPaginationAdapter != null) {
            mPaginationAdapter.update(models);
        }
        finishRefreshing(PullToRefreshListView.PULL_DOWN);
        if (isLastPage) {
            hideLoadMore();
        } else {
            showLoadMore();
        }
    }

    @Override
    public void onReadPage(List<Model> models) {

    }

    @Override
    public void onException(Exception e) {
        PaginationAdapter adapter = (PaginationAdapter) getRefreshableView().getAdapter();
        if (adapter != null) {
            adapter.update(null);
        }
    }
}
