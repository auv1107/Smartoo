package com.sctdroid.autosigner.presentation.presenters.impl;

import com.sctdroid.autosigner.domain.executor.Executor;
import com.sctdroid.autosigner.domain.executor.MainThread;
import com.sctdroid.autosigner.domain.interactors.PaginationInteractor;
import com.sctdroid.autosigner.domain.model.Model;
import com.sctdroid.autosigner.presentation.presenters.ListPresenter;
import com.sctdroid.autosigner.presentation.presenters.base.AbstractPresenter;

import java.util.List;

/**
 * Created by lixindong on 6/22/16.
 */
public class ListPresenterImpl extends AbstractPresenter implements ListPresenter, PaginationInteractor.Callback {
    PaginationInteractor mInteractor;
    View mView;
    public ListPresenterImpl(Executor executor, MainThread mainThread, PaginationInteractor interactor, View view) {
        super(executor, mainThread);
        mInteractor = interactor;
        mInteractor.setCallback(this);
        mView = view;
    }

    @Override
    public void resume() {
        readFirstPage();
    }

    @Override
    public void readNextPage() {
        mInteractor.readNextPage();
    }

    @Override
    public void readFirstPage() {
        mInteractor.readFirstPage();
    }

    @Override
    public void readPage(final int cursor, int count) {
        mInteractor.readPage(cursor);
    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void onReadNextPage(List<Model> models, boolean isLastPage) {
        mView.onDataAppended(models);
    }

    @Override
    public void onReadPreviousPage(List<Model> models) {

    }

    @Override
    public void onReadFirstPage(List<Model> models, boolean isLastPage) {
        mView.onDataChanged(models);
        mView.hideRefreshView(0);
    }

    @Override
    public void onReadPage(List<Model> models) {
        mView.onDataChanged(models);
    }

    @Override
    public void onException(Exception e) {
        mView.showError(e.getMessage());
    }
}
