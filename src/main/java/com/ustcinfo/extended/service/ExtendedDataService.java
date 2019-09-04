package com.ustcinfo.extended.service;


import com.ustcinfo.extended.common.BusinessDataType;
import com.ustcinfo.extended.common.OrderParam;
import com.ustcinfo.extended.common.Pagination;
import com.ustcinfo.extended.common.QueryParam;
import com.ustcinfo.extended.entity.ExtendedDataEntity;
import com.ustcinfo.extended.entity.ExtendedDataFiled;
import com.ustcinfo.extended.repository.ExtendedDataRepository;
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
public class ExtendedDataService {

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
    private ExtendedDataRepository extendedDataRepository;

    private Logger logger = LoggerFactory.getLogger(ExtendedDataService.class);

    /**
     * 扩展数据详情查询
     *
     * @param businessDataType 数据业务类型，枚举类
     * @param id               数据主表的主键值
     * @return String, Value的扩展数据map, key为字段名, value为字段值
     */
    public Map queryDetailWithExt(BusinessDataType businessDataType, Long id) {
        if (id == null) {
            throw new RuntimeException("请传入非空的id值");
        }
        ExtendedDataEntity extDataEntity = getExtendedDataEntity(businessDataType);
        List<ExtendedDataFiled> exConfigFiledList = getExtendedDataFileds(businessDataType);

        String jpqlStr = queryDataWithExt(exConfigFiledList, extDataEntity);
        jpqlStr = jpqlStr + " and " + extDataEntity.getMainEntityAlias() + "."
                + extDataEntity.getMainEntityPrimarykey() + "=:id";
        HashMap<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("id", id);
        List<Map> list = extendedDataRepository.findList(jpqlStr, Map.class, paramMap, null);
        if (list == null || list.size() == 0) {
            return null;
        }
        if (list.size() != 1) {
            throw new RuntimeException("查询结果不唯一");
        }
        return list.get(0);
    }

    /**
     * 扩展数据表格查询
     *
     * @param businessDataType 数据业务类型, 枚举类
     * @param pagination       分页条件
     * @param queryParams      查询条件List
     * @param orderParams      排序条件List
     * @return list为扩展数据集合, list中的元素类型为map, 每一个map对应一个扩展数据对象, 每一个键值对对应扩展数据属性名-属性值
     */
    public List<Map> queryTableWithExt(BusinessDataType businessDataType,
                                       Pagination pagination,
                                       List<QueryParam> queryParams,
                                       List<OrderParam> orderParams) {

        ExtendedDataEntity extDataEntity = getExtendedDataEntity(businessDataType);
        List<ExtendedDataFiled> exConfigFiledList = getExtendedDataFileds(businessDataType);

        String queryStr = queryDataWithExt(exConfigFiledList, extDataEntity);

        HashMap<String, Object> paramMap = new HashMap<String, Object>();

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
            queryParamStr.replace(0, 4, "");
            queryStr += queryParamStr;
        }

        // 拼接排序条件
        StringBuilder orderStr = new StringBuilder();
        if (orderParams != null) {
            for (OrderParam orderParam : orderParams) {
                orderStr.append(orderParam.getFiled()).append(" ").append(orderParam.getOrder()).append(", ");
            }
            orderStr.replace(orderStr.length() - 2, orderStr.length(), " ");
            queryStr += orderStr;
        }

