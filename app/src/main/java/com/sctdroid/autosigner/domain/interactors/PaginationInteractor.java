package com.sctdroid.autosigner.domain.interactors;

import com.sctdroid.autosigner.domain.model.Model;

import java.util.List;

/**
 * Created by lixindong on 6/28/16.
 */
public interface PaginationInteractor {
    interface Callback {
        void onReadNextPage(List<Model> models, boolean isLastPage);
        void onReadPreviousPage(List<Model> models);
        void onReadFirstPage(List<Model> models, boolean isLastPage);
        void onReadPage(List<Model> models);
        void onException(Exception e);
    }

    void readNextPage();
    void readPreviousPage();
    void readPage(int page);
    void readFirstPage();
    void setCallback(Callback callback);
}
