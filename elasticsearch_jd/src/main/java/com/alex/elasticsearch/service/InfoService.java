package com.alex.elasticsearch.service;

import com.alex.elasticsearch.dao.InfoMapper;
import com.alex.elasticsearch.entity.Info;
import com.alex.elasticsearch.utils.HtmlParseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class InfoService {

    @Autowired
    private InfoMapper infoMapper;

    public void findInfoFromCamel(String keyword) throws IOException {
        //根据关键字到jd爬取数据
        List<Info> contentList = HtmlParseUtil.parseCamel(keyword);
        for (Info info: contentList) {
            infoMapper.insertInfo(info.getName(), info.getDescription(), info.getDefaultv(), info.getType(), keyword);
        }
    }
}
