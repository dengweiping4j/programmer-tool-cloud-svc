package com.programmer.zuul.controller;

import com.programmer.zuul.domain.LoginUser;
import com.programmer.zuul.service.AuthService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户认证控制器
 *
 * @author dengweiping
 * @date 2021/1/8 16:20
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    /**
     * 登录
     *
     * @param loginUser
     * @return
     */
    @ApiOperation(value = "登录", notes = "登录", produces = "application/json")
    @ApiResponses({@ApiResponse(code = 200, message = "新增成功"),
            @ApiResponse(code = 204, message = "没有内容")})
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<Object> login(@RequestBody LoginUser loginUser) {
        LOGGER.debug("REST request to login : {}", loginUser);
        return new ResponseEntity<>(authService.login(loginUser), HttpStatus.OK);
    }
}
