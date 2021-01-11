package com.programmer.util.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 生成代码参数实体类
 *
 * @author dengweiping
 * @date 2021/1/9 17:11
 */
@Data
public class GeneratorParamsDTO {

    //表名列表
    private List<String> tables;

    //模块名
    private String moduleName;

    //包名
    private String packageName;

    //作者
    private String author;
}