        return extendedDataRepository.findList(queryStr, Map.class, paramMap, pagination);
    }

    /**
     * 删除扩展数据, 先删扩展表的记录, 再删主表的记录, 事务控制一致性。
     *
     * @param businessDataType 数据业务类型, 枚举类
     * @param id               主表主键值
     * @return 删除结果，true为成功
     */
    @Transactional
    public boolean deleteDataWithExt(BusinessDataType businessDataType, Long id) {
        if (id == null) {
            throw new RuntimeException("删除的id不能为空");
        }

        //查询扩展配置实体表信息
        ExtendedDataEntity extDataEntity = getExtendedDataEntity(businessDataType);

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("paramId", id);

        //拼接从表的删除语句
        String extTableStr = extDataEntity.getExtEntityName() + " " + extDataEntity.getExtEntityAlias();
        String extParamStr = extDataEntity.getExtEntityAlias() + "."
                + extDataEntity.getExtEntityForeignkey() + "=:paramId";
        String extDeleteStr = String.format("delete %s where %s", extTableStr, extParamStr);
        int exRows = extendedDataRepository.executeUpdate(extDeleteStr, map);
        if (exRows == 0) {
            throw new RuntimeException("删除的从表记录数为0, 请确认表中是否有该id记录");
        }
        if (exRows > 1) {
            throw new RuntimeException("删除的从表记录数大于, 请确认扩展配置表或传入id值是否正确");
        }

        //拼接主表的删除语句
        String mainTableStr = extDataEntity.getMainEntityName() + " " + extDataEntity.getMainEntityAlias();
        String mainParamStr = extDataEntity.getMainEntityAlias() + "."
                + extDataEntity.getMainEntityPrimarykey() + "=:paramId";
        String mainDeleteStr = String.format("delete %s where %s", mainTableStr, mainParamStr);
        int MainRows = extendedDataRepository.executeUpdate(mainDeleteStr, map);
        if (MainRows == 0) {
            throw new RuntimeException("删除的主表记录数为0, 请确认表中是否有该id记录");
        }
        if (MainRows > 1) {
            throw new RuntimeException("删除的主表表记录数大于1, 请确认扩展配置表或传入id值是否正确");
        }

        return true;
    }

    /**
     * @param businessDataType 数据业务类型, 枚举类
     * @param map              想要更新的主表从表属性map, 需要在配置表中配置的才能被更新。 如果传入value为空则不会更新。
     * @return 更新结果，true为成功
     */
    @Transactional
    public boolean updateDataWithExt(BusinessDataType businessDataType, Map<String, Object> map) {

        //检查map是否为空
        if (map == null) {
            throw new RuntimeException("更新的map不能为空");
        }

        //查询扩展配置实体表信息
        ExtendedDataEntity extDataEntity = getExtendedDataEntity(businessDataType);
        List<ExtendedDataFiled> exConfigFiledList = getExtendedDataFileds(businessDataType);
        String mainEntityPrimarykey = extDataEntity.getMainEntityPrimarykey();

        Class<?> mainClass = null;
        Class<?> extClass = null;
        try {
            mainClass = Class.forName(extDataEntity.getMainEntityPackage() + "." + extDataEntity.getMainEntityName());
            extClass = Class.forName(extDataEntity.getExtEntityPackage() + "." + extDataEntity.getExtEntityName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //储存主表/从表更新绑定参数
        HashMap<String, Object> mainParamMap = new HashMap<String, Object>();
        HashMap<String, Object> extendedParamMap = new HashMap<String, Object>();

        //做一遍检查，要求传入的map中必须要有与数据主表属性名一致的key
        if (!map.containsKey(mainEntityPrimarykey)) {
            throw new RuntimeException("请在map中传入主数据表的" + mainEntityPrimarykey + "属性");
        } else if (map.size() == 1) {
            throw new RuntimeException("传入的map中只有主键属性，没有需要更新的属性");
        } else {
            try {
                if (mainClass != null) {
                    Field field = mainClass.getDeclaredField(mainEntityPrimarykey);
                    field.setAccessible(true);
                    String simpleName = field.getType().getSimpleName();
                    switch (simpleName) {
                        case "Long":
                            mainParamMap.put(mainEntityPrimarykey, Long.valueOf(map.get(mainEntityPrimarykey).toString()));
                            extendedParamMap.put(mainEntityPrimarykey, Long.valueOf(map.get(mainEntityPrimarykey).toString()));
                            break;
                        case "String":
                            mainParamMap.put(mainEntityPrimarykey, map.get(mainEntityPrimarykey));
                            extendedParamMap.put(mainEntityPrimarykey, map.get(mainEntityPrimarykey));
                            break;
                        case "Integer":
                            mainParamMap.put(mainEntityPrimarykey, Integer.valueOf(map.get(mainEntityPrimarykey).toString()));
                            extendedParamMap.put(mainEntityPrimarykey, Integer.valueOf(map.get(mainEntityPrimarykey).toString()));
                            break;
                        default:
                            throw new RuntimeException("主键的类型非Long、String、Integer");
                    }
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (NumberFormatException e){
                logger.error("格式转换异常---键：" + mainEntityPrimarykey + "---值：" + map.get(mainEntityPrimarykey).toString());
                e.printStackTrace();
            } catch (NullPointerException e){
                logger.error("传入的主键为空");
                e.printStackTrace();
            }
        }
        int mainParamNum = 0;
        int extParamNum = 0;
        int mainUpdRows;
        int extUpdRows;


        // 拼接主表更新语句
        String updateMainTableStr = extDataEntity.getMainEntityName() + " " + extDataEntity.getMainEntityAlias();
        StringBuilder updateMainSetStr = new StringBuilder();
        String updateMainParamStr;

        //拼接where之前的字符串
        for (ExtendedDataFiled filed : exConfigFiledList) {
            if (filed.getIsMainEntityFiled() == 1) {
                //DONE 判断更新值是否为空， 如果为空就不更新
                if (map.containsKey(filed.getFiledName()) && map.get(filed.getFiledName()) != null) {
                    mainParamNum++;
                    updateMainSetStr.append(extDataEntity.getMainEntityAlias()).append(".")
                            .append(filed.getFiledName()).append("=:").append(filed.getFiledName()).append(",");
                    setUpdateParamMap(map, mainClass, mainParamMap, filed);
                }
            }
        }
        if (mainParamNum > 0){
            updateMainSetStr.replace(updateMainSetStr.length() - 1, updateMainSetStr.length(), " ");
        }
        //拼接where之后的字符串，下同
        updateMainParamStr = extDataEntity.getMainEntityAlias() + "." + mainEntityPrimarykey
                + "=:" + mainEntityPrimarykey;
        String updateMainStr = String.format("update %s set %s where %s", updateMainTableStr, updateMainSetStr, updateMainParamStr);
        if (mainParamNum > 0) {
            mainUpdRows = extendedDataRepository.executeUpdate(updateMainStr, mainParamMap);
            if (mainUpdRows == 0) {
                throw new RuntimeException("主表未成功更新");
            }else if (mainUpdRows > 1){
                throw new RuntimeException("主表更新记录大于1");
            }
        }
        // 拼接从表更新语句
        String updateExtTableStr = extDataEntity.getExtEntityName() + " " + extDataEntity.getExtEntityAlias();
        StringBuilder updateExtSetStr = new StringBuilder();
        String updateExtParamStr;
        for (ExtendedDataFiled filed : exConfigFiledList) {
            if (filed.getIsMainEntityFiled() == 0) {
                if (map.containsKey(filed.getFiledName()) && map.get(filed.getFiledName()) != null) {
                    extParamNum++;
                    updateExtSetStr.append(extDataEntity.getExtEntityAlias()).append(".")
                            .append(filed.getFiledName()).append("=:").append(filed.getFiledName()).append(",");
                    extendedParamMap.put(filed.getFiledName(), map.get(filed.getFiledName()));
                    setUpdateParamMap(map, extClass, extendedParamMap, filed);
                }
            }
        }
        // DONE 判断为“”时不执行操作
        if(extParamNum > 0){
            updateExtSetStr.replace(updateExtSetStr.length() - 1, updateExtSetStr.length(), " ");
        }
        updateExtParamStr = extDataEntity.getExtEntityAlias() + "." + extDataEntity.getExtEntityForeignkey()
                + "=:" + mainEntityPrimarykey;
        String updateExtStr = String.format("update %s set %s where %s", updateExtTableStr, updateExtSetStr, updateExtParamStr);
        if (extParamNum > 0) {
            extUpdRows = extendedDataRepository.executeUpdate(updateExtStr, extendedParamMap);
            if (extUpdRows == 0) {
                throw new RuntimeException("从表未成功更新");
            }else if (extUpdRows > 1){
                throw new RuntimeException("从表更新记录大于1");
            }
        }
        logger.info("传入总参数有" + (mainParamNum + extParamNum) + "个;" + "主表参数" + mainParamNum + "个,扩展表参数" + extParamNum + "个。");
        return true;
    }

    private void setUpdateParamMap(Map<String, Object> map, Class<?> toUpdateClass, HashMap<String, Object> paramMap, ExtendedDataFiled filed) {
        try {
            if (toUpdateClass != null) {
                Field field = toUpdateClass.getDeclaredField(filed.getFiledName());
                field.setAccessible(true);
                String simpleName = field.getType().getSimpleName();
                switch (simpleName) {
                    case "Long":
                        paramMap.put(filed.getFiledName(), Long.valueOf(map.get(filed.getFiledName()).toString()));
                        break;
                    case "String":
                        paramMap.put(filed.getFiledName(), map.get(filed.getFiledName()));
                        break;
                    case "Integer":
                        paramMap.put(filed.getFiledName(), Integer.valueOf(map.get(filed.getFiledName()).toString()));
                        break;
                    case "Date":
                        Date temp = parseDate(map.get(filed.getFiledName()).toString());
                        paramMap.put(filed.getFiledName(), temp);
                        break;
                    case "Boolean":
                        paramMap.put(filed.getFiledName(), Boolean.valueOf(map.get(filed.getFiledName()).toString()));
                        break;
                    case "Double":
                        paramMap.put(filed.getFiledName(), Double.valueOf(map.get(filed.getFiledName()).toString()));
                        break;
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NumberFormatException e){
            logger.error("格式转换异常---键：" + filed.getFiledName() + "---值：" + map.get(filed.getFiledName()).toString());
            e.printStackTrace();
        }
    }

    /**
     * 新增扩展数据，使用反射实现
     *
     * @param businessDataType 数据业务类型，枚举类
     * @param map              要新增的扩展数据，map中的键值对为属性名-属性值，id如果设置有数据库自动生成可以不传。
     * @return 新增结果, true为成功
     */
    @Transactional
    public boolean insertDataWithExt(BusinessDataType businessDataType, Map<String, Object> map) {
        ExtendedDataEntity dataEntity = getExtendedDataEntity(businessDataType);
        List<ExtendedDataFiled> fileds = getExtendedDataFileds(businessDataType);
        //排除传入值为null的键值对
        if (map != null){
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() == null){
                    map.remove(entry.getKey());
                }
            }
        }
        if (map == null || map.isEmpty()) {
            throw new RuntimeException("添加时传入的map为空");
        }
        if (map.containsKey(dataEntity.getMainEntityPrimarykey()) && map.size() == 1){
            throw new RuntimeException("添加时传入的map应至少有一个属性值");
        }
        try {
            Class<?> mainClass = Class.forName(dataEntity.getMainEntityPackage() + "." + dataEntity.getMainEntityName());
            Class<?> extClass = Class.forName(dataEntity.getExtEntityPackage() + "." + dataEntity.getExtEntityName());
            Object mainObject = mainClass.newInstance();
            Object extObject = extClass.newInstance();
            for (ExtendedDataFiled extendedDataFiled : fileds) {
                if (map.containsKey(extendedDataFiled.getFiledName())) {
                    // 不允许主键进行赋值，主键全部由数据库自动生成
                    if (extendedDataFiled.getIsMainEntityFiled() == 1 && !Objects.equals(extendedDataFiled.getFiledName(), dataEntity.getMainEntityPrimarykey())) {
                        setInvokeFiled(map, mainClass, mainObject, extendedDataFiled);
                    }
                    // 不允许外键进行赋值，外键全部在主表生成后，从主表中的主键字段中获得
                    if (extendedDataFiled.getIsMainEntityFiled() == 0 && !Objects.equals(extendedDataFiled.getFiledName(), dataEntity.getExtEntityForeignkey())) {
                        setInvokeFiled(map, extClass, extObject, extendedDataFiled);
                    }
                }
            }
            // 先保存主数据
            extendedDataRepository.save(mainObject);
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
            extendedDataRepository.save(extObject);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 将map中的键值对(属性名-属性值)按value类型自动转换并赋值给反射对象中与key对应的属性。
     *
     * @param map               存储属性名-属性值的map
     * @param invokeClass       反射类型
     * @param invokeObject      反射对象
     * @param extendedDataFiled 存储反射对象的属性名的对象实例
     * @throws NoSuchFieldException   找不到属性
     */
    private void setInvokeFiled(Map<String, Object> map, Class<?> invokeClass, Object invokeObject, ExtendedDataFiled extendedDataFiled) throws NoSuchFieldException {
        Field field = invokeClass.getDeclaredField(extendedDataFiled.getFiledName());
        field.setAccessible(true);
        String simpleName = field.getType().getSimpleName();
        try {
            switch (simpleName) {
                case "Long":
                    field.set(invokeObject, Long.valueOf(map.get(extendedDataFiled.getFiledName()).toString()));
                    break;
                case "String":
                    field.set(invokeObject, map.get(extendedDataFiled.getFiledName()));
                    break;
                case "Integer":
                    field.set(invokeObject, Integer.valueOf(map.get(extendedDataFiled.getFiledName()).toString()));
                    break;
                case "Date":
                    Date temp = parseDate(map.get(extendedDataFiled.getFiledName()).toString());
                    field.set(invokeObject, temp);
                    break;
                case "Boolean":
                    field.set(invokeObject, Boolean.valueOf(map.get(extendedDataFiled.getFiledName()).toString()));
                    break;
                case "Double":
                    field.set(invokeObject, Double.valueOf(map.get(extendedDataFiled.getFiledName()).toString()));
                    break;
            }
        } catch (NumberFormatException e){
            logger.error("格式转换异常---键：" + extendedDataFiled.getFiledName() + "---值：" + map.get(extendedDataFiled.getFiledName()).toString());
            e.printStackTrace();
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据数据业务类型，获取扩展数据字段配置表的记录列表
     *
     * @param businessDataType 数据业务类型，枚举类
     * @return 字段配置对象list
     */
    private List<ExtendedDataFiled> getExtendedDataFileds(BusinessDataType businessDataType) {
        //2.查询ExtendedDataFiled
        String queryDetailStr = String.format("select filed from %s filed, %s entity where entity.%s = filed.%s and entity.%s=:businessCode",
                EX_CONFIG_FILED, EX_CONFIG_ENTITY, EXT_ENTITY_PRIMARYKEY, EXT_FILED_FOREIGNKEY, DATA_TYPE_CODE);
        Map<String, Object> exParamMap = new HashMap<String, Object>();
        exParamMap.put("businessCode", businessDataType.getCode());
        List<ExtendedDataFiled> exConfigDetailList =
                extendedDataRepository.findList(queryDetailStr, ExtendedDataFiled.class, exParamMap, null);

        //对查询的exConfigDetailList做检查
        if (exConfigDetailList == null || exConfigDetailList.isEmpty()) {
            throw new RuntimeException("查询的扩展从配置表为空");
        }
        return exConfigDetailList;
    }

    /**
     * 根据数据业务类型，获取扩展数据实体配置表的一条记录
     *
     * @param businessDataType 数据业务类型，枚举类
     * @return 扩展数据实体配置对象
     */
    private ExtendedDataEntity getExtendedDataEntity(BusinessDataType businessDataType) {
        //1.查询ExtendedDataEntity
        String queryMainStr = "select m from " + EX_CONFIG_ENTITY + " m  where " + DATA_TYPE_CODE + " = :businessCode";
        Map<String, Object> exParamMap = new HashMap<String, Object>();
        exParamMap.put("businessCode", businessDataType.getCode());
        List<ExtendedDataEntity> exConfigMainList =
                extendedDataRepository.findList(queryMainStr, ExtendedDataEntity.class, exParamMap, null);

        //对查询的exConfigMainList做检查
        if (exConfigMainList == null || exConfigMainList.isEmpty()) {
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
     *
     * @param exConfigFiledList 扩展数据字段配置对象列表
     * @param extDataEntity     扩展数据实体配置对象
     * @return jpql: select ... from entity1,entity2 where entity1.xx =entity2.xx
     */
    private String queryDataWithExt(List<ExtendedDataFiled> exConfigFiledList,
                                    ExtendedDataEntity extDataEntity) {

        //根据查询出来的扩展表配置记录，拼接map()括号内语句
        StringBuilder mapFiledStr = new StringBuilder();
        String tableStr;
        String tableEqualStr;

        for (ExtendedDataFiled configFiled : exConfigFiledList) {
            // 如果是主表字段
            if (configFiled.getIsMainEntityFiled() == 1) {
                mapFiledStr.append(extDataEntity.getMainEntityAlias()).append(".")
                        .append(configFiled.getFiledName()).append(" as ")
                        .append(configFiled.getFiledAlias()).append(",");
            }
            // 如果是扩展表字段
            if (configFiled.getIsMainEntityFiled() == 0) {
                mapFiledStr.append(extDataEntity.getExtEntityAlias()).append(".")
                        .append(configFiled.getFiledName()).append(" as ")
                        .append(configFiled.getFiledAlias()).append(",");
            }
        }
        mapFiledStr.replace(mapFiledStr.length() - 1, mapFiledStr.length(), "");

        //(2). 拼接 from之后，where之前的语句
        tableStr = extDataEntity.getMainEntityName() + " " + extDataEntity.getMainEntityAlias()
                + "," + extDataEntity.getExtEntityName() + " " + extDataEntity.getExtEntityAlias();

        //(3). 拼接where 之后的语句
        tableEqualStr = extDataEntity.getMainEntityAlias() + "." + extDataEntity.getMainEntityPrimarykey()
                + "=" + extDataEntity.getExtEntityAlias() + "." + extDataEntity.getExtEntityForeignkey();

        return String.format("select new map(%s) from %s where %s", mapFiledStr.toString(), tableStr, tableEqualStr);

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
            String fmtstr;
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
