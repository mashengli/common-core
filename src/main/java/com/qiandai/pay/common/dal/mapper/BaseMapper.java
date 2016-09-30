package com.qiandai.pay.common.dal.mapper;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import com.qiandai.pay.common.dal.query.MapperQuery;

/**
 * Created by mashengli on 2016/7/6.
 */
public interface BaseMapper<T extends Serializable, ID extends Serializable> {
    long insert(T entity);

    int update(T entity);

    T findOne(ID id);

    List<T> findAll();

    List<T> findList(MapperQuery query);

    List<T> findByIds(Iterator<ID> ids);

    void delete(ID id);

    void deleteByIds(List<ID> ids);

    long countList(MapperQuery query);

    long countAll();

}
