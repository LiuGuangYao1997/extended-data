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



    @Transactional
    public String deleteDataWithExt(BusinessType businessType, Long id) {

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

        return "删除主表记录数: " + MainRows + "; 删除扩展表记录数: " + exRows;
    }

    @Transactional
    public String updateDataWithExt(BusinessType businessType, Map<String, Object> map) {

        //检查map是否为空
        if (map == null) {
            throw new RuntimeException("更新的map不能为空");
        }

        //查询主表与扩展表配置信息
        List<ExtendedConfigMain> exConfigMainList = getExtendedConfigMains(businessType);
        List<ExtendedConfigDetail> exConfigDetailList = getExtendedConfigDetails(businessType);

        StringBuilder jpqlMainStr = new StringBuilder();
        StringBuilder jpqlExtendedStr = new StringBuilder();
        //primaryName用来储存主表的主键名称，等会拼接主表和从表的jpql语句时，where后面会用到
        String primaryName = "";
        //储存主表/从表更新绑定参数
        HashMap<String, Object> mainParamMap = new HashMap<>();
        HashMap<String, Object> extendedParamMap = new HashMap<>();

        for (ExtendedConfigMain configMain : exConfigMainList) {
            if (configMain.getIsMainTable() == 1) {
                //做一遍检查，要求传入的map中必须要有与数据主表属性名一致的key
                if (!map.containsKey(configMain.getPrimaryKeyFiledName())) {
                    throw new RuntimeException("请在map中传入主数据表的" + configMain.getPrimaryKeyFiledName() + "属性");
                } else {
                    primaryName = configMain.getPrimaryKeyFiledName();
                    //这里因为生成的实体的数据类型都是Long型，如果传入Integer型，在绑定参数时就会报参数类型不匹配错误，所以这里把它转换一下
                    mainParamMap.put(primaryName, new Long(map.get(primaryName).toString()));
                    extendedParamMap.put(primaryName, new Long(map.get(primaryName).toString()));
                }
            }
        }

        for (ExtendedConfigMain configMain : exConfigMainList) {
            if (configMain.getIsMainTable() == 1) {
                //拼接where之前的字符串
                appendJpqlStr(map, exConfigDetailList, jpqlMainStr, mainParamMap, configMain);
                //拼接where之后的字符串，下同
                jpqlMainStr.append("where ").append(configMain.getEntityAlias()).append(".")
                        .append(primaryName).append("=:")
                        .append(primaryName);
            }
            if (configMain.getIsMainTable() == 0) {
                appendJpqlStr(map, exConfigDetailList, jpqlExtendedStr, extendedParamMap, configMain);
                jpqlExtendedStr.append("where ").append(configMain.getEntityAlias()).append(".")
                        .append(configMain.getForeignKeyFiledName()).append("=:")
                        .append(primaryName);
            }
        }

        int extUpdRows = testRepository.executeUpdate(jpqlExtendedStr.toString(), extendedParamMap);
        int mainUpdRows = testRepository.executeUpdate(jpqlMainStr.toString(), mainParamMap);

        return "更新主表记录数: " + mainUpdRows + "; 更新扩展表记录数: " + extUpdRows;
    }

    public String insertDataWithExt(BusinessType businessType, Map<String, Object> map){
        List<ExtendedConfigMain> configMains = getExtendedConfigMains(businessType);
        List<ExtendedConfigDetail> configDetails = getExtendedConfigDetails(businessType);

        StringBuilder jpqlStr = new StringBuilder();

        return "test";
    }


    /**
     * @param map 前台传入的map
     * @param exConfigDetailList 配置从表查询的结果集
     * @param jpqlStr  要被拼接的语句
     * @param paramMap 用来接收set...的参数map
     * @param configMain 配置主表对象
     */
    public void appendJpqlStr(Map<String, Object> map, List<ExtendedConfigDetail> exConfigDetailList, StringBuilder jpqlStr, HashMap<String, Object> paramMap, ExtendedConfigMain configMain) {
        jpqlStr.append("update ").append(configMain.getEntityName()).append(" ")
                .append(configMain.getEntityAlias()).append(" set ");
        for (ExtendedConfigDetail configDetail : exConfigDetailList) {
            //从表字段是主表对象的字段时
            if (Objects.equals(configDetail.getExtendedMainId(), configMain.getId())) {
                //对前台传入的map进行遍历
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (Objects.equals(entry.getKey(), configDetail.getEntityFiledName())
                            && !Objects.equals(entry.getKey(), configMain.getPrimaryKeyFiledName())) {
                        //这里也是把Integer型转换为Long型
                        if (entry.getValue() instanceof Integer) {
                            paramMap.put(entry.getKey(), new Long(entry.getValue().toString()));
                        } else {
                            paramMap.put(entry.getKey(), entry.getValue());
                        }
                        jpqlStr.append(configMain.getEntityAlias()).append(".")
                                .append(configDetail.getEntityFiledName()).append("=:")
                                .append(entry.getKey()).append(",");
                    }
                }
            }
        }
        jpqlStr.replace(jpqlStr.length() - 1, jpqlStr.length(), " ");
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
}
