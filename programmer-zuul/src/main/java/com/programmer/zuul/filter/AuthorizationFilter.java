package com.programmer.zuul.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 身份认证
 *
 * @author dengweiping
 * @date 2021/1/6 13:53
 */
@Slf4j
@Component
public class AuthorizationFilter extends AbstractGatewayFilterFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationFilter.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            String uri = request.getURI().getPath();
            String method = request.getMethodValue();

            // 1.从AuthenticationFilter中获取userName
            String key = "x-auth-token";
            String userName = Objects.requireNonNull(request.getHeaders().get(key)).get(0);

            // 2.验证权限
            if (!checkPermissions(userName, uri, method)) {
                LOGGER.info("用户：{}, 没有权限", userName);
                response.setStatusCode(HttpStatus.FORBIDDEN);
                return response.setComplete();
            }

            return chain.filter(exchange);
        };
    }

    public boolean checkPermissions(String userName, String uri, String method) {
        String key = String.format("login:permission:%s", userName);
        String hashKey = String.format("%s:%s", method, uri);

        if (redisTemplate.opsForHash().hasKey(key, hashKey)) {
            return true;
        }

        String allKey = "login:permission:all";
        // 权限列表中没有则通过
        return !redisTemplate.opsForHash().hasKey(allKey, hashKey);
    }
}
