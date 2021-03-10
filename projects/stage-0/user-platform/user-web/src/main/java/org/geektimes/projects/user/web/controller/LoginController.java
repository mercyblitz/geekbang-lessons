package org.geektimes.projects.user.web.controller;

import org.geektimes.web.mvc.controller.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;


/**
 * 用户登录 控制器
 *
 * @author wenhai
 * @date   2021/3/3
 */
@Path("/login")
public class LoginController implements PageController {

    @GET
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        return "/login-form.jsp";
    }
}
