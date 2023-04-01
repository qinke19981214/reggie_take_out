package com.itheima.reggie.filter;


import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
* 检查用户是否完成登入,过滤器
* */


@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //匹配的工具类

    private static final AntPathMatcher PATH_MATCHER =new AntPathMatcher();




    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpServletResponse response =(HttpServletResponse)servletResponse;
        //获取请求路径
        String uri = request.getRequestURI();

        log.info("拦截请求路径: {}",uri);

        //设置不过滤白名单
        String[] urls={"/employee/login","/employee/logout","/backend/**","/front/**","/user/sendMsg","/user/login"};

        boolean check = check(uri, urls);

        //匹配白名单成功
        if (check){
            log.info("不需要拦截请求路径: {}",uri);
            filterChain.doFilter(request,response);
            return;

        }

        //4.1判断是否登入
        if (request.getSession().getAttribute("employee")!=null){
            log.info("用户已经登入id: {}",request.getSession().getAttribute("employee"));
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("employee") );
            filterChain.doFilter(request,response);
            return;

        }
        //4.2判断是否登入
        if (request.getSession().getAttribute("user")!=null){
            log.info("用户已经登入id: {}",request.getSession().getAttribute("user"));
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("user") );
            filterChain.doFilter(request,response);
            return;
        }






           log.info("用户没有登入");
            response.getWriter().write(JSON.toJSONString( R.error("NOTLOGIN")));








        log.info("请求路径: {}",request.getRequestURI());




    }

    @Override
    public void destroy() {

    }



    public boolean check(String uri, String[] urls){


        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, uri);

            if (match){
                return true;
            }
        }

        return false;





    }








}
