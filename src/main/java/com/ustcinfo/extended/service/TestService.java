package com.ustcinfo.extended.service;


import com.ustcinfo.extended.common.BusinessType;
import com.ustcinfo.extended.entity.ExtendedDataEntity;
import com.ustcinfo.extended.entity.ExtendedDataFiled;
import com.ustcinfo.extended.repository.TestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liu.guangyao@utscinfo.com
 * @date 2019-08-27
 */

@Service
public class TestService {

    /**
     * 扩展字段配置表主表实体名
     */
    private static final String EX_CONFIG_ENTITY = ExtendedDataEntity.class.getSimpleName();

    /**
     * 扩展字段配置表从表实体名
     */
    private static final String EX_CONFIG_FILED = ExtendedDataFiled.class.getSimpleName();

    @Autowired
    private TestRepository testRepository;

    private Logger logger = LoggerFactory.getLogger(TestService.class);

    public List<Map<String, Object>> queryDataWithExt(BusinessType businessType) {

        List<ExtendedDataEntity> exConfigEntityList = getExtendedDataEntitys(businessType);
        List<ExtendedDataFiled> exConfigFiledList = getExtendedDataFileds(businessType);
        ExtendedDataEntity extDataEntity = new ExtendedDataEntity();

        if (exConfigEntityList == null || exConfigEntityList.size() != 1) {
            throw new RuntimeException("在扩展数据实体表中查询不到数据相关的配置记录");
        } else {
            extDataEntity = exConfigEntityList.get(0);
        }

        //3.根据查询出来的扩展表配置记录，拼接jpql语句
        StringBuilder jpqlStr = new StringBuilder();

        //(1). 拼接 form之前的语句
        jpqlStr.append("select new map(");
        for (ExtendedDataFiled configFiled : exConfigFiledList) {
            // 如果是主表字段
            if (configFiled.getIsMainEntityFiled() == 1) {
                jpqlStr.append(" ").append(extDataEntity.getMainEntityAlias()).append(".")
                        .append(configFiled.getFiledName()).append(" as ")
                        .append(configFiled.getFiledAlias()).append(",");
            }
            // 如果是扩展表字段
            if (configFiled.getIsMainEntityFiled() == 0) {
                jpqlStr.append(" ").append(extDataEntity.getExtEntityAlias()).append(".")
                        .append(configFiled.getFiledName()).append(" as ")
                        .append(configFiled.getFiledAlias()).append(",");
            }
        }

        jpqlStr.replace(jpqlStr.length() - 1, jpqlStr.length(), ") ");

        //(2). 拼接 from之后，where之前的语句
        jpqlStr.append("from ").append(extDataEntity.getMainEntityName()).append(" ")
                .append(extDataEntity.getMainEntityAlias()).append(", ").append(extDataEntity.getExtEntityName())
                .append(" ").append(extDataEntity.getExtEntityAlias()).append(" ");

        //(3). 拼接where 之后的语句
        jpqlStr.append("where ").append(extDataEntity.getMainEntityAlias()).append(".")
                .append(extDataEntity.getMainEntityPrimarykey()).append("=").append(extDataEntity.getExtEntityAlias())
                .append(".").append(extDataEntity.getExtEntityForeignkey()).append(" ");

        //4.调用持久层，执行jpql语句
        List<Map<String, Object>> list = testRepository.findList(jpqlStr.toString(), null);
        logger.debug("搜索的结果为： ");
        logger.debug(list.toString());

        return list;
    }

