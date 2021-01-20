package com.programmer.blog.service;


import com.alibaba.fastjson.JSON;
import com.programmer.blog.domain.BlogPaper;
import com.programmer.blog.domain.Pagination;
import com.programmer.blog.domain.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.ShardSearchFailure;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
            return Result.success(response);
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
            LOGGER.error("query elasticsearch error :{}", e);
        }

        return null;
    }

    /**
     * 分页查询
     */
    public BlogPaper queryDocument() {
        try {
            // 获取请求对象
            GetRequest getRequest = new GetRequest("blog", "md", null);
            // 获取文档信息
            GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            // 将 JSON 转换成对象
            if (getResponse.isExists()) {
                BlogPaper responseBlog = JSON.parseObject(getResponse.getSourceAsBytes(), BlogPaper.class);
                return responseBlog;
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("query elasticsearch error :{}", e);
        }

        return null;
    }

    /**
     * 使用分词查询,并分页
     *
     * @return
     */
    public Map<String, Object> search(List<String> indices, Pagination pagination, QueryBuilder queryBuilder, List<SortBuilder> sortBuilders, List<String> highlightFields) throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        int page = pagination.getPage() - 1;
        int pageSize = pagination.getPageSize();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        if (queryBuilder != null) {
            sourceBuilder.query(queryBuilder);
        } else {
            // 默认全部
            sourceBuilder.query(QueryBuilders.matchAllQuery());
        }

        sourceBuilder.from(page * pageSize);
        sourceBuilder.size(pageSize);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        // 排序
        sourceBuilder.sort(new FieldSortBuilder("createDate").order(SortOrder.DESC));
        if (sortBuilders != null) {
            for (SortBuilder sortBuilder : sortBuilders) {
                sourceBuilder.sort(sortBuilder);
            }
        }

        // 过滤 "_source"
//        sourceBuilder.fetchSource(false);

        //返回字段
        String[] includeFields = new String[]{"id", "title", "description", "author", "createDate"};
        //排除字段
        String[] excludeFields = new String[]{"content"};
        sourceBuilder.fetchSource(includeFields, excludeFields);

        // 高亮
//        HighlightBuilder highlightBuilder = new HighlightBuilder();
//        HighlightBuilder.Field highlightTitle = new HighlightBuilder.Field("title");
//        highlightTitle.highlighterType("unified");
//        highlightBuilder.field(highlightTitle);
//        HighlightBuilder.Field highlightUser = new HighlightBuilder.Field("user");
//        highlightBuilder.field(highlightUser);
//        sourceBuilder.highlighter(highlightBuilder);
        HighlightBuilder highlightBuilder = null;
        if (highlightFields != null) {
            highlightBuilder = new HighlightBuilder();
            for (String field : highlightFields) {
                HighlightBuilder.Field highlightField = new HighlightBuilder.Field(field);
                highlightBuilder.field(highlightField);
            }
        }
        if (highlightBuilder != null) {
            sourceBuilder.highlighter(highlightBuilder);
        }
        String[] ind = indices.toArray(new String[indices.size()]);
        searchRequest.indices(ind);
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        RestStatus status = searchResponse.status();
        TimeValue took = searchResponse.getTook();
        Boolean terminatedEarly = searchResponse.isTerminatedEarly();
        boolean timedOut = searchResponse.isTimedOut();

        int totalShards = searchResponse.getTotalShards();
        int successfulShards = searchResponse.getSuccessfulShards();
        int failedShards = searchResponse.getFailedShards();
        for (ShardSearchFailure failure : searchResponse.getShardFailures()) {
            // failures should be handled here
        }

        SearchHits hits = searchResponse.getHits();
        TotalHits totalHits = hits.getTotalHits();
        // the total number of hits, must be interpreted in the context of totalHits.relation
        long numHits = totalHits.value;
        // whether the number of hits is accurate (EQUAL_TO) or a lower bound of the total (GREATER_THAN_OR_EQUAL_TO)
        TotalHits.Relation relation = totalHits.relation;
        float maxScore = hits.getMaxScore();

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> newPagination = new HashMap<>();
        newPagination.put("page", page);
        newPagination.put("pageSize", pageSize);
        newPagination.put("total", numHits);
        result.put("pagination", newPagination);
        List<Map<String, Object>> data = new ArrayList<>();

        // 查询结果
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            // do something with the SearchHit

            String id = hit.getId();
            float score = hit.getScore();

//            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            data.add(sourceAsMap);

//            String documentTitle = (String) sourceAsMap.get("title");
//            List<Object> users = (List<Object>) sourceAsMap.get("user");
//            Map<String, Object> innerObject = (Map<String, Object>) sourceAsMap.get("innerObject");

            // 高亮
//            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
//            HighlightField highlight = highlightFields.get("title");
//            Text[] fragments = highlight.fragments();
//            String fragmentString = fragments[0].string();
        }

//        Suggest suggest = searchResponse.getSuggest();
//        TermSuggestion termSuggestion = suggest.getSuggestion("suggest_user");
//        for (TermSuggestion.Entry entry : termSuggestion.getEntries()) {
//            for (TermSuggestion.Entry.Option option : entry) {
//                String suggestText = option.getText().string();
//            }
//        }

        result.put("data", data);

        return result;
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
