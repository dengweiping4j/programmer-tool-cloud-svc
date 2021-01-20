package com.programmer.blog.service;

import com.programmer.blog.domain.BlogPaper;
import com.programmer.blog.domain.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * md文档业务逻辑类
 *
 * @author dengweiping
 * @date 2021/1/19 14:36
 */
@Service
public class MdBlogService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MdBlogService.class);
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

    @Autowired
    private DocumentService documentService;

    public Result createMdBlog(BlogPaper blog) {
        blog.setId(UUID.randomUUID().toString());
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

    public Page<BlogPaper> query(BlogPaper blogPaper, Pageable pageable) {
        documentService.queryDocument();
        return null;
    }
}
