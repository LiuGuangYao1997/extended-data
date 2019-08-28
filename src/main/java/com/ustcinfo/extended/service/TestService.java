package com.ustcinfo.extended.service;


import com.ustcinfo.extended.common.BusinessType;
import com.ustcinfo.extended.entity.ExtendedConfigDetail;
import com.ustcinfo.extended.entity.ExtendedConfigMain;
import com.ustcinfo.extended.repository.TestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

        //1.查询ExtendedConfigMain
        String queryMainStr = "select m from " + EX_CONFIG_MAIN + " m  where businessCode = :businessCode";
        HashMap<String, String> exParamMap = new HashMap<>();
        exParamMap.put("businessCode", businessType.getCode());
        List<ExtendedConfigMain> exConfigMainList = testRepository.findList(queryMainStr, exParamMap);

        //对查询的exConfigMainList做检查
        if (exConfigMainList == null){
            throw new RuntimeException("查询的扩展主配置表为空");
        }
        if (exConfigMainList.size() != 2){
            throw new RuntimeException("扩展主配置表的配置有误，请检查\n" +
                    "可能原因：同一业务代码配置记录数不等于2\n" +
                    exConfigMainList.toString());
        }

        //2.查询ExtendedConfigDetail
        String queryDetailStr = "select d from " + EX_CONFIG_DETAIL + " d, " + EX_CONFIG_MAIN + " m" +
                " where m.id = d.extendedMainId and m.businessCode = :businessCode";
        List<ExtendedConfigDetail> exConfigDetailList = testRepository.findList(queryDetailStr, exParamMap);

        //对查询的exConfigDetailList做检查
        if (exConfigDetailList == null){
            throw new RuntimeException("查询的扩展从配置表为空");
        }

        //3.根据查询出来的扩展表配置记录，拼接jpql语句
        StringBuilder jpqlStr = new StringBuilder();

        //(1). 拼接 form之前的语句
        jpqlStr.append("select new map(");
        for (ExtendedConfigMain configMain : exConfigMainList) {
            for (ExtendedConfigDetail configDetail : exConfigDetailList) {
                if (Objects.equals(configDetail.getExtendedMainId(), configMain.getId())){
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

}
