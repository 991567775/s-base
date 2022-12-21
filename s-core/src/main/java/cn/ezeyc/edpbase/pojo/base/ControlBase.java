package cn.ezeyc.edpbase.pojo.base;

import cn.ezeyc.edpcommon.annotation.framework.configuration;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * ControlBase：
 *
 * @author: Administrator
 * @date: 2020年12月2日, 0002 16:31:24
 */
@configuration
public class ControlBase {
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;
    @Autowired
    protected HttpServlet servlet;
    @Autowired
    protected HttpSession session;


}
