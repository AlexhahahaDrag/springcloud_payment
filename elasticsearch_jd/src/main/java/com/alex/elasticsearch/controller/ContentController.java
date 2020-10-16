package com.alex.elasticsearch.controller;

import com.alex.elasticsearch.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContentController {

    @Autowired
    private ContentService contentService;

    @GetMapping("/findInfoFromJD/{keyword}")
    public String findInfoFromJD(@PathVariable("keyword") String keyword,
                                 @RequestParam("indexName") String indexName) throws Exception{
        contentService.findInfoFromJD(keyword, indexName);
        return "success";
    }

    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    public String search(@PathVariable("keyword") String keyword,
                         @PathVariable("pageNo") String pageNo,
                         @PathVariable("pageSize") String pageSize) {
        contentService.search(keyword, pageNo, pageSize);;
        return "success";
    }
}
