package com.alex.elasticsearch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @description: 
 * @author:      alex
 * @createTime:  2020/10/15 16:36 
 * @version:     1.0
 */
@Controller
public class IndexController {


    /**
     * 获取模版首页
     * @return
     */
    @GetMapping({"/","/index"})
    public String index(){
        return "index";
    }
}
