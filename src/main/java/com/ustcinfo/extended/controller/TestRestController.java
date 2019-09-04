package com.ustcinfo.extended.controller;

import com.ustcinfo.extended.common.BusinessDataType;
import com.ustcinfo.extended.common.OrderParam;
import com.ustcinfo.extended.common.Pagination;
import com.ustcinfo.extended.common.QueryParam;
import com.ustcinfo.extended.service.ExtendedDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class TestRestController {

    @Autowired
    private ExtendedDataService extendedDataService;

    @RequestMapping("/test/extDelete/{dataType}/{id}")
    public boolean extDelete(@PathVariable String dataType, @PathVariable Long id) {
        if (Objects.equals(dataType, BusinessDataType.PRODUCT_INFO_SELECT_1.getCode())) {
            return extendedDataService.deleteDataWithExt(BusinessDataType.PRODUCT_INFO_SELECT_1, id);
        }
        if (Objects.equals(dataType, BusinessDataType.USER_INFO_SELECT_1.getCode())) {
            return extendedDataService.deleteDataWithExt(BusinessDataType.USER_INFO_SELECT_1, id);
        } else {
            return false;
        }
    }

    @RequestMapping("/test/extUpdate/{dataType}")
    public boolean extUpdate(@PathVariable String dataType, @RequestBody Map<String, Object> map) {
        if (Objects.equals(dataType, BusinessDataType.PRODUCT_INFO_SELECT_1.getCode())) {
            return extendedDataService.updateDataWithExt(BusinessDataType.PRODUCT_INFO_SELECT_1, map);
        }
        if (Objects.equals(dataType, BusinessDataType.USER_INFO_SELECT_1.getCode())) {
            return extendedDataService.updateDataWithExt(BusinessDataType.USER_INFO_SELECT_1, map);
        } else {
            return false;
        }
    }

    @RequestMapping("/test/extInsert/{dataType}")
    public boolean extInsert(@PathVariable String dataType, @RequestBody Map<String, Object> map) {
        if (Objects.equals(dataType, BusinessDataType.PRODUCT_INFO_SELECT_1.getCode())) {
            return extendedDataService.insertDataWithExt(BusinessDataType.PRODUCT_INFO_SELECT_1, map);
        }
        if (Objects.equals(dataType, BusinessDataType.USER_INFO_SELECT_1.getCode())) {
            return extendedDataService.insertDataWithExt(BusinessDataType.USER_INFO_SELECT_1, map);
        } else {
            return false;
        }
    }

    @RequestMapping("/test/extQueryDetail/{dataType}/{id}")
    public Map extInsert(@PathVariable String dataType, @PathVariable Long id) {
        if (Objects.equals(dataType, BusinessDataType.PRODUCT_INFO_SELECT_1.getCode())) {
            return extendedDataService.queryDetailWithExt(BusinessDataType.PRODUCT_INFO_SELECT_1, id);
        }
        if (Objects.equals(dataType, BusinessDataType.USER_INFO_SELECT_1.getCode())) {
            return extendedDataService.queryDetailWithExt(BusinessDataType.USER_INFO_SELECT_1, id);
        } else {
            return null;
        }
    }

    @RequestMapping("/test/extQueryTable/{dataType}")
    public List<Map> extQueryTable(@PathVariable String dataType) {
        Pagination pagination = new Pagination();
        pagination.setPage(1);
        pagination.setPerpage(5);
        ArrayList<QueryParam> queryParams = new ArrayList<>();
        QueryParam queryParam1 = new QueryParam("username", "like", "ming");
        QueryParam queryParam2 = new QueryParam("age", ">=", "18");
        queryParams.add(queryParam1);
        queryParams.add(queryParam2);
        List<OrderParam> orderParams = Arrays.asList(new OrderParam("age", "desc"), new OrderParam("username", "asc"));
        if (Objects.equals(dataType, BusinessDataType.PRODUCT_INFO_SELECT_1.getCode())) {
            return extendedDataService.queryTableWithExt(BusinessDataType.PRODUCT_INFO_SELECT_1, pagination,queryParams,orderParams);
        }
        if (Objects.equals(dataType, BusinessDataType.USER_INFO_SELECT_1.getCode())) {
            return extendedDataService.queryTableWithExt(BusinessDataType.USER_INFO_SELECT_1, pagination, queryParams, orderParams);
        } else {
            return null;
        }
    }
}
