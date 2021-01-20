package com.programmer.blog.service;

import com.programmer.blog.domain.BlogPaper;
import com.programmer.blog.domain.Pagination;
import com.programmer.blog.domain.Result;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * md文档业务逻辑类
 *
 * @author dengweiping
 * @date 2021/1/19 14:36
 */
@Service
public class MdBlogService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MdBlogService.class);
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

    @Autowired
    private DocumentService documentService;

    public Result createMdBlog(BlogPaper blog) {
        blog.setId(UUID.randomUUID().toString());
        blog.setAuthor("邓卫平");
        blog.setCreateDate(sdf.format(new Date()));
        return documentService.addDocument(blog);
    }

    public Result getBlogById(String id) {
        BlogPaper blogPaper = documentService.getDocument(id);
        if (blogPaper == null) {
            return Result.error("查询失败");
        }

        return Result.success(blogPaper);
    }

    public Map<String, Object> query(BlogPaper queryDTO, Pagination pagination) {
        Map<String, Object> result = null;
        try {
            // 转换
            QueryBuilder queryBuilder = null;
            if (queryDTO.getContent() != null && !queryDTO.getContent().isEmpty()) {
                queryBuilder = QueryBuilders.multiMatchQuery(queryDTO.getContent(), "content");
            }

            List<String> indices=new ArrayList<>();
            indices.add("blog");
            result = documentService.search(indices, pagination, queryBuilder, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
