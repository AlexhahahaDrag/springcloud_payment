package com.alex.elasticsearch.controller;

import com.alex.elasticsearch.entity.Content;
import com.alex.elasticsearch.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

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
    public List<Content> search(@PathVariable("keyword") String keyword,
                         @PathVariable(value = "pageNo", required = false) Integer pageNo,
                         @PathVariable(value = "pageSize", required = false) Integer pageSize) throws IOException {
        return contentService.search(keyword, pageNo, pageSize);
    }
}
