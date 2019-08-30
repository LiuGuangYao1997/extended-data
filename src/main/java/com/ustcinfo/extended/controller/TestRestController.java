package com.ustcinfo.extended.controller;

import com.ustcinfo.extended.common.BusinessType;
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
    public String extDelete(@PathVariable String dataType, @PathVariable Long id) {
        if (Objects.equals(dataType, BusinessType.PRODUCT_INFO_SELECT_1.getCode())) {
            return testService.deleteDataWithExt(BusinessType.PRODUCT_INFO_SELECT_1, id);
        }
        if (Objects.equals(dataType, BusinessType.USER_INFO_SELECT_1.getCode())) {
            return testService.deleteDataWithExt(BusinessType.USER_INFO_SELECT_1, id);
        } else {
            return "请确认您请求的数据代码是否正确";
        }
    }

    @RequestMapping("/test/extUpdate/{dataType}")
    public String extUpdate(@PathVariable String dataType, @RequestBody Map<String, Object> map) {
        if (Objects.equals(dataType, BusinessType.PRODUCT_INFO_SELECT_1.getCode())) {
            return testService.updateDataWithExt(BusinessType.PRODUCT_INFO_SELECT_1, map);
        }
        if (Objects.equals(dataType, BusinessType.USER_INFO_SELECT_1.getCode())) {
            return testService.updateDataWithExt(BusinessType.USER_INFO_SELECT_1, map);
        } else {
            return "请确认您请求的数据代码是否正确";
        }
    }
}
