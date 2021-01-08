package com.programmer.zuul.filter;//package com.dwp.filter;
//
//import com.netflix.zuul.ZuulFilter;
//import com.netflix.zuul.context.RequestContext;
//import org.apache.http.HttpStatus;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.http.HttpServletRequest;
//
///**
// * 过滤器
// *
// * @author dengweiping
// * @date 2021/1/5 11:12
// */
//@Component
//public class ApiFilter extends ZuulFilter {
//    private static Logger log = LoggerFactory.getLogger(ApiFilter.class);
//
//    @Override
//    public String filterType() {
//        return "pre";
//    }
//
//    @Override
//    public int filterOrder() {
//        return 0;
//    }
//
//    @Override
//    public boolean shouldFilter() {
//        return true;
//    }
//
//    @Override
//    public Object run() {
//        RequestContext ctx = RequestContext.getCurrentContext();
//        HttpServletRequest request = ctx.getRequest();
//        log.info(String.format("%s >>> %s", request.getMethod(), request.getRequestURL().toString()));
//        Object accessToken = request.getParameter("x-auth-token");
//        if (accessToken == null) {
//            log.warn("x-auth-token is empty");
//            ctx.setSendZuulResponse(false);
//            ctx.setResponseStatusCode(HttpStatus.SC_UNAUTHORIZED);
//
//            try {
//                ctx.getResponse().addHeader("x-auth-token", "123456");
//                ctx.getResponse().setHeader("Access-Control-Expose-Headers", "x-auth-token");
//                ctx.getResponse().getWriter().write("x-auth-token is empty");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//        log.info("ok");
//        return null;
//    }
//}
