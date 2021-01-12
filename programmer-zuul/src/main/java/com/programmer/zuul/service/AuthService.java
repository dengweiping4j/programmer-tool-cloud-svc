package com.programmer.zuul.service;

import com.alibaba.fastjson.JSON;
import com.programmer.zuul.domain.LoginUser;
import com.programmer.zuul.domain.Result;
import com.programmer.zuul.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户认证业务逻辑类
 *
 * @author dengweiping
 * @date 2021/1/8 16:32
 */
@Service
public class AuthService {
    @Autowired
    private RedisTemplate redisTemplate;

    public Result login(LoginUser loginUser, ServerWebExchange serverWebExchange) {
        // 获取用户信息、比对密码

        //生成token
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("userId", "123456");
        tokenMap.put("userName", loginUser.getUserName());
        String token = JwtUtil.createJwt(JSON.toJSONString(tokenMap));

       /* //保存到redis
        redisTemplate.opsForValue().set("token", token);*/

        //保存token到cookie
        ResponseCookie responseCookie = ResponseCookie.from("token", token).path("/").maxAge(Duration.ofMinutes(30)).build();
        serverWebExchange.getResponse().addCookie(responseCookie);

        return Result.success(token);
    }
}
