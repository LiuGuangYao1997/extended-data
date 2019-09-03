package com.ustcinfo.extended.service;


import com.ustcinfo.extended.common.BusinessType;
import com.ustcinfo.extended.common.OrderParam;
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
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

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

    /**
     * 扩展数据详情查询
     *
     * @param businessType 数据业务类型，枚举类
     * @param id           数据主表的主键值
     * @return String, Value的扩展数据map, key为字段名, value为字段值
     */
    public Map queryDetailWithExt(BusinessType businessType, Long id) {
        if (id == null) {
            throw new RuntimeException("请传入非空的id值");
        }
        ExtendedDataEntity extDataEntity = getExtendedDataEntity(businessType);
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

    /**
     * 扩展数据表格查询
     *
     * @param businessType 数据业务类型, 枚举类
     * @param pagination   分页条件
     * @param queryParams  查询条件List
     * @param orderParams  排序条件List
     * @return list为扩展数据集合, list中的元素类型为map, 每一个map对应一个扩展数据对象, 每一个键值对对应扩展数据属性名-属性值
     */
    public List<Map> queryTableWithExt(BusinessType businessType,
                                       Pagination pagination,
                                       List<QueryParam> queryParams,
                                       List<OrderParam> orderParams) {

        ExtendedDataEntity extDataEntity = getExtendedDataEntity(businessType);
        List<ExtendedDataFiled> exConfigFiledList = getExtendedDataFileds(businessType);
        String jpqlStr = queryDataWithExt(exConfigFiledList, extDataEntity);

        HashMap<String, Object> paramMap = new HashMap<>();

        // 拼接查询条件
        StringBuilder queryParamStr = new StringBuilder();
        if (queryParams != null) {
            for (QueryParam queryParam : queryParams) {
                if (queryParam != null) {
                    queryParamStr.append(" and ").append(queryParam.getFiled()).append(" ")
                            .append(queryParam.getLog()).append(" :");
                    queryParamStr.append(queryParam.getFiled());
                    queryParamStr.append(" ");
                    if (Objects.equals(queryParam.getLog(), "like")) {
                        paramMap.put(queryParam.getFiled(), "%" + queryParam.getVal() + "%");
                    } else {
                        paramMap.put(queryParam.getFiled(), queryParam.getVal());
                    }
                }
            }
        }
        jpqlStr += queryParamStr.toString();

        // 拼接排序条件
        StringBuilder orderStr = new StringBuilder();
        if (orderParams != null) {
            orderStr.append(" order by ");
            for (OrderParam orderParam : orderParams) {
                orderStr.append(orderParam.getFiled()).append(" ").append(orderParam.getOrder()).append(", ");
            }
            orderStr.replace(orderStr.length() - 2, orderStr.length(), " ");
        }
        jpqlStr += orderStr.toString();

        List<Map> list = testRepository.findList(jpqlStr, Map.class, paramMap, pagination);

        return list;
    }

    /**
     * 删除扩展数据, 先删扩展表的记录, 再删主表的记录, 事务控制一致性。
     *
     * @param businessType 数据业务类型, 枚举类
     * @param id           主表主键值
     * @return 删除结果，true为成功
     */
    @Transactional
    public boolean deleteDataWithExt(BusinessType businessType, Long id) {
        if (id == null) {
            throw new RuntimeException("删除的id不能为空");
        }

        //查询扩展配置实体表信息
        ExtendedDataEntity extDataEntity = getExtendedDataEntity(businessType);

        HashMap<String, Object> map = new HashMap<>();
        map.put("paramId", id);

        //拼接从表的删除语句
        String jpqlExtendedStr = "delete " + extDataEntity.getExtEntityName() + " " +
                extDataEntity.getExtEntityAlias() + " where " + extDataEntity.getExtEntityAlias() +
                "." + extDataEntity.getExtEntityForeignkey() + "=:paramId";
        int exRows = testRepository.executeUpdate(jpqlExtendedStr, map);
        if (exRows != 1) {
            throw new RuntimeException("删除的从表记录数不唯一, 请确认表配置");
        }
        //拼接主表的删除语句
        String jpqlMainStr = "delete " + extDataEntity.getMainEntityName() + " " +
                extDataEntity.getMainEntityAlias() + " where " + extDataEntity.getMainEntityAlias() +
                "." + extDataEntity.getMainEntityPrimarykey() + "=:paramId";
        int MainRows = testRepository.executeUpdate(jpqlMainStr, map);
        if (MainRows != 1) {
            throw new RuntimeException("删除的主表记录数不唯一, 请确认表配置");
        }

        //return "删除主表记录数: " + MainRows + "; 删除扩展表记录数: " + exRows;
        return true;
    }

    /**
     * @param businessType 数据业务类型, 枚举类
     * @param map          想要更新的主表从表属性map, 需要在配置表中配置的才能被更新。 如果传入value为空则不会更新。
     * @return 更新结果，true为成功
     */
    @Transactional
    public boolean updateDataWithExt(BusinessType businessType, Map<String, Object> map) {

        //检查map是否为空
        if (map == null) {
            throw new RuntimeException("更新的map不能为空");
        }

        //查询扩展配置实体表信息
        ExtendedDataEntity extDataEntity = getExtendedDataEntity(businessType);
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
            mainParamMap.put(mainEntityPrimarykey, map.get(mainEntityPrimarykey));
            extendedParamMap.put(mainEntityPrimarykey, map.get(mainEntityPrimarykey));
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
        logger.info("传入总参数有" + map.size() + "个;" + "主表参数" + mainParamNum + "个,扩展表参数" + extParamNum + "个。");

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
                        mainParamMap.put(filed.getFiledName(), map.get(filed.getFiledName()));
                    }
                }
            }
            jpqlMainStr.replace(jpqlMainStr.length() - 1, jpqlMainStr.length(), " ");
            //拼接where之后的字符串，下同
            jpqlMainStr.append("where ").append(extDataEntity.getMainEntityAlias()).append(".")
                    .append(mainEntityPrimarykey).append("=:")
                    .append(mainEntityPrimarykey);
            mainUpdRows = testRepository.executeUpdate(jpqlMainStr.toString(), mainParamMap);
            if (mainUpdRows != 1) {
                throw new RuntimeException("主表更新记录不唯一");
            }
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
                        extendedParamMap.put(filed.getFiledName(), map.get(filed.getFiledName()));
                    }
                }
            }
            jpqlExtendedStr.replace(jpqlExtendedStr.length() - 1, jpqlExtendedStr.length(), " ");
            jpqlExtendedStr.append("where ").append(extDataEntity.getExtEntityAlias()).append(".")
                    .append(extDataEntity.getExtEntityForeignkey()).append("=:")
                    .append(mainEntityPrimarykey);
            extUpdRows = testRepository.executeUpdate(jpqlExtendedStr.toString(), extendedParamMap);
            if (extUpdRows != 1){
                throw new RuntimeException("从表更新记录不唯一");
            }
        }
        //return "更新主表记录数: " + mainUpdRows + "; 更新扩展表记录数: " + extUpdRows;
        return true;
    }

    /**
     * 新增扩展数据，使用反射实现
     * @param businessType 数据业务类型，枚举类
     * @param map 要新增的扩展数据，map中的键值对为属性名-属性值，id如果设置有数据库自动生成可以不传。
     * @return 新增结果,true为成功
     */
    @Transactional
    public boolean insertDataWithExt(BusinessType businessType, Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            throw new RuntimeException("添加时传入的map为空");
        }
        ExtendedDataEntity dataEntity = getExtendedDataEntity(businessType);
        List<ExtendedDataFiled> fileds = getExtendedDataFileds(businessType);
        try {
            Class<?> mainClass = Class.forName(dataEntity.getMainEntityPackage() + "." + dataEntity.getMainEntityName());
            Class<?> extClass = Class.forName(dataEntity.getExtEntityPackage() + "." + dataEntity.getExtEntityName());
            Object mainObject = mainClass.newInstance();
            Object extObject = extClass.newInstance();
            for (ExtendedDataFiled extendedDataFiled : fileds) {
                if (map.containsKey(extendedDataFiled.getFiledName())) {
                    if (extendedDataFiled.getIsMainEntityFiled() == 1) {
                        setInvokeFiled(map, mainClass, mainObject, extendedDataFiled);
                    }
                    if (extendedDataFiled.getIsMainEntityFiled() == 0) {
                        setInvokeFiled(map, extClass, extObject, extendedDataFiled);
                    }
                }
            }
            // 先保存主数据
            testRepository.save(mainObject);
            // 保存主数据后通过反射获取到它的id值
            Object mainId = null;
            try {
                Field field = mainClass.getDeclaredField(dataEntity.getMainEntityPrimarykey());
                field.setAccessible(true);
                mainId = field.get(mainObject);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            // 将主数据的id值赋值给扩展数据的外键属性
            try {
                Field field = extClass.getDeclaredField(dataEntity.getExtEntityForeignkey());
                field.setAccessible(true);
                field.set(extObject, mainId);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            // 保存扩展数据
            testRepository.save(extObject);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 将map中的键值对(属性名-属性值)按value类型自动转换并赋值给反射对象中与key对应的属性。
     *
     * @param map 存储属性名-属性值的map
     * @param invokeClass 反射类型
     * @param invokeObject 反射对象
     * @param extendedDataFiled 存储反射对象的属性名的对象实例
     * @throws NoSuchFieldException 找不到属性
     * @throws IllegalAccessException 没有访问权限
     */
    private void setInvokeFiled(Map<String, Object> map, Class<?> invokeClass, Object invokeObject, ExtendedDataFiled extendedDataFiled) throws NoSuchFieldException, IllegalAccessException {
        Field field = invokeClass.getDeclaredField(extendedDataFiled.getFiledName());
        field.setAccessible(true);
        String simpleName = field.getType().getSimpleName();
        if (Objects.equals("Long", simpleName)) {
            field.set(invokeObject, Long.valueOf(map.get(extendedDataFiled.getFiledName()).toString()));
        } else if (Objects.equals("String", simpleName)) {
            field.set(invokeObject, map.get(extendedDataFiled.getFiledName()));
        } else if (Objects.equals("Integer", simpleName)) {
            field.set(invokeObject, Integer.valueOf(map.get(extendedDataFiled.getFiledName()).toString()));
        } else if (Objects.equals("Date", simpleName)) {
            Date temp = parseDate(map.get(extendedDataFiled.getFiledName()).toString());
            field.set(invokeObject, temp);
        } else if (Objects.equals("Boolean", simpleName)) {
            field.set(invokeObject, Boolean.valueOf(map.get(extendedDataFiled.getFiledName()).toString()));
        } else if (Objects.equals("Double", simpleName)) {
            field.set(invokeObject, Double.valueOf(map.get(extendedDataFiled.getFiledName()).toString()));
        }
    }

    /**
     * 根据数据业务类型，获取扩展数据字段配置表的记录列表
     * @param businessType 数据业务类型，枚举类
     * @return 字段配置对象list
     */
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

    /**
     * 根据数据业务类型，获取扩展数据实体配置表的一条记录
     * @param businessType 数据业务类型，枚举类
     * @return 扩展数据实体配置对象
     */
    private ExtendedDataEntity getExtendedDataEntity(BusinessType businessType) {
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

    /**
     * 拼接查询jpql的公共地方 select ... from entity1,entity2 where entity1.xx =entity2.xx
     * @param exConfigFiledList 扩展数据字段配置对象列表
     * @param extDataEntity 扩展数据实体配置对象
     * @return jpql: select ... from entity1,entity2 where entity1.xx =entity2.xx
     */
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

    /**
     * 格式化string为Date
     *
     * @param datestr 字符串类型的日期
     * @return date
     */
    private Date parseDate(String datestr) {
        if (null == datestr || "".equals(datestr)) {
            return null;
        }
        try {
            String fmtstr = null;
            if (datestr.indexOf(':') > 0) {
                fmtstr = "yyyy-MM-dd HH:mm:ss";
            } else {
                fmtstr = "yyyy-MM-dd";
            }
            SimpleDateFormat sdf = new SimpleDateFormat(fmtstr, Locale.UK);
            return sdf.parse(datestr);
        } catch (Exception e) {
            return null;
        }
    }
}
