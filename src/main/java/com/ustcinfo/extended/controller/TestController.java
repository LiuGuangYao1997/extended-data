package com.ustcinfo.extended.controller;

import com.ustcinfo.extended.common.DataType;
import com.ustcinfo.extended.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
public class TestController {

    @Autowired
    private TestService testService;

    
    @RequestMapping("/test/extQuery/{dataType}")
    public List extQuery(@PathVariable String dataType){
        if (Objects.equals(dataType, DataType.PRODUCT_INFO_SELECT_1.getCode())){
            return testService.queryDataWithExt(DataType.PRODUCT_INFO_SELECT_1);
        }
        if (Objects.equals(dataType, DataType.USER_INFO_SELECT_1.getCode())){
            return testService.queryDataWithExt(DataType.USER_INFO_SELECT_1);
        }else {
            return null;
        }
    }
}
