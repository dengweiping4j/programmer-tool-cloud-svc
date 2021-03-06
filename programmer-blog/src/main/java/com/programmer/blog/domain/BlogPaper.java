package com.programmer.blog.domain;

import lombok.Data;

import java.util.Date;

/**
 * 博客文档实体
 *
 * @author dengweiping
 * @date 2021/1/19 14:30
 */
@Data
public class BlogPaper {
    private String id;
    private String author;
    private String title;
    private String description;
    private String content;
    private Date createDate;
}
