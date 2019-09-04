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
public class ExtendedDataRepository {

    @Autowired
    private EntityManager entityManager;

    /**
     * 根据传入的参数查询数据库，并返回查询列表
     * @param qlString 带有参数占位符的JPQL语句
     * @param resultClass 想要获取的返回参数类型
     * @param map      参数map，key为参数名，value为参数值
     * @param pagination 分页对象参数
     * @return List<T> T为传入的resultClass类型
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
        query.setHint("javax.persistence.query.timeout", 5000);

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

    /**
     * @param object 传入对象object，对其进行保存操作
     */
    public void save(Object object){
        entityManager.persist(object);
    }
}
