package com.ustcinfo.extended.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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
     * @param map 参数map，key为参数名，value为参数值
     * @return List<Map<String, Object>> map为实体对象 key为属性名，value为属性值
     */
    public List<Map<String, Object>> findList(String qlString, Map<String,String> map){
        Query query = setParameters(qlString, map);
        return query.getResultList();
    }

    /**
     * @param qlString 带有参数占位符的JPQL语句
     * @param map 参数map，key为参数名，value为参数值
     * @return 操作影响记录数
     */
    public int executeUpdate(String qlString, Map<String,String> map){
        Query query = setParameters(qlString, map);
        return query.executeUpdate();
    }

    private Query setParameters(String qlString, Map<String, String> map) {
        Query query = entityManager.createQuery(qlString);
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        return query;
    }
}
