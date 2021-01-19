package com.programmer.blog.service;


import com.alibaba.fastjson.JSON;
import com.programmer.blog.domain.BlogPaper;
import com.programmer.blog.domain.Result;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 文档业务逻辑类
 *
 * @author dengweiping
 * @date 2021/1/19 14:26
 */
@Slf4j
@Service
public class DocumentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentService.class);

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 增加文档信息
     */
    public Result addDocument(BlogPaper blog) {
        try {
            // 创建索引请求对象
            IndexRequest indexRequest = new IndexRequest("blog", "md", blog.getId());
            // 将对象转换为 byte 数组
            byte[] json = JSON.toJSONBytes(blog);
            // 设置文档内容
            indexRequest.source(json, XContentType.JSON);
            // 执行增加文档
            IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);

            LOGGER.info("result response :{}", response);
            return Result.success();
        } catch (Exception e) {
            return Result.error("创建文档失败", e.getMessage());
        }
    }

    /**
     * 获取文档信息
     */
    public BlogPaper getDocument(String id) {
        try {
            // 获取请求对象
            GetRequest getRequest = new GetRequest("blog", "md", id);
            // 获取文档信息
            GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            // 将 JSON 转换成对象
            if (getResponse.isExists()) {
                BlogPaper responseBlog = JSON.parseObject(getResponse.getSourceAsBytes(), BlogPaper.class);
                return responseBlog;
            }
        } catch (IOException e) {
            LOGGER.error("query error :{}", e);
        }

        return null;
    }

    /**
     * 更新文档信息
     */
    public void updateDocument(BlogPaper blogPaper) {
        try {
            // 创建索引请求对象
            UpdateRequest updateRequest = new UpdateRequest("blog", "md", blogPaper.getId());
            // 将对象转换为 byte 数组
            byte[] json = JSON.toJSONBytes(blogPaper);
            // 设置更新文档内容
            updateRequest.doc(json, XContentType.JSON);
            // 执行更新文档
            UpdateResponse response = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            log.info("创建状态：{}", response.status());
        } catch (Exception e) {
            log.error("", e);
        }
    }

    /**
     * 删除文档信息
     */
    public void deleteDocument(String id) {
        try {
            // 创建删除请求对象
            DeleteRequest deleteRequest = new DeleteRequest("blog", "md", id);
            // 执行删除文档
            DeleteResponse response = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            log.info("删除状态：{}", response.status());
        } catch (IOException e) {
            log.error("", e);
        }
    }
}
