# 基于SpringCloud的练手项目

**所用技术**

- springboot
- springcloud
- jwt
- redis
- mysql
- jpa



目前实现了登录认证，token生成等功能，在zuul模块通过gateway拦截器对请求进行拦截，验证相关权限，通过后访问具体模块
