package com.alex.elasticsearch.utils;

import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ElasticsearchUtil {

    @NonNull
    private RestHighLevelClient client;

    /**
     * 添加日志索引库
     */
    public boolean createIndex(String name) {
        //判断索引是否存在;
        boolean flag = false;
        if (!indexExists(name)) {
            log.info("创建索引:{}", name);
            try {
                CreateIndexResponse createIndexResponse = client.indices().create(new CreateIndexRequest(name), RequestOptions.DEFAULT);
                flag = createIndexResponse.isAcknowledged();
            } catch (IOException e) {
                log.info("创建索引{}失败", name);
                e.printStackTrace();
                return false;
            }
        }
        return flag;
    }

    /**
     * 判断索引库是否存在
     */
    public boolean indexExists(String name) {
        GetIndexRequest getIndexRequest = new GetIndexRequest(name);
        log.info("判断索引是否存在：{}", name);
        boolean isExists = false;
        try {
            isExists = client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isExists;
    }

    /**
     * 测试删除索引库
     */
    public boolean deleteIndex(String name) {
        //判断索引是否存在;
        boolean flag = false;
        if (indexExists(name)) {
            log.info("创建索引:{}", name);
            try {
                AcknowledgedResponse acknowledgedResponse = client.indices().delete(new DeleteIndexRequest(name), RequestOptions.DEFAULT);
                flag = acknowledgedResponse.isAcknowledged();
            } catch (IOException e) {
                log.info("创建索引{}失败", name);
                e.printStackTrace();
                return false;
            }
        }
        return flag;
    }

    /**
     * 测试添加日志记录文档
     */
    public boolean createDocument(String indexName, JSONObject jsonObject) {
        IndexRequest indexRequest = new IndexRequest(indexName);
        indexRequest.id("1");
        indexRequest.timeout(TimeValue.timeValueSeconds(2));
        indexRequest.source(jsonObject, XContentType.JSON);
        boolean flag = false;
        try {
            log.info("在索引{}，创建文档:{}", indexName, jsonObject);
            IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
            RestStatus status = indexResponse.status();
            flag = RestStatus.OK.equals(status);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 判断文档是否存在
     * @param indexName  索引名称
     * @param id  文档id
     * @return
     */
    public boolean documentExists(String indexName, String id) {
        GetRequest getRequest = new GetRequest();
        getRequest.index(indexName);
        getRequest.id(id);
        boolean flag = false;
        try {
            log.info("判断在索引中{}，是否存在id:{}的文档", indexName, id);
            flag =  client.exists(getRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * 根据id获取文档
     * @param indexName  索引名称
     * @param id  文档id
     * @return
     */
    public Map<String, Object> getDocument(String indexName, String id) {
        Map<String, Object> source = null;
        if (documentExists(indexName, id)) {
            GetRequest getRequest = new GetRequest();
            getRequest.index(indexName);
            getRequest.id(id);
            try {
                GetResponse documentFields = client.get(getRequest, RequestOptions.DEFAULT);
                source = documentFields.getSource();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return source;
    }

    /**
     * 根据文档id修改文档信息
     * @param indexName  索引名称
     * @param id  文档id
     * @param jsonObject  文档
     */
    public boolean updateDocument(String indexName, String id, JSONObject jsonObject) {
        boolean flag = false;
        if (documentExists(indexName, id)) {
            UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.index(indexName);
            updateRequest.id(id);
            updateRequest.doc(jsonObject);
            try {
                log.info("更新索引{}文档{}信息{}", indexName, id, jsonObject);
                UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
                flag = RestStatus.OK.equals(updateResponse.status());
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return flag;
    }


    /**
     * 删除文档
     * @param indexName  索引名称
     * @param id  文档id
     */
    public boolean deleteDocument(String indexName, String id) {
        boolean flag = false;
        if (documentExists(indexName, id)) {
            DeleteRequest deleteRequest = new DeleteRequest();
            deleteRequest.index(indexName);
            deleteRequest.id(id);
            try {
                log.info("删除索引{}文档{}信息{}", indexName, id);
                DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
                flag = RestStatus.OK.equals(deleteResponse.status());
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return flag;
    }

    /**
     * 批量插入文档数据
     * @param indexName 索引
     * @param list 批量文档
     * @return
     */
    public boolean bulkDocument(String indexName, List<JSONObject> list) {
        boolean flag;
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout(TimeValue.timeValueSeconds(3));
        AtomicInteger index = new AtomicInteger(1);
        list.forEach(jsonObject -> {
            bulkRequest.add(new IndexRequest(indexName).id(String.valueOf(index.getAndIncrement())).source(jsonObject, XContentType.JSON));
        });
        try {
            log.info("索引{}批量插入文档数据{}", indexName, list);
            BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            flag = RestStatus.OK.equals(bulkResponse.status());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return flag;
    }

}
