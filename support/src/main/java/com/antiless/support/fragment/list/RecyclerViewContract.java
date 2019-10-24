package com.antiless.support.fragment.list;

import android.view.View;

import com.antiless.support.BasePresenter;
import com.antiless.support.BaseView;

import java.util.List;

/**
 * Created by lixindong on 2018/8/11.
 */

public interface RecyclerViewContract {

    /**
     * business around data
     **/
    interface Presenter extends BasePresenter {

        void refresh();

        void loadMore();

        boolean hasMore();
    }

    interface View<T> extends BaseView<Presenter> {
        void appendData(List<T> data);

        void updateData(List<T> data);

        void setRecyclerViewLayoutAdapter(RecyclerViewLayoutAdapter<T> recyclerViewLayoutAdapter);

        T getItem(int position);

        boolean getUserVisible();

        void setEmptyContent(String text);

        android.view.View getEmptyView();
    }

}
