package com.sctdroid.autosigner.domain.repository;

import com.sctdroid.autosigner.domain.model.Model;

import java.util.List;

/**
 * Created by lixindong on 6/22/16.
 */
public interface ListRepository {
    interface OnGetListener {
        void onGet(final List<Model> list, int previousCursor, int nextCursor, int total);
        void onException(Exception e);
    }
    void get(int page, int pageSize, OnGetListener listener);
}
