package com.sctdroid.autosigner.fragments;

import android.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.sctdroid.autosigner.R;
import com.sctdroid.autosigner.activities.GalleryActivity;
import com.sctdroid.autosigner.presentation.ui.activities.StatusActivity;
import com.sctdroid.autosigner.utils.AccessTokenKeeper;
import com.sctdroid.autosigner.utils.Constants;
import com.sctdroid.autosigner.views.StatusItem;
import com.sctdroid.autosigner.views.adapter.StatusAdapter;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sctdroid.pullToRefresh.PullToRefreshBaseView;
import com.sctdroid.pullToRefresh.PullToRefreshListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.IgnoreWhen;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

/**
 * Created by lixindong on 1/20/16.
 */
@EFragment(R.layout.fragment_timeline)
public class TimelineFragment extends Fragment {

    private static final String TAG = TimelineFragment.class.getSimpleName();
    @ViewById(R.id.listview)
    PullToRefreshListView listView;

    @Bean(StatusAdapter.class)
    StatusAdapter adapter;

    StatusList statusList;

    Oauth2AccessToken mAccessToken;
    StatusesAPI mStatusesAPI;

    public final int SINCE_ID = 0;
    public final int MAX_ID = 0;
    public final int REQUEST_COUNT = 10;
    public final int FIRST_PAGE = 1;
    private int currentPage = 1;
    private String sinceId = "";
    private String maxId = "";

    @AfterViews
    void init() {
        mAccessToken = AccessTokenKeeper.readAccessToken(getActivity());
        if (mAccessToken.isSessionValid()) {
            initListView();
            mStatusesAPI = new StatusesAPI(getActivity(), Constants.APP_KEY, mAccessToken);
            fetchTimeline(REQUEST_COUNT, SINCE_ID);
        } else {
            Toast.makeText(getActivity(), "Please login first.", Toast.LENGTH_LONG).show();
            getActivity().finish();
        }
    }

    private void initListView() {
        adapter.setOnImageClickListener(new StatusItem.OnImageClickListener() {
            @Override
            public void onImageClicked(int position, ArrayList<String> urls) {
                GalleryActivity.viewPictures(getActivity(), position, urls);
            }
        });
        listView.setAdapter(adapter);
        listView.showLoadMore();
        listView.setRefreshListener(new PullToRefreshBaseView.RefreshListener() {
            @Override
            public void onPullDownToRefresh() {
                fetchTimeline(REQUEST_COUNT, MAX_ID);
            }

            @Override
            public void onPullUpTpRefresh() {
            }

            @Override
            public void onPullUpToLoad() {
                fetchTimeline(REQUEST_COUNT, Long.parseLong(maxId));
            }
        });
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (position > 0) {
                Status status = adapter.getItem(position - 1);
                StatusActivity.Companion.start(getActivity(), status);
            }
        });
    }

    @Background
    @IgnoreWhen(IgnoreWhen.State.DETACHED)
    void fetchTimeline(int count, long max_id) {
        String statuses = mStatusesAPI.friendsTimelineSync(SINCE_ID, max_id, count, FIRST_PAGE, false, StatusesAPI.FEATURE_ORIGINAL, false);
        Log.d(TAG, statuses);
        statusList = StatusList.parse(statuses);
        maxId = statusList.next_cursor;
        if (max_id == 0) {
            adapter.update(statusList);
        } else {
            adapter.append(statusList);
        }
        hideRefreshView(max_id);
    }

    @UiThread
    void hideRefreshView(long max_id) {
        if (max_id == MAX_ID) {
            listView.finishRefreshing(PullToRefreshListView.PULL_DOWN);
        }
    }
}
