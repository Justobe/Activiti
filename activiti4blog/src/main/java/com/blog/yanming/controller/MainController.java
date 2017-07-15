package com.blog.yanming.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by YanMing on 2017/7/15.
 */
@Controller
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    /**
     * @Date: 13:29 2017/6/6
     * 跳转到模型管理页面
     */
    @RequestMapping(value = {"/modelPage"})
    public String modelManagePage(){
        return "modelManage";
    }

    /**
     * @Date: 13:30 2017/6/6
     * 跳转到流程管理页面
     */
    @RequestMapping(value = {"/processPage"})
    public String processManagePage(){
        return "processManage";
    }


}