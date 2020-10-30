package com.alex.elasticsearch.controller;

import com.alex.elasticsearch.service.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {

    @Autowired
    private InfoService infoService;

    @GetMapping("/findInfoCamel")
    public String findInfoFromJD(@RequestParam("keyword") String keyword) throws Exception{
        infoService.findInfoFromCamel(keyword);
        return "success";
    }
}
