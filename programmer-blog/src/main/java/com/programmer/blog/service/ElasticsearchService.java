package com.programmer.blog.service;


import com.programmer.blog.domain.ElasticsearchIndex;
import com.programmer.blog.domain.ElasticsearchProperties;
import com.programmer.blog.domain.Result;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * ElasticSearch业务逻辑类
 *
 * @author dengweiping
 * @date 2021/1/19 14:26
 */
@Slf4j
@Service
public class ElasticsearchService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchService.class);

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 创建索引
     */
    public void createIndexTest() throws IOException {
        //1 准备索引的setting
        Settings.Builder settins = Settings.builder()
                .put("number_of_shards", 1)
                .put("number_of_replicas", 0);

        //2 准备索引的mapping
        XContentBuilder mappings = JsonXContent.contentBuilder()
                .startObject()
                .startObject("properties")
                .startObject("name")
                .field("type", "text")
                .endObject()
                .startObject("age")
                .field("type", "integer")
                .endObject()
                .startObject("birthday")
                .field("type", "date")
                .field("format", "yyyy-MM-dd")
                .endObject()
                .endObject()
                .endObject();
        //3 封装request对象
        CreateIndexRequest request = new CreateIndexRequest("aaa")
                .settings(settins)
                .mapping(mappings);
        //4 使用client去连接ES
        CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        boolean acknowledged = response.isAcknowledged();
        System.out.println(acknowledged);
    }

    /**
     * 创建索引
     *
     * @param elasticSearchIndex
     * @return
     */
    public Result createIndex(ElasticsearchIndex elasticSearchIndex) {
 /*       try {
            //1 准备索引的setting
            Settings.Builder settins = Settings.builder()
                    .put("number_of_shards", 1)
                    .put("number_of_replicas", 0);

            //2 准备索引的mapping
            XContentBuilder mappings = JsonXContent.contentBuilder()
                    .startObject()
                    .startObject("properties")
                    .startObject("name")
                    .field("type", "text")
                    .endObject()
                    .startObject("age")
                    .field("type", "integer")
                    .endObject()
                    .startObject("birthday")
                    .field("type", "date")
                    .field("format", "yyyy-MM-dd")
                    .endObject()
                    .endObject()
                    .endObject();
            //3 封装request对象
            CreateIndexRequest request = new CreateIndexRequest(elasticSearchIndex.getIndexName())
                    .settings(settins)
                    .mapping(mappings);
            //4 使用client去连接ES
            CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
            boolean acknowledged = response.isAcknowledged();
            System.out.println(acknowledged);
        } catch (IOException e) {
            e.printStackTrace();
        }

       return null;*/

        try {
            CreateIndexRequest request = new CreateIndexRequest(elasticSearchIndex.getIndexName());
            request.settings(Settings.builder()
                    .put("index.number_of_shards", elasticSearchIndex.getNumberOfShards())
                    .put("index.number_of_replicas", elasticSearchIndex.getNumberOfReplicas())
            );
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject()
                    .field("properties")
                    .startObject();
            {
                for (ElasticsearchProperties elasticSearchProperties : elasticSearchIndex.getProperties()) {
                    builder.field(elasticSearchProperties.getName())
                            .startObject()
                            .field("index", "true");

                    if (elasticSearchProperties.getType() != null) {
                        builder.field("type", elasticSearchProperties.getType());
                    }
                    if (elasticSearchProperties.getAnalyzer() != null) {
                        builder.field("analyzer", elasticSearchProperties.getAnalyzer());
                    }
                    if (elasticSearchProperties.getSearchAnalyzer() != null) {
                        builder.field("search_analyzer", elasticSearchProperties.getSearchAnalyzer());
                    }
                    builder.endObject();
                }

            }
            builder.endObject();
            builder.endObject();
            request.mapping(builder);
            //2客户端执行请求，请求后获得响应
            CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
            LOGGER.info("create index response : {}", response);
            return Result.success(response);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("create index error info : {}", e);
            return Result.error(e.getMessage());
        }
    }

}
