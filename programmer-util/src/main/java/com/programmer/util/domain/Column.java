package com.programmer.util.domain;

import lombok.Data;

/**
 * 字段实体
 *
 * @author dengweiping
 * @date 2021/1/9 17:26
 */
@Data
public class Column {
    //列名
    private String columnName;

    //列名类型
    private String columnType;

    //列名备注
    private String columnComment;

    //是否主键
    private boolean isPrimary;
}
