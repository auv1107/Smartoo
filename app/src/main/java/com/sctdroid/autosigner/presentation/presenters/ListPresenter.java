package com.sctdroid.autosigner.presentation.presenters;

import com.sctdroid.autosigner.domain.model.Model;
import com.sctdroid.autosigner.presentation.presenters.base.BasePresenter;
import com.sctdroid.autosigner.presentation.ui.BaseView;

import java.util.List;

/**
 * Created by lixindong on 6/22/16.
 */
public interface ListPresenter extends BasePresenter {
    interface View extends BaseView {
        void onDataChanged(List<Model> data);
        void onDataAppended(List<Model> data);
        void hideRefreshView(int cursor);
    }
    void readPage(int cursor, int count);
    void readNextPage();
    void readFirstPage();
}
