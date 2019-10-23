package com.sctdroid.autosigner.fragments;

import android.app.Fragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sctdroid.autosigner.R;
import com.sctdroid.autosigner.presentation.ui.views.PaginationListview;

/**
 * Created by lixindong on 6/28/16.
 */
public class StatusFragment extends Fragment {
    View mContentView = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_status, container, false);
        }
        return mContentView;
    }

    PaginationListview mListview;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListview = (PaginationListview) mContentView.findViewById(R.id.listview);
//        mListview.setInteractor(new PaginationInteractorImpl(null,
//                new FollowerPaginationRepository(getActivity(), Constants.APP_KEY, AccessTokenKeeper.readAccessToken(getActivity()))));
//        mListview.showLoadMore();
//        mListview.setAdapter(new PaginationAdapter(getActivity(), FriendItemView.class));
    }
}
