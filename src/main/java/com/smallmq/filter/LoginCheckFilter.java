package com.smallmq.filter;

import com.alibaba.fastjson.JSON;
import com.smallmq.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    // 路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * 过滤器初始化
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        log.info("LoginCheckFilter doFilter" + request.getRequestURI());

        // 放行urls
        String[] urls = new String[]{
                "/front/page/login.html",
                "/employee/login",
                "/employee/register",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/login",
                "/user/sendMsg",
        };
        // 校验是否放行
        boolean check = Check(urls, request.getRequestURI());
        if (check) {
            filterChain.doFilter(request, response);
            log.info("check pass");
            return;
        }
        // 校验是否登录
        if(request.getSession().getAttribute("employee")!=null){
            filterChain.doFilter(request, response);
            log.info("employee pass");
            return;
        }
        // 校验用户
        if(request.getSession().getAttribute("user")!=null){
            filterChain.doFilter(request, response);
            log.info("pass user");
            return;
        }
        // 未登录返回NOTLOGIN
        response.getWriter().write(JSON.toJSONString(Response.error("NOTLOGIN")));
        log.info("NOTLOGIN");
        return;


    }

    /**
     * Check if the request matches the filter pattern.
     * @param urls
     * @param requestURI
     */
    public boolean Check(String[] urls, String requestURI) {
        for (String url : urls) {
            if (PATH_MATCHER.match(url, requestURI)) {
                return true;
            }
        }
        return false;
    }
}
