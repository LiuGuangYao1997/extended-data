package com.ustcinfo.extended.repository;


import com.ustcinfo.extended.common.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;


/**
 * @author liu.guangyao@utscinfo.com
 * @date 2019-08-27
 */
@Repository
public class TestRepository {

    @Autowired
    private EntityManager entityManager;

    /**
     * @param qlString 带有参数占位符的JPQL语句
     * @param map      参数map，key为参数名，value为参数值
     * @return List<Map               <               String               ,                               Object>> map为实体对象 key为属性名，value为属性值
     */
    public <T> List<T> findList(String qlString, Class<T> resultClass, Map<String, Object> map, Pagination pagination) {
        TypedQuery<T> query = entityManager.createQuery(qlString, resultClass);
        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        if (pagination != null) {
            query.setFirstResult(pagination.start());
            query.setMaxResults(pagination.getPerpage());
        }

        return query.getResultList();
    }

    /**
     * @param qlString 带有参数占位符的JPQL语句
     * @param map      参数map，key为参数名，value为参数值
     * @return 操作影响记录数
     */
    public int executeUpdate(String qlString, Map<String, Object> map) {
        Query query = entityManager.createQuery(qlString);
        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        return query.executeUpdate();
    }
}
