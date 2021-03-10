package org.geektimes.projects.user.web.controller;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.service.UserService;
import org.geektimes.web.mvc.controller.PageController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;


/**
 * 用户 控制器
 *
 * @author wenhai
 * @date   2021/3/3
 */
@Path("/user")
public class UserController implements PageController {


    @Resource(name = "bean/UserService")
    private UserService userService;


    @POST
    @Override
    @Path("/register")
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {

        String name = request.getParameter("name");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhoneNumber(phoneNumber);
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        try {
            boolean register = userService.register(user);
            response.getWriter().write(register ? "注册成功" : "注册失败");
        } catch (Exception e) {
            response.getWriter().write(e.getMessage());
        }
        response.getWriter().flush();
        response.getWriter().close();
        return null;
    }

}
