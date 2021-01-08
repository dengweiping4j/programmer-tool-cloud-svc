package com.programmer.zuul.filter;

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

/**
 * 权限校验
 *
 * @author dengweiping
 * @date 2021/1/6 14:15
 */
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // 1.从Headers中获取token
            String key = "x-auth-token";
            if (!request.getHeaders().containsKey(key)) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            } else {
                Object severToken = redisTemplate.opsForValue().get(key);
                //token不合法或已过期
                if (severToken == null) {
                    response.setStatusCode(HttpStatus.FORBIDDEN);
                    return response.setComplete();
                }
            }

            return chain.filter(exchange);
        };
    }
}
