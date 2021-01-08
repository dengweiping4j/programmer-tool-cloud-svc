package com.programmer.zuul.service;

import com.programmer.zuul.domain.LoginUser;
import com.programmer.zuul.domain.Result;
import com.programmer.zuul.utils.JWTUtils;
import org.springframework.stereotype.Service;

/**
 * 用户认证业务逻辑类
 *
 * @author dengweiping
 * @date 2021/1/8 16:32
 */
@Service
public class AuthService {
    public Result login(LoginUser loginUser) {
        // 获取用户信息、比对密码

        //生成token
        String token = JWTUtils.createJwt(loginUser.getId());
        return Result.success(token);
    }
}
