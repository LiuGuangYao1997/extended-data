package com.ustcinfo.extended.controller;

import com.ustcinfo.extended.common.BusinessType;
import com.ustcinfo.extended.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
public class TestRestController {

    @Autowired
    private TestService testService;

    
    @RequestMapping("/test/extQuery/{dataType}")
    public List extQuery(@PathVariable String dataType){
        if (Objects.equals(dataType, BusinessType.PRODUCT_INFO_SELECT_1.getCode())){
            return testService.queryDataWithExt(BusinessType.PRODUCT_INFO_SELECT_1);
        }
        if (Objects.equals(dataType, BusinessType.USER_INFO_SELECT_1.getCode())){
            return testService.queryDataWithExt(BusinessType.USER_INFO_SELECT_1);
        }else {
            return null;
        }
    }

    @RequestMapping("/test/extDelete/{dataType}/{id}")
    public String extDelete(@PathVariable String dataType, @PathVariable Long id){
        if (Objects.equals(dataType, BusinessType.PRODUCT_INFO_SELECT_1.getCode())){
            return testService.deleteDataWithExt(BusinessType.PRODUCT_INFO_SELECT_1, id);
        }
        if (Objects.equals(dataType, BusinessType.USER_INFO_SELECT_1.getCode())){
            return testService.deleteDataWithExt(BusinessType.USER_INFO_SELECT_1, id);
        }else {
            return null;
        }
    }
}
