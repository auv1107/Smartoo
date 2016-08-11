package com.sctdroid.autosigner.domain.interactors.base;

import com.sctdroid.autosigner.domain.executor.MainThread;
import com.sctdroid.autosigner.domain.repository.PaginationRepository;

/**
 * Created by lixindong on 6/28/16.
 */
public abstract class AbstractPaginationInteractor {
    protected MainThread mMainThread;
    protected PaginationRepository mRepository;
    public AbstractPaginationInteractor(MainThread mainThread) {
        mMainThread = mainThread;
        mRepository = buildRepository();
    }

    public abstract PaginationRepository buildRepository();
}
