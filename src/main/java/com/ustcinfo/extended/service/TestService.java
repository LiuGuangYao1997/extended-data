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

@Service
public class TestService {

    @Autowired
    private TestRepository testRepository;

    private Logger logger = LoggerFactory.getLogger(TestService.class);

    public List<Map<String, Object>> queryDataWithExt(BusinessType businessType) {

        //1. 先根据dataType查找出扩展配置表中与此业务相关的配置字段
//        ExtendedConfiguration configuration = new ExtendedConfiguration();
//        configuration.setDataType(businessType.getKey());
//        Example<ExtendedConfiguration> example = Example.of(configuration);
//        List<ExtendedConfiguration> extendedConfigurations = extendedConfigRepository.findAll(example);

        //1.查询ExtendedConfigMain
        String queryMainStr = "select m from ExtendedConfigMain m  where businessCode = :businessCode";
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
        String queryDetailStr = "select d from ExtendedConfigDetail d, ExtendedConfigMain m" +
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
                    jpqlStr.append(" ").append(configMain.getEntityAlias()).append(".").append(configDetail.getEntityFiledName()).append(" as ").append(configDetail.getEntityFiledAlias()).append(",");
                }
            }
        }
        jpqlStr.replace(jpqlStr.length() - 1, jpqlStr.length(), ") ");


        //(2). 拼接 from之后，where之前的语句
        jpqlStr.append("from");
        for (ExtendedConfigMain configMain : exConfigMainList) {
            jpqlStr.append(" ").append(configMain.getEntityName()).append(" ").append(configMain.getEntityAlias()).append(",");
        }
        jpqlStr.replace(jpqlStr.length() - 1, jpqlStr.length(), " ");

        //(3). 拼接where 之后的语句
        jpqlStr.append("where");
        for (ExtendedConfigMain configMain : exConfigMainList) {
            //如果是主表
            if (configMain.getIsMainTable() == 1) {
                jpqlStr.append(" ").append(configMain.getEntityAlias()).append(".").append(configMain.getPrimaryKeyFiledName()).append("=");
                //如果是从表
            } else {
                jpqlStr.append(" ").append(configMain.getEntityAlias()).append(".").append(configMain.getForeignKeyFiledName()).append("=");
            }
        }
        jpqlStr.replace(jpqlStr.length() - 1, jpqlStr.length(), " ");

        //4.调用持久层，执行jpql语句
        List<Map<String, Object>> list = testRepository.findList(jpqlStr.toString(), null);
        logger.debug("搜索的结果为： ");
        logger.debug(list.toString());

        return list;

/*
        //2. 根据查询出来的扩展表配置记录，拼接jpql语句
        StringBuffer jpqlStr = new StringBuffer();

        //(1). 拼接 form之前的语句
        jpqlStr.append("select new map(");
        if (extendedConfigurations == null || extendedConfigurations.size() < 1) {
            throw new RuntimeException("该项业务尚未在扩展字段配置表中维护！");
        }
        for (ExtendedConfiguration ec : extendedConfigurations) {
            jpqlStr.append(" " + ec.getTableName() + "." + ec.getEntityFiledName() + " as " + ec.getEntityFiledName() + ",");
        }
        logger.debug("拼接的查询语句1：---" + jpqlStr);
        jpqlStr.replace(jpqlStr.length() - 1, jpqlStr.length(), ") ");
        logger.debug("将尾部符号替换：---" + jpqlStr);

        //(2). 拼接 from之后，where之前的语句
        jpqlStr.append("from");
        //根据EntityName对ExtendedConfiguration进行去重
        TreeSet<ExtendedConfiguration> ecGroupByEntityName = new TreeSet<>(new Comparator<ExtendedConfiguration>() {
            @Override
            public int compare(ExtendedConfiguration o1, ExtendedConfiguration o2) {
                return o1.getEntityName().compareTo(o2.getEntityName());
            }
        });
        for (ExtendedConfiguration ec : extendedConfigurations) {
            ecGroupByEntityName.add(ec);
        }
        for (ExtendedConfiguration ec : ecGroupByEntityName) {
            jpqlStr.append(" " + ec.getEntityName() + " " + ec.getTableName() + ",");
        }

        logger.debug("拼接的查询语句2：---" + jpqlStr);
        jpqlStr.replace(jpqlStr.length() - 1, jpqlStr.length(), " ");
        logger.debug("将尾部符号替换：---" + jpqlStr);

        //(3). 拼接where 之后的语句
        jpqlStr.append("where");
        if (ecGroupByEntityName.size() != 2) {
            throw new RuntimeException("主表与扩展表之间的关联配置应该是一个主表一个从表，检查配置是否正确");
        }
        //默认id为主表和扩展表的实体的主键，mainTableId为扩展表的关联外键
        ArrayList<java.lang.String> strings = new ArrayList<>();
        for (ExtendedConfiguration ec : ecGroupByEntityName) {
            //如果是主表
            if (Objects.equals(ec.getTableName(), ec.getRelevantMainTable())) {
                strings.add(" " + ec.getTableName() + ".id");
                //如果是从表
            } else {
                strings.add(" " + ec.getTableName() + ".mainTableId");
            }
        }
        jpqlStr.append(strings.get(0) + " = " + strings.get(1));

        logger.debug("拼接的查询语句3：---" + jpqlStr);


        //3.调用持久层，执行jpql语句
        List<Map<String, Object>> list = testRepository.findList(jpqlStr.toString(), null);
        logger.debug("搜索的结果为： ");
        logger.debug(list.toString());

        return list;*/
    }

}
