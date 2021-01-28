package com.programmer.blog.controller;

import com.programmer.blog.domain.ElasticsearchIndex;
import com.programmer.blog.service.ElasticsearchService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * elasticsearch控制器类
 *
 * @author dengweiping
 * @date 2021/1/23 15:56
 */
@RestController
@RequestMapping("/api/elasticsearch")
public class ElasticsearchController {

    @Autowired
    private ElasticsearchService elasticSearchService;

    /**
     * 创建索引
     *
     * @return
     */
    @ApiOperation(value = "创建索引", notes = "创建索引", produces = "application/json")
    @ApiResponses({@ApiResponse(code = 200, message = "新增成功"),
            @ApiResponse(code = 204, message = "没有内容")})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Object> create(@RequestBody ElasticsearchIndex elasticSearchIndex) {
        return new ResponseEntity<>(elasticSearchService.createIndex(elasticSearchIndex), HttpStatus.OK);
    }
}
