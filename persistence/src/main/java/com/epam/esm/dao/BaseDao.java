package com.epam.esm.dao;

import com.epam.esm.domain.AbstractEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BaseDao<T extends AbstractEntity, K> {

    boolean create(T t);

    List<T> readAll();

    Optional<T> readById(K id);

    boolean update(T t);

    boolean delete(K id);
}
