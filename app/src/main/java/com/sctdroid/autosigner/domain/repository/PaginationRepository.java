package com.sctdroid.autosigner.domain.repository;

import android.os.Bundle;

import com.sctdroid.autosigner.domain.model.Model;

import java.util.List;

/**
 * Created by lixindong on 6/28/16.
 */
public interface PaginationRepository {
    interface OnGetListener {
        void onGet(final List<Model> list, Bundle bundle);
        void onException(Exception e);
    }
    void get(Bundle bundle, OnGetListener listener);
}
