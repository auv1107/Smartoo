package com.sctdroid.autosigner.domain.interactors.impl;

import android.os.Bundle;

import com.sctdroid.autosigner.domain.executor.MainThread;
import com.sctdroid.autosigner.domain.interactors.PaginationInteractor;
import com.sctdroid.autosigner.domain.interactors.base.AbstractPaginationInteractor;
import com.sctdroid.autosigner.domain.model.Model;
import com.sctdroid.autosigner.domain.repository.PaginationRepository;

import java.util.List;

/**
 * Created by lixindong on 6/28/16.
 */
public class PaginationInteractorImpl extends AbstractPaginationInteractor implements PaginationInteractor{
    public final int COUNT = 50;
    public final int FIRST_PAGE = 0;
    Bundle mBundle;
    Callback mCallback;

    public PaginationInteractorImpl(MainThread mainThread, PaginationRepository repository) {
        this(mainThread, repository, null);
    }
    public PaginationInteractorImpl(MainThread mainThread, PaginationRepository repository, Callback callback) {
        super(mainThread);
        mBundle = new Bundle();
        mCallback = callback;
        mRepository = repository;
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    public PaginationRepository buildRepository() {
        return null;
    }

    @Override
    public void readNextPage() {
        Bundle bundle = new Bundle();
        bundle.putInt("cursor", mBundle.getInt("next_cursor"));
        bundle.putInt("count", COUNT);
        mRepository.get(bundle, new PaginationRepository.OnGetListener() {
            @Override
            public void onGet(List<Model> list, Bundle bundle) {
                obtainBundleInfo(bundle);
                if (mCallback != null) {
                    mCallback.onReadNextPage(list, isLastPage(mBundle));
                }
            }

            @Override
            public void onException(Exception e) {
                if (mCallback != null) {
                    mCallback.onException(e);
                }
            }
        });
    }

    @Override
    public void readPreviousPage() {
        Bundle bundle = new Bundle();
        bundle.putInt("cursor", mBundle.getInt("previous_cursor"));
        bundle.putInt("count", COUNT);
        mRepository.get(bundle, new PaginationRepository.OnGetListener() {
            @Override
            public void onGet(List<Model> list, Bundle bundle) {
                obtainBundleInfo(bundle);
                if (mCallback != null) {
                    mCallback.onReadPreviousPage(list);
                }
            }

            @Override
            public void onException(Exception e) {
                if (mCallback != null) {
                    mCallback.onException(e);
                }
            }
        });
    }

    @Override
    public void readPage(int page) {
        Bundle bundle = new Bundle();
        bundle.putInt("cursor", page);
        bundle.putInt("count", COUNT);
        mRepository.get(bundle, new PaginationRepository.OnGetListener() {
            @Override
            public void onGet(List<Model> list, Bundle bundle) {
                obtainBundleInfo(bundle);
                if (mCallback != null) {
                    mCallback.onReadPage(list);
                }
            }

            @Override
            public void onException(Exception e) {
                if (mCallback != null) {
                    mCallback.onException(e);
                }
            }
        });
    }

    @Override
    public void readFirstPage() {
        Bundle bundle = new Bundle();
        bundle.putInt("cursor", FIRST_PAGE);
        bundle.putInt("count", COUNT);
        mRepository.get(bundle, new PaginationRepository.OnGetListener() {
            @Override
            public void onGet(List<Model> list, Bundle bundle) {
                obtainBundleInfo(bundle);
                if (mCallback != null) {
                    mCallback.onReadFirstPage(list, isLastPage(mBundle));
                }
            }

            @Override
            public void onException(Exception e) {
                if (mCallback != null) {
                    mCallback.onException(e);
                }
            }
        });
    }

    public void obtainBundleInfo(Bundle bundle) {
        mBundle.putInt("next_cursor", bundle.getInt("next_cursor", 0));
        mBundle.putInt("previous_cursor", bundle.getInt("previous_cursor", 0));
        mBundle.putInt("total_number", bundle.getInt("total_number", 0));
    }

    public boolean isLastPage(Bundle bundle) {
        int next_cursor = bundle.getInt("next_cursor", 0);
        int total_number = bundle.getInt("total_number", 0);
        int previous_cursor = bundle.getInt("previous_cursor", 0);
        return next_cursor == 0 && previous_cursor != 0;
    }
}