    @Transactional
    public String deleteDataWithExt(BusinessType businessType, Long id) {

        if (id == null) {
            throw new RuntimeException("删除的id不能为空");
        }

        //查询扩展配置实体表信息
        List<ExtendedDataEntity> exConfigEntityList = getExtendedDataEntitys(businessType);
        ExtendedDataEntity extDataEntity = new ExtendedDataEntity();
        if (exConfigEntityList == null || exConfigEntityList.size() != 1) {
            throw new RuntimeException("在扩展数据实体表中查询不到数据相关的配置记录");
        } else {
            extDataEntity = exConfigEntityList.get(0);
        }

        StringBuilder jpqlMainStr = new StringBuilder();
        StringBuilder jpqlExtendedStr = new StringBuilder();

        //拼接主表的删除语句
        jpqlMainStr.append("delete ").append(extDataEntity.getMainEntityName()).append(" ")
                .append(extDataEntity.getMainEntityAlias()).append(" where ").append(extDataEntity.getMainEntityAlias())
                .append(".").append(extDataEntity.getMainEntityPrimarykey()).append("=:paramId");

        //拼接从表的删除语句
        jpqlExtendedStr.append("delete ").append(extDataEntity.getExtEntityName()).append(" ")
                .append(extDataEntity.getExtEntityAlias()).append(" where ").append(extDataEntity.getExtEntityAlias())
                .append(".").append(extDataEntity.getExtEntityForeignkey()).append("=:paramId");

        HashMap<String, Object> map = new HashMap<>();
        map.put("paramId", id);

        //先删除扩展表记录
        int exRows = testRepository.executeUpdate(jpqlExtendedStr.toString(), map);
        //再删除主表记录
        int MainRows = testRepository.executeUpdate(jpqlMainStr.toString(), map);

        return "删除主表记录数: " + MainRows + "; 删除扩展表记录数: " + exRows;
    }

    @Transactional
    public String updateDataWithExt(BusinessType businessType, Map<String, Object> map) {

        //检查map是否为空
        if (map == null) {
            throw new RuntimeException("更新的map不能为空");
        }

        //查询扩展配置实体表信息
        List<ExtendedDataEntity> exConfigEntityList = getExtendedDataEntitys(businessType);
        List<ExtendedDataFiled> exConfigFiledList = getExtendedDataFileds(businessType);
        ExtendedDataEntity extDataEntity = new ExtendedDataEntity();
        if (exConfigEntityList == null || exConfigEntityList.size() != 1) {
            throw new RuntimeException("在扩展数据实体表中查询不到数据相关的配置记录");
        } else {
            extDataEntity = exConfigEntityList.get(0);
        }

        String mainEntityPrimarykey = extDataEntity.getMainEntityPrimarykey();

        StringBuilder jpqlMainStr = new StringBuilder();
        StringBuilder jpqlExtendedStr = new StringBuilder();
        //储存主表/从表更新绑定参数
        HashMap<String, Object> mainParamMap = new HashMap<>();
        HashMap<String, Object> extendedParamMap = new HashMap<>();


        //做一遍检查，要求传入的map中必须要有与数据主表属性名一致的key
        if (!map.containsKey(mainEntityPrimarykey)) {
            throw new RuntimeException("请在map中传入主数据表的" + mainEntityPrimarykey + "属性");
        } else {
            //这里因为生成的实体的数据类型都是Long型，如果传入Integer型，在绑定参数时就会报参数类型不匹配错误，所以这里把它转换一下
            mainParamMap.put(mainEntityPrimarykey, new Long(map.get(mainEntityPrimarykey).toString()));
            extendedParamMap.put(mainEntityPrimarykey, new Long(map.get(mainEntityPrimarykey).toString()));
        }
        int mainParamNum = 0;
        int extParamNum = 0;
        int mainUpdRows = 0;
        int extUpdRows = 0;

        for (ExtendedDataFiled filed : exConfigFiledList) {
            if (filed.getIsMainEntityFiled() == 1 && map.containsKey(filed.getFiledName())){
                mainParamNum++;
            }else if (filed.getIsMainEntityFiled() == 0 && map.containsKey(filed.getFiledName())) {
                extParamNum++;
            }
        }
        logger.info("传入总参数有" + map.size() + "个;" + "主表参数" + mainParamNum + "个,扩展表参数" + extParamNum + "个.");

        // 拼接主表更新语句

        if(mainParamNum > 0){
            //拼接where之前的字符串
            jpqlMainStr.append("update ").append(extDataEntity.getMainEntityName()).append(" ")
                    .append(extDataEntity.getMainEntityAlias()).append(" set");
            for (ExtendedDataFiled filed : exConfigFiledList) {
                if (filed.getIsMainEntityFiled() == 1){
                    jpqlMainStr.append(" ").append(extDataEntity.getMainEntityAlias()).append(".")
                            .append(filed.getFiledName()).append("=:").append(filed.getFiledName()).append(",");
                    if (map.get(filed.getFiledName()) instanceof Integer){
                        mainParamMap.put(filed.getFiledName(), new Long(map.get(filed.getFiledName()).toString()));
                    } else {
                        mainParamMap.put(filed.getFiledName(), map.get(filed.getFiledName()));
                    }
                }
            }
            jpqlMainStr.replace(jpqlMainStr.length()-1, jpqlMainStr.length(), " ");
            //拼接where之后的字符串，下同
            jpqlMainStr.append("where ").append(extDataEntity.getMainEntityAlias()).append(".")
                    .append(mainEntityPrimarykey).append("=:")
                    .append(mainEntityPrimarykey);
            mainUpdRows = testRepository.executeUpdate(jpqlMainStr.toString(), mainParamMap);
        }
        if (extParamNum > 0){
            // 拼接从表更新语句
            jpqlExtendedStr.append("update ").append(extDataEntity.getExtEntityName()).append(" ")
                    .append(extDataEntity.getExtEntityAlias()).append(" set");
            for (ExtendedDataFiled filed : exConfigFiledList) {
                if (filed.getIsMainEntityFiled() == 0){
                    jpqlExtendedStr.append(" ").append(extDataEntity.getExtEntityAlias()).append(".")
                            .append(filed.getFiledName()).append("=:").append(filed.getFiledName()).append(",");
                    if (map.get(filed.getFiledName()) instanceof Integer){
                        extendedParamMap.put(filed.getFiledName(), new Long(map.get(filed.getFiledName()).toString()));
                    } else {
                        extendedParamMap.put(filed.getFiledName(), map.get(filed.getFiledName()));
                    }
                }
            }
            jpqlExtendedStr.replace(jpqlExtendedStr.length()-1, jpqlExtendedStr.length(), " ");
            jpqlExtendedStr.append("where ").append(extDataEntity.getExtEntityAlias()).append(".")
                    .append(extDataEntity.getExtEntityForeignkey()).append("=:")
                    .append(mainEntityPrimarykey);
            extUpdRows = testRepository.executeUpdate(jpqlExtendedStr.toString(), extendedParamMap);
        }

        return "更新主表记录数: " + mainUpdRows + "; 更新扩展表记录数: " + extUpdRows;
    }

