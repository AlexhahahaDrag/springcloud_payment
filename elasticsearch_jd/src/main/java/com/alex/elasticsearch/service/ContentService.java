package com.alex.elasticsearch.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alex.elasticsearch.entity.Content;
import com.alex.elasticsearch.utils.ElasticsearchUtil;
import com.alex.elasticsearch.utils.HtmlParseUtil;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.query.HighlightQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        ElasticsearchUtil elasticsearchUtil = new ElasticsearchUtil(restHighLevelClient);
        //根据关键字到jd爬取数据
        List<Content> contentList = HtmlParseUtil.parseJD(keyword);
        //将内容转换成json
        List<JSONObject> res = contentList.stream().map(JSONUtil::parseObj).collect(Collectors.toList());
        System.out.println(res);
        //如果索引不存在，就创建一个新的索引
        if (!elasticsearchUtil.indexExists(indexName))
            elasticsearchUtil.createIndex(indexName);
        //批量导入数据到指定的索引中
        elasticsearchUtil.bulkDocument(indexName, res);
    }

    public List<Content> search(String keyword, Integer pageNo, Integer pageSize) throws IOException {
        if (pageNo == null || pageNo < 0)
            pageNo = 0;
        if (pageSize == null || pageSize < 0)
            pageSize = 10;
        ElasticsearchUtil elasticsearchUtil = new ElasticsearchUtil(restHighLevelClient);
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页
        searchSourceBuilder.from(pageNo * pageSize);
        searchSourceBuilder.size(pageSize);
        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span class='key' style='color:red'>");
        highlightBuilder.postTags("</span>");
        highlightBuilder.field("name");
        searchSourceBuilder.highlighter(highlightBuilder);
        //设置查询
        MatchQueryBuilder termQueryBuilder = QueryBuilders.matchQuery("name", keyword);
        searchSourceBuilder.query(termQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] hitsArray = search.getHits().getHits();
        List<Content> res = new ArrayList<>();
        for (SearchHit hit : hitsArray) {
            Content content = JSONUtil.toBean(hit.getSourceAsString(), Content.class);
            HighlightField name = hit.getHighlightFields().get("name");
            System.out.println(name.getFragments()[0]);
            content.setName(name.getFragments()[0].string());
            res.add(content);
        }
        return res;
    }
}
