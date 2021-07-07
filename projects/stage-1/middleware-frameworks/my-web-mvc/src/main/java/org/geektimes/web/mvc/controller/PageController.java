package org.geektimes.web.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 页面控制器，负责服务端页面渲染
 *
 * @since 1.0
 */
public interface PageController extends Controller {

    /**
     * @param request  HTTP 请求
     * @param response HTTP 相应
     * @return 视图地址路径
     * @throws Throwable 异常发生时
     */
    String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable;
}
