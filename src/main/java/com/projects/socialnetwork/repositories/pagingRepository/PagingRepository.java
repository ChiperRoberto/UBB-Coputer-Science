package com.projects.socialnetwork.repositories.pagingRepository;

import com.projects.socialnetwork.models.Entity;
import com.projects.socialnetwork.repositories.Repository;
import com.projects.socialnetwork.repositories.paging.Page;
import com.projects.socialnetwork.repositories.paging.Pageable;

import java.util.IdentityHashMap;

public interface PagingRepository<ID,E extends Entity<ID>> extends Repository<ID,E> {
    Page<E> findAll(Pageable pageable);
}
