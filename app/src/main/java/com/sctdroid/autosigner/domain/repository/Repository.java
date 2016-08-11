package com.sctdroid.autosigner.domain.repository;

import com.sctdroid.autosigner.domain.model.Model;

/**
 * A sample repository with CRUD operations on a model.
 */
public interface Repository {

    boolean insert(Model model);

    boolean update(Model model);

    Model get(Object id);

    boolean delete(Model model);
}
