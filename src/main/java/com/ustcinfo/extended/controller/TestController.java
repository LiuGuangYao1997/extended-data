package com.ustcinfo.extended.controller;

import com.ustcinfo.extended.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @RequestMapping("/test/findExtended")
    public List findExtended(){
        return  testService.findExtended();
    }
}
