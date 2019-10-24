package com.antiless.support.fragment.list;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.ViewGroup;

/**
 * Created by lixindong on 2018/8/11.
 */

/** business about layout **/
public abstract class RecyclerViewLayoutAdapter<T> {
    public abstract RecyclerView.LayoutManager getLayoutManager(Context context);

    public abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindViewHolder(RecyclerView.ViewHolder holder, T item);

    public void initRecyclerView(RecyclerView recyclerView) {}

    public void onDataAppended(RecyclerView recyclerView) {}

    public void onDataUpdated(RecyclerView recyclerView) {}

    public void onItemClick(RecyclerView recyclerView, RecyclerView.ViewHolder vh, T data) {}
    public void onItemLongClick(RecyclerView recyclerView, RecyclerView.ViewHolder vh, T data) {}

    public boolean onContextItemSelected(MenuItem item) {
        return false;
    }

    public int getItemViewType(int position, T item) {
        return 0;
    }
}
