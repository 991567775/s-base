package cn.ezeyc.edpbase.http;


import cn.ezeyc.edpcommon.enums.ResultEnum;
import cn.ezeyc.edpcommon.pojo.ResultBody;
import cn.ezeyc.edpcommon.util.Http;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.io.IOException;

/**
 * 过滤器
 * 读取post参数传递Request保留参数值
 * @author wz
 */
@WebFilter(urlPatterns = "/*",filterName = "PostFilter")
public class PostFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        try {
            if(servletRequest.getContentType()!=null&&servletRequest.getContentType().contains("multipart/form-data")){
                filterChain.doFilter(servletRequest, servletResponse);
            }else{
                ServletRequest requestWrapper = null;
                if(servletRequest instanceof HttpServletRequest) {
                    requestWrapper = new RequestWrapper((HttpServletRequest) servletRequest);
                }
                if(requestWrapper == null) {
                    filterChain.doFilter(servletRequest, servletResponse);
                } else {
                    filterChain.doFilter(requestWrapper, servletResponse);
                }
            }

        } catch (IOException e) {
            Http.returnJson((HttpServletResponse) servletResponse,ResultBody.failed(ResultEnum.verify).setMessage(e.getCause().getMessage()));
        } catch (ServletException e) {
            Http.returnJson((HttpServletResponse) servletResponse,ResultBody.failed(ResultEnum.verify).setMessage(e.getCause().getMessage()));
        }
    }


    @Override
    public void destroy() {

    }
}