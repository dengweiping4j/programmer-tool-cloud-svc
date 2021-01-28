package com.programmer.blog.controller;

import com.programmer.blog.domain.BlogPaper;
import com.programmer.blog.domain.DemoData;
import com.programmer.blog.domain.Pagination;
import com.programmer.blog.service.MdBlogService;
import com.programmer.blog.util.ExcelUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

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
    public ResponseEntity<Object> query(@RequestBody BlogPaper blogPaper,
                                        @Valid Pagination pagination) {

        ExcelUtil excelUtil = new ExcelUtil();
        List<DemoData> data = new ArrayList<>();
        DemoData demoData = new DemoData();
        demoData.setDate("2021-01-01");
        demoData.setName1("120");
        demoData.setName2("240");
        data.add(demoData);
        data.add(demoData);
        data.add(demoData);
        excelUtil.simpleWrite(data);

        //Map<String, Object> result = mdBlogService.query(blogPaper, pagination);
        //return ResponseEntity.ok(result);

        return ResponseEntity.ok(null);
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
    public ResponseEntity<Object> create(@RequestBody BlogPaper blogPaper, HttpServletRequest request) {
        return new ResponseEntity<>(mdBlogService.createMdBlog(blogPaper), HttpStatus.OK);
    }

}
