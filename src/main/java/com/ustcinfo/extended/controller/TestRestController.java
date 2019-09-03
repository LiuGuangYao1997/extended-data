package com.ustcinfo.extended.controller;

import com.ustcinfo.extended.common.BusinessType;
import com.ustcinfo.extended.common.OrderParam;
import com.ustcinfo.extended.common.Pagination;
import com.ustcinfo.extended.common.QueryParam;
import com.ustcinfo.extended.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class TestRestController {

    @Autowired
    private TestService testService;

    @RequestMapping("/test/extDelete/{dataType}/{id}")
    public boolean extDelete(@PathVariable String dataType, @PathVariable Long id) {
        if (Objects.equals(dataType, BusinessType.PRODUCT_INFO_SELECT_1.getCode())) {
            return testService.deleteDataWithExt(BusinessType.PRODUCT_INFO_SELECT_1, id);
        }
        if (Objects.equals(dataType, BusinessType.USER_INFO_SELECT_1.getCode())) {
            return testService.deleteDataWithExt(BusinessType.USER_INFO_SELECT_1, id);
        } else {
            return false;
        }
    }

    @RequestMapping("/test/extUpdate/{dataType}")
    public boolean extUpdate(@PathVariable String dataType, @RequestBody Map<String, Object> map) {
        if (Objects.equals(dataType, BusinessType.PRODUCT_INFO_SELECT_1.getCode())) {
            return testService.updateDataWithExt(BusinessType.PRODUCT_INFO_SELECT_1, map);
        }
        if (Objects.equals(dataType, BusinessType.USER_INFO_SELECT_1.getCode())) {
            return testService.updateDataWithExt(BusinessType.USER_INFO_SELECT_1, map);
        } else {
            return false;
        }
    }

    @RequestMapping("/test/extInsert/{dataType}")
    public boolean extInsert(@PathVariable String dataType, @RequestBody Map<String, Object> map) {
        if (Objects.equals(dataType, BusinessType.PRODUCT_INFO_SELECT_1.getCode())) {
            return testService.insertDataWithExt(BusinessType.PRODUCT_INFO_SELECT_1, map);
        }
        if (Objects.equals(dataType, BusinessType.USER_INFO_SELECT_1.getCode())) {
            return testService.insertDataWithExt(BusinessType.USER_INFO_SELECT_1, map);
        } else {
            return false;
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
        if (Objects.equals(dataType, BusinessType.PRODUCT_INFO_SELECT_1.getCode())) {
            return testService.queryTableWithExt(BusinessType.PRODUCT_INFO_SELECT_1, pagination,queryParams,orderParams);
        }
        if (Objects.equals(dataType, BusinessType.USER_INFO_SELECT_1.getCode())) {
            return testService.queryTableWithExt(BusinessType.USER_INFO_SELECT_1, pagination, queryParams, orderParams);
        } else {
            return null;
        }
    }
}
