package com.sctdroid.autosigner.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sctdroid.autosigner.R;
import com.sctdroid.autosigner.domain.executor.impl.ThreadExecutor;
import com.sctdroid.autosigner.domain.interactors.PaginationInteractor;
import com.sctdroid.autosigner.domain.interactors.impl.PaginationInteractorImpl;
import com.sctdroid.autosigner.domain.model.Model;
import com.sctdroid.autosigner.domain.repository.PaginationRepository;
import com.sctdroid.autosigner.domain.repository.impl.FollowerPaginationRepository;
import com.sctdroid.autosigner.presentation.adapters.ListAdapter;
import com.sctdroid.autosigner.presentation.presenters.ListPresenter;
import com.sctdroid.autosigner.presentation.presenters.impl.ListPresenterImpl;
import com.sctdroid.autosigner.presentation.ui.items.FriendItemView;
import com.sctdroid.autosigner.threading.MainThreadImpl;
import com.sctdroid.autosigner.utils.AccessTokenKeeper;
import com.sctdroid.autosigner.utils.Constants;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sctdroid.pullToRefresh.PullToRefreshBaseView;
import com.sctdroid.pullToRefresh.PullToRefreshListView;

import java.util.List;

/**
 * Created by lixindong on 6/22/16.
 */
public class SimpleListFragment extends Fragment implements ListPresenter.View {
    PullToRefreshListView listView;

    List<Model> mData;
    Oauth2AccessToken mAccessToken;

    ListAdapter listAdapter;

    View mContentView = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(getLayoutResId(), container, false);
        }
        return mContentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onViewChanged(view);
    }

    public void onViewChanged(View rootView) {
        listView = ((PullToRefreshListView) rootView.findViewById(R.id.listview));
        init();
    }

    @Override
    public void onDataChanged(List<Model> data) {
        mData = data;
        listAdapter.update(data);
    }

    @Override
    public void onDataAppended(List<Model> data) {
        if (mData == null) {
            mData = data;
        } else {
            mData.addAll(data);
        }
        listAdapter.update(mData);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showError(String message) {

    }

    ListPresenter mListPresenter;
    void init() {
        mAccessToken = AccessTokenKeeper.readAccessToken(getActivity());
        mListPresenter = new ListPresenterImpl(ThreadExecutor.getInstance(), MainThreadImpl.getInstance(),
                buildInteractor(getActivity(), Constants.APP_KEY, mAccessToken), this);
        listAdapter = new ListAdapter(getActivity(), FriendItemView.class);
        listView.setAdapter(listAdapter);
        listView.showLoadMore();
        listView.setRefreshListener(new PullToRefreshBaseView.RefreshListener() {
            @Override
            public void onPullDownToRefresh() {
                mListPresenter.readFirstPage();
            }

            @Override
            public void onPullUpTpRefresh() {
            }

            @Override
            public void onPullUpToLoad() {
                mListPresenter.readNextPage();
            }
        });
    }

    protected PaginationRepository buildRepository(Context context, String appKey, Oauth2AccessToken accessToken) {
        return new FollowerPaginationRepository(context, appKey, accessToken);
    }
    protected PaginationInteractor buildInteractor(Context context, String appKey, Oauth2AccessToken accessToken) {
        return new PaginationInteractorImpl(null, buildRepository(context, appKey, accessToken));
    }

    protected int getLayoutResId() {
        return R.layout.fragment_simple_list;
    }

    @Override
    public void onResume() {
        super.onResume();
        mListPresenter.resume();
    }

    public void hideRefreshView(int cursor) {
        if (cursor == 0) {
            listView.finishRefreshing(PullToRefreshListView.PULL_DOWN);
        }
    }
}
