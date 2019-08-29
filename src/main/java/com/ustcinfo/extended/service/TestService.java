package com.ustcinfo.extended.service;


import com.ustcinfo.extended.common.BusinessType;
import com.ustcinfo.extended.entity.ExtendedConfigDetail;
import com.ustcinfo.extended.entity.ExtendedConfigMain;
import com.ustcinfo.extended.repository.TestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author liu.guangyao@utscinfo.com
 * @date 2019-08-27
 */

@Service
public class TestService {

    /**
     * 扩展字段配置表主表实体名
     */
    private static final String EX_CONFIG_MAIN = ExtendedConfigMain.class.getSimpleName();

    /**
     * 扩展字段配置表从表实体名
     */
    private static final String EX_CONFIG_DETAIL = ExtendedConfigDetail.class.getSimpleName();

    @Autowired
    private TestRepository testRepository;

    private Logger logger = LoggerFactory.getLogger(TestService.class);

    public List<Map<String, Object>> queryDataWithExt(BusinessType businessType) {


        List<ExtendedConfigMain> exConfigMainList = getExtendedConfigMains(businessType);

        List<ExtendedConfigDetail> exConfigDetailList = getExtendedConfigDetails(businessType);

        //3.根据查询出来的扩展表配置记录，拼接jpql语句
        StringBuilder jpqlStr = new StringBuilder();

        //(1). 拼接 form之前的语句
        jpqlStr.append("select new map(");
        for (ExtendedConfigMain configMain : exConfigMainList) {
            for (ExtendedConfigDetail configDetail : exConfigDetailList) {
                if (Objects.equals(configDetail.getExtendedMainId(), configMain.getId())) {
                    jpqlStr.append(" ").append(configMain.getEntityAlias()).append(".")
                            .append(configDetail.getEntityFiledName()).append(" as ")
                            .append(configDetail.getEntityFiledAlias()).append(",");
                }
            }
        }
        jpqlStr.replace(jpqlStr.length() - 1, jpqlStr.length(), ") ");


        //(2). 拼接 from之后，where之前的语句
        jpqlStr.append("from");
        for (ExtendedConfigMain configMain : exConfigMainList) {
            jpqlStr.append(" ").append(configMain.getEntityName()).append(" ")
                    .append(configMain.getEntityAlias()).append(",");
        }
        jpqlStr.replace(jpqlStr.length() - 1, jpqlStr.length(), " ");

        //(3). 拼接where 之后的语句
        jpqlStr.append("where");
        for (ExtendedConfigMain configMain : exConfigMainList) {
            //如果是主表
            if (configMain.getIsMainTable() == 1) {
                jpqlStr.append(" ").append(configMain.getEntityAlias()).append(".")
                        .append(configMain.getPrimaryKeyFiledName()).append("=");
                //如果是从表
            } else {
                jpqlStr.append(" ").append(configMain.getEntityAlias()).append(".")
                        .append(configMain.getForeignKeyFiledName()).append("=");
            }
        }
        jpqlStr.replace(jpqlStr.length() - 1, jpqlStr.length(), " ");

        //4.调用持久层，执行jpql语句
        List<Map<String, Object>> list = testRepository.findList(jpqlStr.toString(), null);
        logger.debug("搜索的结果为： ");
        logger.debug(list.toString());

        return list;
    }

    private List<ExtendedConfigDetail> getExtendedConfigDetails(BusinessType businessType) {
        //2.查询ExtendedConfigDetail
        String queryDetailStr = "select d from " + EX_CONFIG_DETAIL + " d, " + EX_CONFIG_MAIN + " m" +
                " where m.id = d.extendedMainId and m.businessCode = :businessCode";
        Map<String, Object> exParamMap = new HashMap<>();
        exParamMap.put("businessCode", businessType.getCode());
        List<ExtendedConfigDetail> exConfigDetailList = testRepository.findList(queryDetailStr, exParamMap);

        //对查询的exConfigDetailList做检查
        if (exConfigDetailList == null) {
            throw new RuntimeException("查询的扩展从配置表为空");
        }
        return exConfigDetailList;
    }

    private List<ExtendedConfigMain> getExtendedConfigMains(BusinessType businessType) {
        //1.查询ExtendedConfigMain
        String queryMainStr = "select m from " + EX_CONFIG_MAIN + " m  where businessCode = :businessCode";
        Map<String, Object> exParamMap = new HashMap<>();
        exParamMap.put("businessCode", businessType.getCode());
        List<ExtendedConfigMain> exConfigMainList = testRepository.findList(queryMainStr, exParamMap);

        //对查询的exConfigMainList做检查
        if (exConfigMainList == null) {
            throw new RuntimeException("查询的扩展主配置表为空");
        }
        if (exConfigMainList.size() != 2) {
            throw new RuntimeException("扩展主配置表的配置有误，请检查\n\t" +
                    "可能原因：同一业务代码配置记录数不等于2\n\t配置表查询的数据: " +
                    exConfigMainList.toString());
        }
        return exConfigMainList;
    }

    @Transactional
    public int deleteDataWithExt(BusinessType businessType, Long id) {

        if (id == null) {
            throw new RuntimeException("删除的id不能为空");
        }

        //查询主表与扩展表配置信息
        List<ExtendedConfigMain> exConfigMainList = getExtendedConfigMains(businessType);

        StringBuilder jpqlMainStr = new StringBuilder();
        StringBuilder jpqlExtendedStr = new StringBuilder();

        for (ExtendedConfigMain configMain : exConfigMainList) {
            //拼接主表的删除语句
            if (configMain.getIsMainTable() == 1) {
                jpqlMainStr.append("delete ").append(configMain.getEntityName()).append(" ")
                        .append(configMain.getEntityAlias()).append(" where ").append(configMain.getEntityAlias())
                        .append(".").append(configMain.getPrimaryKeyFiledName()).append("=:paramId");
            }
            //拼接从表的删除语句
            if (configMain.getIsMainTable() == 0) {
                jpqlExtendedStr.append("delete ").append(configMain.getEntityName()).append(" ")
                        .append(configMain.getEntityAlias()).append(" where ").append(configMain.getEntityAlias())
                        .append(".").append(configMain.getForeignKeyFiledName()).append("=:paramId");
            }
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("paramId", id);

        //先删除扩展表记录
        int exRows = testRepository.executeUpdate(jpqlExtendedStr.toString(), map);
        //再删除主表记录
        int MainRows = testRepository.executeUpdate(jpqlMainStr.toString(), map);

        return 0;

    }

}
