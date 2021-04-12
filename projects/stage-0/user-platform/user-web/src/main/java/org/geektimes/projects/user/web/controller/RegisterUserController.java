package org.geektimes.projects.user.web.controller;

import org.geektimes.context.ClassicComponentContext;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.service.UserService;
import org.geektimes.web.mvc.controller.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * 注册用户
 */
@Path("")
public class RegisterUserController implements PageController {

    @GET
    @Path("/registerUser")
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        UserService userService = ClassicComponentContext.getInstance().getComponent("bean/My-UserService");

        User user = new User();

        user.setId(0L);
        user.setName(request.getParameter("name"));
        user.setPassword(request.getParameter("password"));
        user.setEmail(request.getParameter("email"));
        user.setPhoneNumber(request.getParameter("phoneNumber"));

        userService.register(user);
        request.getServletContext().log("用户注册 user:" + userService.queryUserById(0L));
        return "WEB-INF/success.jsp";
    }

}
