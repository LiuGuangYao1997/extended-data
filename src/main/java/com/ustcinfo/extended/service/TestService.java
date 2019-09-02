package com.ustcinfo.extended.service;


import com.ustcinfo.extended.common.BusinessType;
import com.ustcinfo.extended.common.Pagination;
import com.ustcinfo.extended.common.QueryParam;
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

    // 扩展字段配置表主表实体名
    private static final String EX_CONFIG_ENTITY = ExtendedDataEntity.class.getSimpleName();

    // 扩展字段配置表从表实体名
    private static final String EX_CONFIG_FILED = ExtendedDataFiled.class.getSimpleName();

    // 数据类型属性名称
    private static final String DATA_TYPE_CODE = "dataTypeCode";

    // 扩展配置实体表主键属性名
    private static final String EXT_ENTITY_PRIMARYKEY = "id";

    // 扩展配置字段表外键键属性名
    private static final String EXT_FILED_FOREIGNKEY = "extEntityId";

    @Autowired
    private TestRepository testRepository;

    private Logger logger = LoggerFactory.getLogger(TestService.class);

    public Map queryDetailWithExt(BusinessType businessType, Long id) {
        if (id == null) {
            throw new RuntimeException("请传入非空的id值");
        }
        ExtendedDataEntity extDataEntity = getExtendedDataEntitys(businessType);
        List<ExtendedDataFiled> exConfigFiledList = getExtendedDataFileds(businessType);


        String jpqlStr = queryDataWithExt(exConfigFiledList, extDataEntity);
        jpqlStr = jpqlStr + " and " + extDataEntity.getMainEntityAlias() + "."
                + extDataEntity.getMainEntityPrimarykey() + "=:id";
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", id);
        List<Map> list = testRepository.findList(jpqlStr, Map.class, paramMap, null);
        if (list == null || list.size() == 0) {
            throw new RuntimeException("查询结果为空");
        }
        if (list.size() != 1) {
            throw new RuntimeException("查询结果不唯一");
        }
        return list.get(0);
    }

    public List<Map<String, Object>> queryTableWithExt(BusinessType businessType,
                                                       Pagination pagination,
                                                       List<QueryParam> queryParams,
                                                       List<Map<String,Object>> orderMap) {

        ExtendedDataEntity extDataEntity = getExtendedDataEntitys(businessType);
        List<ExtendedDataFiled> exConfigFiledList = getExtendedDataFileds(businessType);
        String jpqlStr = queryDataWithExt(exConfigFiledList, extDataEntity);

        // 拼接查询条件
        StringBuilder queryParamStr = new StringBuilder();
        if (queryParams != null){
            for (QueryParam queryParam : queryParams) {
                if (queryParam != null){
                    queryParamStr.append(" and ").append(queryParam.getFiled())
                            .append(queryParam.getLog()).append(" '").append(queryParam.getVal()).append("' ");
                }
            }
        }
        jpqlStr += queryParamStr.toString();

        // 拼接排序条件
        StringBuilder orderStr = new StringBuilder();
        if (orderMap != null){
            orderStr.append(" order by ");
            for (Map<String, Object> map : orderMap) {
                orderStr.append(map.get("filed")).append(" ").append(map.get("esc")).append(", ");
            }
            orderStr.replace(orderStr.length()-2, orderStr.length(), " ");
        }
        jpqlStr += orderStr.toString();

        testRepository.findList(jpqlStr, Map.class, null, null);

        return null;
    }

    @Transactional
    public String deleteDataWithExt(BusinessType businessType, Long id) {
        if (id == null) {
            throw new RuntimeException("删除的id不能为空");
        }

        //查询扩展配置实体表信息
        ExtendedDataEntity extDataEntity = getExtendedDataEntitys(businessType);

        HashMap<String, Object> map = new HashMap<>();
        map.put("paramId", id);

        //拼接从表的删除语句
        String jpqlExtendedStr = "delete " + extDataEntity.getExtEntityName() + " " +
                extDataEntity.getExtEntityAlias() + " where " + extDataEntity.getExtEntityAlias() +
                "." + extDataEntity.getExtEntityForeignkey() + "=:paramId";
        int exRows = testRepository.executeUpdate(jpqlExtendedStr, map);
        //拼接主表的删除语句
        String jpqlMainStr = "delete " + extDataEntity.getMainEntityName() + " " +
                extDataEntity.getMainEntityAlias() + " where " + extDataEntity.getMainEntityAlias() +
                "." + extDataEntity.getMainEntityPrimarykey() + "=:paramId";
        int MainRows = testRepository.executeUpdate(jpqlMainStr, map);

        return "删除主表记录数: " + MainRows + "; 删除扩展表记录数: " + exRows;
    }

    @Transactional
    public String updateDataWithExt(BusinessType businessType, Map<String, Object> map) {

        //检查map是否为空
        if (map == null) {
            throw new RuntimeException("更新的map不能为空");
        }

        //查询扩展配置实体表信息
        ExtendedDataEntity extDataEntity = getExtendedDataEntitys(businessType);
        List<ExtendedDataFiled> exConfigFiledList = getExtendedDataFileds(businessType);
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
            if (filed.getIsMainEntityFiled() == 1 && map.containsKey(filed.getFiledName())) {
                mainParamNum++;
            } else if (filed.getIsMainEntityFiled() == 0 && map.containsKey(filed.getFiledName())) {
                extParamNum++;
            }
        }
        logger.info("传入总参数有" + map.size() + "个;" + "主表参数" + mainParamNum + "个,扩展表参数" + extParamNum + "个.");

        // 拼接主表更新语句
        if (mainParamNum > 0) {
            //拼接where之前的字符串
            jpqlMainStr.append("update ").append(extDataEntity.getMainEntityName()).append(" ")
                    .append(extDataEntity.getMainEntityAlias()).append(" set");
            for (ExtendedDataFiled filed : exConfigFiledList) {
                if (filed.getIsMainEntityFiled() == 1) {
                    //DONE 判断更新值是否为空， 如果为空就不更新
                    if (map.containsKey(filed.getFiledName()) && map.get(filed.getFiledName()) != null) {
                        jpqlMainStr.append(" ").append(extDataEntity.getMainEntityAlias()).append(".")
                                .append(filed.getFiledName()).append("=:").append(filed.getFiledName()).append(",");
                        if (map.get(filed.getFiledName()) instanceof Integer) {
                            mainParamMap.put(filed.getFiledName(), new Long(map.get(filed.getFiledName()).toString()));
                        } else {
                            mainParamMap.put(filed.getFiledName(), map.get(filed.getFiledName()));
                        }
                    }
                }
            }
            jpqlMainStr.replace(jpqlMainStr.length() - 1, jpqlMainStr.length(), " ");
            //拼接where之后的字符串，下同
            jpqlMainStr.append("where ").append(extDataEntity.getMainEntityAlias()).append(".")
                    .append(mainEntityPrimarykey).append("=:")
                    .append(mainEntityPrimarykey);
            mainUpdRows = testRepository.executeUpdate(jpqlMainStr.toString(), mainParamMap);
        }
        if (extParamNum > 0) {
            // 拼接从表更新语句
            jpqlExtendedStr.append("update ").append(extDataEntity.getExtEntityName()).append(" ")
                    .append(extDataEntity.getExtEntityAlias()).append(" set");
            for (ExtendedDataFiled filed : exConfigFiledList) {
                if (filed.getIsMainEntityFiled() == 0) {
                    if (map.containsKey(filed.getFiledName()) && map.get(filed.getFiledName()) != null) {
                        jpqlExtendedStr.append(" ").append(extDataEntity.getExtEntityAlias()).append(".")
                                .append(filed.getFiledName()).append("=:").append(filed.getFiledName()).append(",");
                        if (map.get(filed.getFiledName()) instanceof Integer) {
                            extendedParamMap.put(filed.getFiledName(), new Long(map.get(filed.getFiledName()).toString()));
                        } else {
                            extendedParamMap.put(filed.getFiledName(), map.get(filed.getFiledName()));
                        }
                    }
                }
            }
            jpqlExtendedStr.replace(jpqlExtendedStr.length() - 1, jpqlExtendedStr.length(), " ");
            jpqlExtendedStr.append("where ").append(extDataEntity.getExtEntityAlias()).append(".")
                    .append(extDataEntity.getExtEntityForeignkey()).append("=:")
                    .append(mainEntityPrimarykey);
            extUpdRows = testRepository.executeUpdate(jpqlExtendedStr.toString(), extendedParamMap);
        }

        return "更新主表记录数: " + mainUpdRows + "; 更新扩展表记录数: " + extUpdRows;
    }

    @Transactional
    public String insertDataWithExt(BusinessType businessType, Object mainObject, Object extObject) {

        return "test";
    }

    private List<ExtendedDataFiled> getExtendedDataFileds(BusinessType businessType) {
        //2.查询ExtendedDataFiled
        String queryDetailStr = "select d from " + EX_CONFIG_FILED + " d, " + EX_CONFIG_ENTITY + " m" +
                " where m." + EXT_ENTITY_PRIMARYKEY + " = d." + EXT_FILED_FOREIGNKEY + " and m." + DATA_TYPE_CODE
                + " = :businessCode";
        Map<String, Object> exParamMap = new HashMap<>();
        exParamMap.put("businessCode", businessType.getCode());
        List<ExtendedDataFiled> exConfigDetailList =
                testRepository.findList(queryDetailStr, ExtendedDataFiled.class, exParamMap, null);

        //对查询的exConfigDetailList做检查
        if (exConfigDetailList == null) {
            throw new RuntimeException("查询的扩展从配置表为空");
        }
        return exConfigDetailList;
    }

    private ExtendedDataEntity getExtendedDataEntitys(BusinessType businessType) {
        //1.查询ExtendedDataEntity
        String queryMainStr = "select m from " + EX_CONFIG_ENTITY + " m  where " + DATA_TYPE_CODE + " = :businessCode";
        Map<String, Object> exParamMap = new HashMap<>();
        exParamMap.put("businessCode", businessType.getCode());
        List<ExtendedDataEntity> exConfigMainList =
                testRepository.findList(queryMainStr, ExtendedDataEntity.class, exParamMap, null);

        //对查询的exConfigMainList做检查
        if (exConfigMainList == null) {
            throw new RuntimeException("查询的扩展主配置表为空");
        }
        if (exConfigMainList.size() != 1) {
            throw new RuntimeException("扩展主配置表的配置有误，请检查\n\t" +
                    "可能原因：同一业务代码配置记录数不等于1\n\t配置表查询的数据: " +
                    exConfigMainList.toString());
        }
        return exConfigMainList.get(0);
    }

    private String queryDataWithExt(List<ExtendedDataFiled> exConfigFiledList,
                                    ExtendedDataEntity extDataEntity) {

        //根据查询出来的扩展表配置记录，拼接jpql语句
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

        return jpqlStr.toString();
    }
}
