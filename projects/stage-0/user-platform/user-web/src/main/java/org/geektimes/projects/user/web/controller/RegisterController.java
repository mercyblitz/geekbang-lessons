package org.geektimes.projects.user.web.controller;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.service.UserService;
import org.geektimes.projects.user.service.impl.UserServiceImpl;
import org.geektimes.web.mvc.controller.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * @program: geekbang-lessons
 * @description:
 * @create: 2021-03-04 21:58
 */
@Path("/registerAction" )
public class RegisterController implements PageController {

    UserService userService =new UserServiceImpl();

    @POST
    @Path("/register")
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        User user = new User();
        user.setName(request.getParameter("name"));
        user.setPassword(request.getParameter("password"));
        user.setPhoneNumber(request.getParameter("mobile"));
        user.setEmail(request.getParameter("email"));
        if (userService.register(user)) {
            request.setAttribute("result", "注册成功");
            return "success.jsp";
        } else {
            request.setAttribute("result","注册失败");
            return "fail.jsp";
        }
    }


}
