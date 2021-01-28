package com.programmer.blog.domain;

import lombok.Data;

/**
 * elasticsearch索引字段实体
 *
 * @author dengweiping
 * @date 2021/1/23 15:48
 */
@Data
public class ElasticsearchProperties {
    private String name;
    private String type;
    private String analyzer;
    private String searchAnalyzer;
}
