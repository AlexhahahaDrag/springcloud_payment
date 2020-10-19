package com.alex.elasticsearch;

import cn.hutool.json.JSONUtil;
import com.alex.elasticsearch.entity.Content;
import com.alex.elasticsearch.utils.ElasticsearchUtil;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ElatsticsearchTest {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void elsticsearchCreateIndiceTest() {
        ElasticsearchUtil elasticsearchUtil = new ElasticsearchUtil(restHighLevelClient);
        Content content = new Content("111", "12", "2");
        elasticsearchUtil.createIndex("test1");
    }

    @Test
    public void elsticsearchCreateDocumentTest() {
        ElasticsearchUtil elasticsearchUtil = new ElasticsearchUtil(restHighLevelClient);
        Content content = new Content("111", "12", "2");
        elasticsearchUtil.createDocument("test1", JSONUtil.parseObj(content));
    }
}
