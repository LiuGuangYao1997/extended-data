package com.ustcinfo.extended.service;


import com.ustcinfo.extended.common.DataType;
import com.ustcinfo.extended.entity.ExtendedConfiguration;
import com.ustcinfo.extended.repository.ExtendedConfigurationRepository;
import com.ustcinfo.extended.repository.TestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TestService {

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private ExtendedConfigurationRepository extendedConfigRepository;

    private Logger logger = LoggerFactory.getLogger(TestService.class);

    public List<Map<String, Object>> queryDataWithExt(DataType dataType) {

        //1. 先根据dataType查找出扩展配置表中与此业务相关的配置字段
        ExtendedConfiguration configuration = new ExtendedConfiguration();
        configuration.setDataType(dataType.getKey());
        Example<ExtendedConfiguration> example = Example.of(configuration);
        List<ExtendedConfiguration> extendedConfigurations = extendedConfigRepository.findAll(example);

        //2. 根据查询出来的扩展表配置记录，拼接jpql语句
        StringBuffer jpql = new StringBuffer();

        //(1). 拼接 form之前的语句
        jpql.append("select new map(");
        if (extendedConfigurations == null || extendedConfigurations.size() < 1) {
            throw new RuntimeException("该项业务尚未在扩展字段配置表中维护！");
        }
        for (ExtendedConfiguration ec : extendedConfigurations) {
            jpql.append(" " + ec.getTableName() + "." + ec.getEntityFiledName() + " as " + ec.getEntityFiledName() + ",");
        }
        logger.debug("拼接的查询语句1：---" + jpql);
        jpql.replace(jpql.length() - 1, jpql.length(), ") ");
        logger.debug("将尾部符号替换：---" + jpql);

        //(2). 拼接 from之后，where之前的语句
        jpql.append("from");
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
            jpql.append(" " + ec.getEntityName() + " " + ec.getTableName() + ",");
        }

        logger.debug("拼接的查询语句2：---" + jpql);
        jpql.replace(jpql.length() - 1, jpql.length(), " ");
        logger.debug("将尾部符号替换：---" + jpql);

        //(3). 拼接where 之后的语句
        jpql.append("where");
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
        jpql.append(strings.get(0) + " = " + strings.get(1));

        logger.debug("拼接的查询语句3：---" + jpql);


        //3.调用持久层，执行jpql语句
        List<Map<String, Object>> list = testRepository.findList(jpql.toString(), null);
        logger.debug("搜索的结果为： ");
        logger.debug(list.toString());

        return list;
    }

}
