package com.ustcinfo.extended;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SpringBootApplication
public class ExtendedDataDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExtendedDataDemoApplication.class, args);
    }

    @Component
    public class AExceptionHandler implements HandlerExceptionResolver {
        @Override
        public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
            response.setCharacterEncoding("utf-8");
            return null;
        }
    }

}
