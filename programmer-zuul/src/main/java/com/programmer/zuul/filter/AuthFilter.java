package com.programmer.zuul.filter;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.interfaces.Claim;
import com.programmer.zuul.utils.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Map;

/**
 * 网关权限拦截
 *
 * @author dengweiping
 * @date 2021/1/6 14:15
 */
@Component
public class AuthFilter extends AbstractGatewayFilterFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthFilter.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            //登录接口，直接放行
            if (request.getPath().equals(JWTUtil.LOGIN_URL)) {
                return chain.filter(exchange);
            }

            ServerHttpResponse response = exchange.getResponse();
            if (request.getCookies().containsKey("token")) {
                String clientToken = request.getCookies().get("token").get(0).getValue();
                try {
                    Map<String, Claim> data = JWTUtil.parseToken(clientToken);
                    String user = data.get("user").asString();
                    Map<String, Object> userMap = JSON.parseObject(user);

                    //redis验证
             /*       String serverToken = redisTemplate.opsForValue().get("token") + "";
                    if (!clientToken.equals(serverToken)) {
                        response.setStatusCode(HttpStatus.UNAUTHORIZED);
                        return response.setComplete();
                    }*/

                    //携带token请求其他业务接口
                    // TODO 将用户信息存放在请求header中传递给下游业务
                    ServerHttpRequest.Builder mutate = request.mutate();
                    mutate.header("userId", userMap.get("userId").toString());
                    mutate.header("userName", userMap.get("userName").toString());
                    ServerHttpRequest buildRequest = mutate.build();

                    //todo 如果响应中需要放数据，也可以放在response的header中
                    //ServerHttpResponse response = exchange.getResponse();
                    //response.getHeaders().add("new_token","token_value");
                    return chain.filter(exchange.mutate().request(buildRequest).build());
                } catch (Exception e) {
                    e.printStackTrace();
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                }

            } else {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        };
    }
}
