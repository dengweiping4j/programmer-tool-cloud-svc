package com.programmer.blog.service;

import com.programmer.blog.domain.BlogPaper;
import com.programmer.blog.domain.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * md文档业务逻辑类
 *
 * @author dengweiping
 * @date 2021/1/19 14:36
 */
@Service
public class MdBlogService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MdBlogService.class);

    @Autowired
    private DocumentService documentService;

    public Result createMdBlog() {
        BlogPaper blog = new BlogPaper();
        blog.setId("1001");
        blog.setTitle("测试文档");
        blog.setBlogAbstract("这个文档用来测试一下");
        blog.setAuthor("邓卫平");
        blog.setContent("");
        blog.setCreateDate("2021-01-19");

        return documentService.addDocument(blog);
    }

    public BlogPaper getBlogById(String id) {
        return documentService.getDocument(id);
    }
}
