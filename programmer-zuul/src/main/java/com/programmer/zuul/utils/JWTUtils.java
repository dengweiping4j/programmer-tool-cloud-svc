package com.programmer.zuul.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author dengweiping
 * @date 2021/1/8 16:18
 */
public class JWTUtils {

    // token 签名的秘钥，可设置到配置文件中
    private static final String SECRET_KEY = "secretKey:5xcJVrXNyQDIxK1l2RS9nw";
    // token过期时间
    public static final long TOKEN_EXPIRE_TIME = 7200 * 1000;

    /**
     * 生成jwt
     */
    public static String createJwt(String userId) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        //设置头信息
        HashMap<String, Object> header = new HashMap<>(2);
        header.put("typ", "JWT");
        header.put("alg", "HS256");
        // 生成 token：头部+载荷+签名
        return JWT.create().withHeader(header)
                .withClaim("userId", userId)
                .withExpiresAt(new Date(System.currentTimeMillis() + TOKEN_EXPIRE_TIME)).sign(algorithm);
    }

    /**
     * 验证Token
     *
     * @param token
     * @return
     * @throws Exception
     */
    public static Map<String, Claim> verifyToken(String token) throws Exception {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET_KEY)).build();
        DecodedJWT jwt = null;
        try {
            jwt = verifier.verify(token);
        } catch (Exception e) {
            throw new RuntimeException("凭证已过期，请重新登录");
        }
        return jwt.getClaims();
    }

    /**
     * 解析Token
     *
     * @param token
     * @return
     */
    public static Map<String, Claim> parseToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getClaims();
    }


}
