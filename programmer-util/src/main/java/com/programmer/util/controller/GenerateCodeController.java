package com.programmer.util.controller;

import com.programmer.util.domain.dto.GeneratorParamsDTO;
import com.programmer.util.service.GeneratorService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 代码生成控制器
 *
 * @author dengweiping
 * @date 2021/1/8 15:04
 */
@RestController
@RequestMapping("/api/generator")
public class GenerateCodeController {

    @Autowired
    private GeneratorService generatorService;

    /**
     * 生成代码
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "生成代码", notes = "需要表名、包名、作者等信息", produces = "application/json")
    @ApiResponses({@ApiResponse(code = 200, message = "操作成功")})
    public void code(GeneratorParamsDTO params, HttpServletResponse response) throws IOException {
        byte[] data = generatorService.generatorCode(params);

        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"generator-code.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");

        IOUtils.write(data, response.getOutputStream());
    }

}
