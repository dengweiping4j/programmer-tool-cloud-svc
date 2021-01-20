package com.programmer.blog.controller;

import com.programmer.blog.domain.BlogPaper;
import com.programmer.blog.domain.Pagination;
import com.programmer.blog.domain.dto.PaginationSuccessDTO;
import com.programmer.blog.service.MdBlogService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * md文档控制器类
 *
 * @author dengweiping
 * @date 2021/1/19 14:40
 */
@RestController
@RequestMapping("/api/blog")
public class MdBlogController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MdBlogController.class);

    @Autowired
    private MdBlogService mdBlogService;

    /**
     * 获取文档
     *
     * @return
     */
    @ApiOperation(value = "获取文档", notes = "获取文档", produces = "application/json")
    @ApiResponses({@ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 204, message = "没有内容")})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> get(@PathVariable("id") String id) {
        return new ResponseEntity<>(mdBlogService.getBlogById(id), HttpStatus.OK);
    }

    /**
     * 分页查询
     *
     * @param blogPaper
     * @param pagination
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询", produces = "application/json")
    @ApiResponses({@ApiResponse(code = 200, message = "查询成功")})
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public ResponseEntity<PaginationSuccessDTO<BlogPaper>> query(@RequestBody BlogPaper blogPaper,
                                                                         @Valid Pagination pagination) {
        Pageable pageable = PageRequest.of(pagination.getPage() - 1, pagination.getPageSize());
        Page<BlogPaper> data = mdBlogService.query(blogPaper, pageable);
        return ResponseEntity.ok(new PaginationSuccessDTO<>(data));
    }

    /**
     * 创建文档
     *
     * @return
     */
    @ApiOperation(value = "创建文档", notes = "创建文档", produces = "application/json")
    @ApiResponses({@ApiResponse(code = 200, message = "新增成功"),
            @ApiResponse(code = 204, message = "没有内容")})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Object> create(@RequestBody BlogPaper blogPaper) {
        LOGGER.info("create blog data: {}", blogPaper);
        return new ResponseEntity<>(mdBlogService.createMdBlog(blogPaper), HttpStatus.OK);
    }

}