    public String insertDataWithExt(BusinessType businessType, Map<String, Object> map) {
        List<ExtendedDataEntity> configMains = getExtendedDataEntitys(businessType);
        List<ExtendedDataFiled> configDetails = getExtendedDataFileds(businessType);

        StringBuilder jpqlStr = new StringBuilder();

        return "test";
    }

    private List<ExtendedDataFiled> getExtendedDataFileds(BusinessType businessType) {
        //2.查询ExtendedDataFiled
        String queryDetailStr = "select d from " + EX_CONFIG_FILED + " d, " + EX_CONFIG_ENTITY + " m" +
                " where m.id = d.extEntityId and m.dataTypeCode = :businessCode";
        Map<String, Object> exParamMap = new HashMap<>();
        exParamMap.put("businessCode", businessType.getCode());
        List<ExtendedDataFiled> exConfigDetailList = testRepository.findList(queryDetailStr, exParamMap);

        //对查询的exConfigDetailList做检查
        if (exConfigDetailList == null) {
            throw new RuntimeException("查询的扩展从配置表为空");
        }
        return exConfigDetailList;
    }

    private List<ExtendedDataEntity> getExtendedDataEntitys(BusinessType businessType) {
        //1.查询ExtendedDataEntity
        String queryMainStr = "select m from " + EX_CONFIG_ENTITY + " m  where dataTypeCode = :businessCode";
        Map<String, Object> exParamMap = new HashMap<>();
        exParamMap.put("businessCode", businessType.getCode());
        List<ExtendedDataEntity> exConfigMainList = testRepository.findList(queryMainStr, exParamMap);

        //对查询的exConfigMainList做检查
        if (exConfigMainList == null) {
            throw new RuntimeException("查询的扩展主配置表为空");
        }
        if (exConfigMainList.size() != 1) {
            throw new RuntimeException("扩展主配置表的配置有误，请检查\n\t" +
                    "可能原因：同一业务代码配置记录数不等于1\n\t配置表查询的数据: " +
                    exConfigMainList.toString());
        }
        return exConfigMainList;
    }
}
