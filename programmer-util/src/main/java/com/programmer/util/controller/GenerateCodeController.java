package com.programmer.util.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 代码生成控制器
 *
 * @author dengweiping
 * @date 2021/1/8 15:04
 */
@RestController
@RequestMapping("/api/generate")
public class GenerateCodeController {

    /**
     * 读取redis
     *
     * @param key redis的键
     * @return 响应类Result
     */
    @ApiOperation(value = "解析md文档", notes = "解析md文档", produces = "application/json")
    @ApiResponses({@ApiResponse(code = 200, message = "查询成功")})
    @RequestMapping(value = "/{key}", method = RequestMethod.GET)
    public ResponseEntity<Object> lineageAnalysis(@PathVariable("key") String key) {
        return ResponseEntity.ok(null);
    }

}
