package com.programmer.blog.domain;

import lombok.Data;

import java.util.List;

/**
 * elasticsearch索引实体
 *
 * @author dengweiping
 * @date 2021/1/23 15:45
 */
@Data
public class ElasticsearchIndex {
    private String indexName;
    private int numberOfShards;
    private int numberOfReplicas;
    private List<ElasticsearchProperties> properties;
}
