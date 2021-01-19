package com.programmer.blog.controller;

import com.programmer.blog.service.MdBlogService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
     * 创建文档
     *
     * @return
     */
    @ApiOperation(value = "创建文档", notes = "创建文档", produces = "application/json")
    @ApiResponses({@ApiResponse(code = 200, message = "新增成功"),
            @ApiResponse(code = 204, message = "没有内容")})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Object> create() {
        return new ResponseEntity<>(mdBlogService.createMdBlog(), HttpStatus.OK);
    }

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

}
