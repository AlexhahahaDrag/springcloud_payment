package com.alex.elasticsearch.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alex.elasticsearch.entity.Content;
import com.alex.elasticsearch.utils.ElasticsearchUtil;
import com.alex.elasticsearch.utils.HtmlParseUtil;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @author:      alex
 * @createTime:  2020/10/16 11:46
 * @version:     1.0
 */
@Service
public class ContentService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * @description:  从jd上爬取上去列表，并加入到指定的es中去；
     * @author:       alex
     * @param:        keyword 关键字
     * @param         indexName 索引
     * @return:
     */
    public void findInfoFromJD(String keyword, String indexName) throws IOException {
        //根据关键字到jd爬取数据
        List<Content> contentList = HtmlParseUtil.parseJD(keyword);
        ElasticsearchUtil elasticsearchUtil = new ElasticsearchUtil(restHighLevelClient);
        //将内容转换成json
        List<JSONObject> res = contentList.stream().map(JSONUtil::parseObj).collect(Collectors.toList());
        //如果索引不存在，就创建一个新的索引
        if (!elasticsearchUtil.indexExists(indexName))
            elasticsearchUtil.createIndex(indexName);
        //批量导入数据到指定的索引中
        elasticsearchUtil.bulkDocument(indexName, res);
    }

    public void search(String keyword, String pageNo, String pageSize) {
    }
}
